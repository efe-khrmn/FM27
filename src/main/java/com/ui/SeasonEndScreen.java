package com.ui;

import com.engine.GameState;
import com.engine.SeasonResult;
import com.engine.TeamStanding;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.util.List;

public class SeasonEndScreen {

    private ScreenManager manager;
    private BorderPane root;

    public SeasonEndScreen(ScreenManager manager) {
        this.manager = manager;
        build();
    }

    private void build() {
        root = new BorderPane();
        root.setStyle("-fx-background-color: " + UIStyles.BG_DARK + ";");
        root.setTop(buildNavBar());

        VBox center = new VBox(24);
        center.setPadding(new Insets(40));
        center.setAlignment(Pos.TOP_CENTER);
        center.setStyle("-fx-background-color: " + UIStyles.BG_DARK + ";");

        // Champion
        List<TeamStanding> standings = GameState.getInstance().getLeague().getStandings();
        SeasonResult seasonResult = new SeasonResult(standings);
        TeamStanding champion = seasonResult.getChampion();

        // Champion card (themed)
        VBox champCard = new VBox(10);
        champCard.setAlignment(Pos.CENTER);
        champCard.setPadding(new Insets(28));
        champCard.setStyle(
                "-fx-background-color: linear-gradient(to bottom right, " + UIStyles.BG_CARD + ", " + UIStyles.BG_PANEL + ");"
                        + "-fx-background-radius: 12;"
                        + "-fx-border-color: #f1c40f; -fx-border-width: 2; -fx-border-radius: 12;");

        Label trophy = new Label("🏆");
        trophy.setStyle("-fx-font-size: 56px;");

        Label champTitle = new Label("Season Champion");
        champTitle.setStyle("-fx-font-size: 16px; -fx-text-fill: #cfd6e4; -fx-font-weight: bold;");

        Label champName = new Label(champion != null ? champion.getTeam().getName() : "Unknown");
        champName.setStyle("-fx-font-size: 36px; -fx-font-weight: bold; -fx-text-fill: #f1c40f;");

        // Check if managed team won
        boolean playerWon = champion != null &&
                champion.getTeam().equals(GameState.getInstance().getManagedTeam());

        Label playerResult = new Label(playerWon ? "Congratulations! You won the league!" : "Better luck next season.");
        playerResult.setStyle("-fx-font-size: 16px; -fx-text-fill: " + (playerWon ? "#2ecc71" : "#e94560") + ";");

        champCard.getChildren().addAll(trophy, champTitle, champName, playerResult);

        // Final standings
        Label standingsTitle = new Label("Final Standings");
        standingsTitle.setStyle(UIStyles.SUBTITLE_STYLE);

        TableView<TeamStanding> table = new TableView<>();
        table.getStylesheets().add(
                "data:text/css," + UIStyles.TABLE_CSS.replace(" ", "%20")
                        .replace("#", "%23").replace(":", "%3A").replace("{", "%7B")
                        .replace("}", "%7D").replace(";", "%3B").replace(".", "%2E")
                        .replace("(", "%28").replace(")", "%29").replace("'", "%27")
        );
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setPrefHeight(320);

        // Rank
        TableColumn<TeamStanding, Void> rankCol = new TableColumn<>("#");
        rankCol.setMaxWidth(40);
        rankCol.setCellFactory(col -> new TableCell<TeamStanding, Void>() {
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (!empty) {
                    setText(String.valueOf(getIndex() + 1));
                    setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-alignment: center;");
                } else { setText(null); }
            }
        });

        // Team name
        TableColumn<TeamStanding, String> nameCol = new TableColumn<>("Team");
        nameCol.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getTeam().getName()));
        nameCol.setCellFactory(col -> new TableCell<TeamStanding, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (item != null && !empty) {
                    setText(item);
                    setStyle("-fx-text-fill: white; -fx-font-size: 13px; -fx-font-weight: bold;");
                } else { setText(null); }
            }
        });

        TableColumn<TeamStanding, Integer> pCol  = makeIntCol("P",  "played");
        TableColumn<TeamStanding, Integer> wCol  = makeIntCol("W",  "won");
        TableColumn<TeamStanding, Integer> dCol  = makeIntCol("D",  "drawn");
        TableColumn<TeamStanding, Integer> lCol  = makeIntCol("L",  "lost");
        TableColumn<TeamStanding, Integer> gfCol = makeIntCol("GF", "scored");
        TableColumn<TeamStanding, Integer> gaCol = makeIntCol("GA", "conceded");

        TableColumn<TeamStanding, Integer> gdCol = new TableColumn<>("GD");
        gdCol.setCellValueFactory(data ->
                new javafx.beans.property.SimpleIntegerProperty(data.getValue().getGoalDiff()).asObject());
        gdCol.setMaxWidth(60);
        gdCol.setCellFactory(col -> new TableCell<TeamStanding, Integer>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (item != null && !empty) {
                    setText(String.valueOf(item));
                    setStyle("-fx-text-fill: white; -fx-alignment: center;");
                } else { setText(null); }
            }
        });

        TableColumn<TeamStanding, Integer> ptsCol = makeIntCol("Pts", "points");
        ptsCol.setCellFactory(col -> new TableCell<TeamStanding, Integer>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (item != null && !empty) {
                    setText(String.valueOf(item));
                    setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-alignment: center;");
                } else { setText(null); }
            }
        });

        boolean isVolleyball = "Volleyball".equalsIgnoreCase(
                GameState.getInstance().getSport().getSportName());
        if (isVolleyball) {
            table.getColumns().addAll(rankCol, nameCol, pCol, wCol, lCol, gfCol, gaCol, gdCol, ptsCol);
        } else {
            table.getColumns().addAll(rankCol, nameCol, pCol, wCol, dCol, lCol, gfCol, gaCol, gdCol, ptsCol);
        }

        table.getItems().addAll(standings);

        // highlight managed team
        table.setRowFactory(tv -> new TableRow<TeamStanding>() {
            @Override
            protected void updateItem(TeamStanding item, boolean empty) {
                super.updateItem(item, empty);
                if (item != null && item.getTeam().equals(GameState.getInstance().getManagedTeam())) {
                    setStyle("-fx-background-color: #1a4a8a;");
                } else {
                    setStyle("");
                }
            }
        });
        // Buttons
        HBox btnBox = new HBox(16);
        btnBox.setAlignment(Pos.CENTER);

        Button newSeasonBtn = new Button("New Season");
        newSeasonBtn.setStyle(UIStyles.BTN_PRIMARY);
        newSeasonBtn.setMinWidth(180);
        newSeasonBtn.setOnAction(e -> manager.showStartScreen());

        Button quitBtn = new Button("Quit to Main Menu");
        quitBtn.setStyle(UIStyles.BTN_SECONDARY);
        quitBtn.setMinWidth(180);
        quitBtn.setOnAction(e -> manager.showStartScreen());

        btnBox.getChildren().addAll(newSeasonBtn, quitBtn);

        center.getChildren().addAll(champCard, standingsTitle, table, btnBox);
        root.setCenter(center);
    }

    private TableColumn<TeamStanding, Integer> makeIntCol(String title, String property) {
        TableColumn<TeamStanding, Integer> col = new TableColumn<>(title);
        col.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>(property));
        col.setMaxWidth(60);
        col.setCellFactory(c -> new TableCell<TeamStanding, Integer>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (item != null && !empty) {
                    setText(String.valueOf(item));
                    setStyle("-fx-text-fill: white; -fx-alignment: center;");
                } else { setText(null); }
            }
        });
        return col;
    }

    private HBox buildNavBar() {
        HBox nav = new HBox();
        nav.setPadding(new Insets(16, 24, 16, 24));
        nav.setStyle("-fx-background-color: " + UIStyles.BG_PANEL + ";");
        nav.setAlignment(Pos.CENTER_LEFT);
        Label title = new Label("Season End");
        title.setStyle(UIStyles.TITLE_STYLE);
        nav.getChildren().add(title);
        return nav;
    }

    public BorderPane getRoot() { return root; }
}
