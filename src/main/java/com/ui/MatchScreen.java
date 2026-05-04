package com.ui;

import com.engine.GameState;
import com.interfaces.IMatch;
import com.interfaces.IPlayer;
import com.interfaces.ITeam;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.util.List;

public class MatchScreen {

    private ScreenManager manager;
    private BorderPane root;
    private IMatch match;
    private boolean interactionEnabled = false;
    private ITeam homeTeam;
    private ITeam awayTeam;

    public MatchScreen(ScreenManager manager) {
        this.manager = manager;
        build();
    }

    private void build() {
        root = new BorderPane();
        root.setStyle("-fx-background-color: " + UIStyles.BG_DARK + ";");
        root.setTop(buildNavBar());

        ITeam home = GameState.getInstance().getManagedTeam();
        // find opponent from schedule
        ITeam away = findOpponent(home);

        match = GameState.getInstance().getSport().createMatch(home, away);

        VBox center = new VBox(20);
        center.setPadding(new Insets(24));
        center.setAlignment(Pos.TOP_CENTER);

        // Score display
        HBox scoreBox = new HBox(32);
        scoreBox.setAlignment(Pos.CENTER);
        scoreBox.setStyle(UIStyles.CARD_STYLE + " -fx-padding: 24;");

        Label homeLabel = new Label(home.getName());
        homeLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: white;");

        Label scoreLabel = new Label("0 - 0");
        scoreLabel.setStyle("-fx-font-size: 36px; -fx-font-weight: bold; -fx-text-fill: " + UIStyles.ACCENT_BLUE + ";");

        Label awayLabel = new Label(away != null ? away.getName() : "TBD");
        awayLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: white;");

        scoreBox.getChildren().addAll(homeLabel, scoreLabel, awayLabel);

        // Segment label
        Label segmentLabel = new Label("Segment: 0 / " + GameState.getInstance().getSport().getSegmentCount());
        segmentLabel.setStyle(UIStyles.SUBTITLE_STYLE);

        // Events log
        TextArea eventLog = new TextArea();
        eventLog.setStyle("-fx-background-color: #0f3460; -fx-text-fill: white; -fx-font-size: 12px; -fx-control-inner-background: #0f3460;");
        eventLog.setEditable(false);
        eventLog.setPrefHeight(240);

        // Buttons
        HBox btnBox = new HBox(16);
        btnBox.setAlignment(Pos.CENTER);

        Button simBtn = new Button("Simulate Next Segment");
        simBtn.setStyle(UIStyles.BTN_PRIMARY);

        Button subBtn = new Button("Substitution / Tactic");
        subBtn.setStyle(UIStyles.BTN_SECONDARY);
        subBtn.setDisable(true);

        Button endBtn = new Button("Finish Match");
        endBtn.setStyle(UIStyles.BTN_DANGER);
        endBtn.setDisable(true);

        simBtn.setOnAction(e -> {
            match.simulateNextSegment();
            scoreLabel.setText(match.getCurrentScore().toString());
            segmentLabel.setText("Segment: " + match.getCurrentSegment()
                    + " / " + GameState.getInstance().getSport().getSegmentCount());

            // append events
            StringBuilder sb = new StringBuilder(eventLog.getText());
            List<Object> events = match.getEvents();
            for (int i = Math.max(0, events.size() - 10); i < events.size(); i++) {
                sb.append(events.get(i).toString()).append("\n");
            }
            eventLog.setText(sb.toString());

            if (match.isFinished()) {
                simBtn.setDisable(true);
                subBtn.setDisable(true);
                endBtn.setDisable(false);
            } else {
                // between segments — allow substitution
                subBtn.setDisable(false);
            }
        });endBtn.setOnAction(e -> {
            GameState.getInstance().setLastMatchResult(match.getResult());
            GameState.getInstance().setLastMatchScore(
                    homeTeam, match.getHomeScore(),
                    awayTeam, match.getAwayScore()
            );
            manager.showPostMatchScreen();
        });

        btnBox.getChildren().addAll(simBtn, subBtn, endBtn);
        center.getChildren().addAll(scoreBox, segmentLabel, eventLog, btnBox);
        root.setCenter(center);
    }

    private ITeam findOpponent(ITeam managedTeam) {
        int week = GameState.getInstance().getWeek();
        List<com.engine.Fixture> fixtures = GameState.getInstance()
                .getLeague().getSchedule().getWeekFixtures(week);
        for (com.engine.Fixture f : fixtures) {
            if (f.getHomeTeam().equals(managedTeam)) return f.getAwayTeam();
            if (f.getAwayTeam().equals(managedTeam)) return f.getHomeTeam();
        }
        return null;
    }

    private HBox buildNavBar() {
        HBox nav = new HBox();
        nav.setPadding(new Insets(16, 24, 16, 24));
        nav.setStyle("-fx-background-color: " + UIStyles.BG_PANEL + ";");
        nav.setAlignment(Pos.CENTER_LEFT);
        Label title = new Label("Match — Week " + GameState.getInstance().getWeek());
        title.setStyle(UIStyles.TITLE_STYLE);
        nav.getChildren().add(title);
        return nav;
    }

    public BorderPane getRoot() { return root; }
}