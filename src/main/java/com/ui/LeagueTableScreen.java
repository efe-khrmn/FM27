package com.ui;

import com.engine.GameState;
import com.engine.TeamStanding;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.util.List;

public class LeagueTableScreen {

    private ScreenManager manager;
    private BorderPane root;

    public LeagueTableScreen(ScreenManager manager) {
        this.manager = manager;
        build();
    }

    private void build() {
        root = new BorderPane();
        root.setStyle("-fx-background-color: " + UIStyles.BG_DARK + ";");
        root.setTop(buildNavBar("League Table"));

        VBox center = new VBox(16);
        center.setPadding(new Insets(24));
        center.setStyle("-fx-background-color: " + UIStyles.BG_DARK + ";");

        Label weekLabel = new Label("Week " + GameState.getInstance().getWeek());
        weekLabel.setStyle(UIStyles.SUBTITLE_STYLE);

        TableView<TeamStanding> table = new TableView<>();
        table.getStylesheets().add(
                "data:text/css," + UIStyles.TABLE_CSS.replace(" ", "%20")
                        .replace("#", "%23").replace(":", "%3A").replace("{", "%7B")
                        .replace("}", "%7D").replace(";", "%3B").replace(".", "%2E")
                        .replace("(", "%28").replace(")", "%29").replace("'", "%27")
        );
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // Rank column
        TableColumn<TeamStanding, Void> rankCol = new TableColumn<>("#");
        rankCol.setMaxWidth(40);
        rankCol.setCellFactory(col -> new TableCell<TeamStanding, Void>() {
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (!empty) {
                    setText(String.valueOf(getIndex() + 1));
                    setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-alignment: center;");
                } else {
                    setText(null);
                }
            }
        });

        // Name column
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
                } else {
                    setText(null);
                }
            }
        });

        TableColumn<TeamStanding, Integer> pCol   = makeIntCol("P",   "played");
        TableColumn<TeamStanding, Integer> wCol   = makeIntCol("W",   "won");
        TableColumn<TeamStanding, Integer> dCol   = makeIntCol("D",   "drawn");
        TableColumn<TeamStanding, Integer> lCol   = makeIntCol("L",   "lost");
        TableColumn<TeamStanding, Integer> gfCol  = makeIntCol("GF",  "scored");
        TableColumn<TeamStanding, Integer> gaCol  = makeIntCol("GA",  "conceded");

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
                } else {
                    setText(null);
                }
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
                } else {
                    setText(null);
                }
            }
        });

        table.getColumns().addAll(rankCol, nameCol, pCol, wCol, dCol, lCol, gfCol, gaCol, gdCol, ptsCol);

        List<TeamStanding> standings = GameState.getInstance().getLeague().getStandings();
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

        center.getChildren().addAll(weekLabel, table);
        root.setCenter(center);
        root.setBottom(buildBottomNav());
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
                } else {
                    setText(null);
                }
            }
        });
        return col;
    }

    private HBox buildNavBar(String title) {
        HBox nav = new HBox();
        nav.setPadding(new Insets(16, 24, 16, 24));
        nav.setStyle("-fx-background-color: " + UIStyles.BG_PANEL + ";");
        nav.setAlignment(Pos.CENTER_LEFT);

        Label titleLabel = new Label(title);
        titleLabel.setStyle(UIStyles.TITLE_STYLE);

        Label teamLabel = new Label("  |  " + GameState.getInstance().getManagedTeam().getName());
        teamLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: " + UIStyles.ACCENT_BLUE + ";");

        nav.getChildren().addAll(titleLabel, teamLabel);
        return nav;
    }

    private HBox buildBottomNav() {
        HBox bottom = new HBox(12);
        bottom.setPadding(new Insets(16, 24, 16, 24));
        bottom.setStyle("-fx-background-color: " + UIStyles.BG_PANEL + ";");
        bottom.setAlignment(Pos.CENTER);

        Button scheduleBtn = new Button("Schedule");
        scheduleBtn.setStyle(UIStyles.BTN_SECONDARY);
        scheduleBtn.setOnAction(e -> manager.showScheduleScreen());

        Button squadBtn = new Button("Squad");
        squadBtn.setStyle(UIStyles.BTN_SECONDARY);
        squadBtn.setOnAction(e -> manager.showSquadScreen());

        Button trainingBtn = new Button("Training");
        trainingBtn.setStyle(UIStyles.BTN_PRIMARY);
        trainingBtn.setOnAction(e -> manager.showTrainingScreen());

        Button matchBtn = new Button("Pre-Match");
        matchBtn.setStyle(UIStyles.BTN_PRIMARY);
        matchBtn.setOnAction(e -> manager.showPreMatchScreen());

        Button saveBtn = new Button("Save");
        saveBtn.setStyle(UIStyles.BTN_SECONDARY);
        saveBtn.setOnAction(e -> {
            try { com.engine.SaveManager.save(); } catch (Exception ex) { ex.printStackTrace(); }
        });

        bottom.getChildren().addAll(scheduleBtn, squadBtn, trainingBtn, matchBtn, saveBtn);
        return bottom;
    }

    public BorderPane getRoot() { return root; }
}