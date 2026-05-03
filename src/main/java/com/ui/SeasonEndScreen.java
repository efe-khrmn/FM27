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

        // Champion
        List<TeamStanding> standings = GameState.getInstance().getLeague().getStandings();
        SeasonResult seasonResult = new SeasonResult(standings);
        TeamStanding champion = seasonResult.getChampion();

        Label champTitle = new Label("Season Champion");
        champTitle.setStyle(UIStyles.SUBTITLE_STYLE);

        Label champName = new Label(champion != null ? champion.getTeam().getName() : "Unknown");
        champName.setStyle("-fx-font-size: 36px; -fx-font-weight: bold; -fx-text-fill: #f1c40f;");

        // Check if managed team won
        boolean playerWon = champion != null &&
                champion.getTeam().equals(GameState.getInstance().getManagedTeam());

        Label playerResult = new Label(playerWon ? "🏆 Congratulations! You won the league!" : "Better luck next season.");
        playerResult.setStyle("-fx-font-size: 18px; -fx-text-fill: " + (playerWon ? "#2ecc71" : "#e74c3c") + ";");

        // Final standings
        Label standingsTitle = new Label("Final Standings");
        standingsTitle.setStyle(UIStyles.SUBTITLE_STYLE);

        TableView<TeamStanding> table = new TableView<>();
        table.setStyle(UIStyles.TABLE_STYLE);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setPrefHeight(300);

        TableColumn<TeamStanding, String> nameCol = new TableColumn<>("Team");
        nameCol.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getTeam().getName()));

        TableColumn<TeamStanding, Integer> ptsCol = new TableColumn<>("Pts");
        ptsCol.setCellValueFactory(data ->
                new javafx.beans.property.SimpleIntegerProperty(data.getValue().getPoints()).asObject());

        TableColumn<TeamStanding, Integer> wCol = new TableColumn<>("W");
        wCol.setCellValueFactory(data ->
                new javafx.beans.property.SimpleIntegerProperty(data.getValue().getWon()).asObject());

        TableColumn<TeamStanding, Integer> gdCol = new TableColumn<>("GD");
        gdCol.setCellValueFactory(data ->
                new javafx.beans.property.SimpleIntegerProperty(data.getValue().getGoalDiff()).asObject());

        table.getColumns().addAll(nameCol, ptsCol, wCol, gdCol);
        table.getItems().addAll(standings);
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

        center.getChildren().addAll(champTitle, champName, playerResult, standingsTitle, table, btnBox);
        root.setCenter(center);
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
