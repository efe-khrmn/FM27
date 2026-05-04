package com.ui;

import com.engine.GameState;
import com.interfaces.ITeam;
import com.interfaces.ITrainingSession;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.util.List;

public class TrainingScreen {

    private ScreenManager manager;
    private BorderPane root;

    public TrainingScreen(ScreenManager manager) {
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

        ITeam team = GameState.getInstance().getManagedTeam();
        ITrainingSession training = GameState.getInstance().getSport().createTraining(team);

        int done = GameState.getInstance().getTrainingsThisWeek();
        int max = GameState.MAX_TRAININGS_PER_WEEK;
        int remaining = Math.max(0, max - done);

        Label infoLabel = new Label("Select training focus (" + remaining + "/" + max + " sessions left this week):");
        infoLabel.setStyle(UIStyles.SUBTITLE_STYLE);

        // Training type buttons
        VBox typeBox = new VBox(12);
        typeBox.setAlignment(Pos.CENTER);
        typeBox.setMaxWidth(400);

        TextArea resultArea = new TextArea();
        resultArea.setStyle(
                "-fx-background-color: #0f3460;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 12px;" +
                        "-fx-control-inner-background: #0f3460;"
        );
        resultArea.setEditable(false);
        resultArea.setPrefHeight(300);
        resultArea.setVisible(false);

        Label resultTitle = new Label("Training Results:");
        resultTitle.setStyle(UIStyles.SUBTITLE_STYLE);
        resultTitle.setVisible(false);

        for (String type : training.getTrainingTypes()) {
            Button btn = new Button(type + " Training");
            btn.setStyle(UIStyles.BTN_PRIMARY);
            btn.setMinWidth(300);
            btn.setOnAction(e -> {
                if (GameState.getInstance().getTrainingsThisWeek() >= GameState.MAX_TRAININGS_PER_WEEK) {
                    resultArea.setText("You have already used both training sessions this week.");
                    resultArea.setVisible(true);
                    resultTitle.setVisible(true);
                    typeBox.setDisable(true);
                    return;
                }
                training.runTraining(type);
                GameState.getInstance().incrementTrainingsThisWeek();
                List<Object> results = training.getResults();
                StringBuilder sb = new StringBuilder();
                sb.append("Sessions used this week: ")
                        .append(GameState.getInstance().getTrainingsThisWeek())
                        .append("/").append(GameState.MAX_TRAININGS_PER_WEEK).append("\n\n");
                for (Object r : results) sb.append(r.toString()).append("\n");
                resultArea.setText(sb.toString());
                resultArea.setVisible(true);
                resultTitle.setVisible(true);
                if (GameState.getInstance().getTrainingsThisWeek() >= GameState.MAX_TRAININGS_PER_WEEK) {
                    typeBox.setDisable(true);
                }
            });
            if (remaining <= 0) btn.setDisable(true);
            typeBox.getChildren().add(btn);
        }
        if (remaining <= 0) {
            typeBox.setDisable(true);
            resultArea.setText("You have already used both training sessions this week. Play your match to advance.");
            resultArea.setVisible(true);
            resultTitle.setVisible(true);
        }

        Button doneBtn = new Button("Done — Back to League Table");
        doneBtn.setStyle(UIStyles.BTN_SECONDARY);
        doneBtn.setOnAction(e -> manager.showLeagueTableScreen());

        center.getChildren().addAll(infoLabel, typeBox, resultTitle, resultArea, doneBtn);
        root.setCenter(center);
    }

    private HBox buildNavBar() {
        HBox nav = new HBox();
        nav.setPadding(new Insets(16, 24, 16, 24));
        nav.setStyle("-fx-background-color: " + UIStyles.BG_PANEL + ";");
        nav.setAlignment(Pos.CENTER_LEFT);
        Label title = new Label("Training — Week " + GameState.getInstance().getWeek());
        title.setStyle(UIStyles.TITLE_STYLE);
        nav.getChildren().add(title);
        return nav;
    }

    public BorderPane getRoot() { return root; }
}
