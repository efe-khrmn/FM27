package com.ui;

import com.engine.GameState;
import com.interfaces.IMatch;
import com.interfaces.IPlayer;
import com.interfaces.ITeam;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MatchScreen {

    private ScreenManager manager;
    private BorderPane root;
    private IMatch match;
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

        homeTeam = GameState.getInstance().getManagedTeam();
        ITeam away = findOpponent(homeTeam);
        awayTeam = away;

        match = GameState.getInstance().getSport().createMatch(homeTeam, away);

        VBox center = new VBox(20);
        center.setPadding(new Insets(24));
        center.setAlignment(Pos.TOP_CENTER);

        HBox scoreBox = new HBox(32);
        scoreBox.setAlignment(Pos.CENTER);
        scoreBox.setStyle(UIStyles.CARD_STYLE + " -fx-padding: 24;");

        Label homeLabel = new Label(homeTeam.getName());
        homeLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: white;");

        Label scoreLabel = new Label("0 - 0");
        scoreLabel.setStyle("-fx-font-size: 36px; -fx-font-weight: bold; -fx-text-fill: " + UIStyles.ACCENT_BLUE + ";");

        Label awayLabel = new Label(awayTeam != null ? awayTeam.getName() : "TBD");
        awayLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: white;");

        scoreBox.getChildren().addAll(homeLabel, scoreLabel, awayLabel);

        Label segmentLabel = new Label("Segment: 0 / " + GameState.getInstance().getSport().getSegmentCount());
        segmentLabel.setStyle(UIStyles.SUBTITLE_STYLE);

        TextArea eventLog = new TextArea();
        eventLog.setStyle("-fx-background-color: #0f3460; -fx-text-fill: white; -fx-font-size: 12px; -fx-control-inner-background: #0f3460;");
        eventLog.setEditable(false);
        eventLog.setPrefHeight(240);

        HBox btnBox = new HBox(16);
        btnBox.setAlignment(Pos.CENTER);

        Button simBtn = new Button("Simulate Next Segment");
        simBtn.setStyle(UIStyles.BTN_PRIMARY);

        Button subBtn = new Button("Substitution / Lineup Edit");
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
                // half-time / between segments — allow substitution
                subBtn.setDisable(false);
            }
        });

        subBtn.setOnAction(e -> openSubstitutionDialog());

        endBtn.setOnAction(e -> {
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

    /**
     * Half-time substitution: pick a player on the field to bring OFF, and a
     * fit (non-injured) bench player to bring ON. Repeats until user closes.
     */
    private void openSubstitutionDialog() {
        while (true) {
            List<IPlayer> onField = new ArrayList<>();
            for (IPlayer p : homeTeam.getStartingLineup()) {
                if (!p.isInjured() && p.isActive()) onField.add(p);
            }
            List<IPlayer> bench = new ArrayList<>();
            for (IPlayer p : homeTeam.getSquad()) {
                if (homeTeam.getStartingLineup().contains(p)) continue;
                if (p.isInjured() || !p.isActive()) continue;
                bench.add(p);
            }

            if (bench.isEmpty()) {
                Alert a = new Alert(Alert.AlertType.INFORMATION,
                        "No available substitutes on the bench.", ButtonType.OK);
                a.showAndWait();
                return;
            }

            // Step 1: select player to take OFF
            ChoiceDialog<IPlayer> offDlg = new ChoiceDialog<>(onField.get(0), onField);
            offDlg.setTitle("Half-Time Substitution");
            offDlg.setHeaderText("Choose a player to take OFF the field");
            offDlg.setContentText("Off:");
            ((ComboBox<IPlayer>) offDlg.getDialogPane().lookup(".combo-box"))
                    .setConverter(playerConverter());
            Optional<IPlayer> offChoice = offDlg.showAndWait();
            if (!offChoice.isPresent()) return;

            // Step 2: select bench player to bring ON
            ChoiceDialog<IPlayer> onDlg = new ChoiceDialog<>(bench.get(0), bench);
            onDlg.setTitle("Half-Time Substitution");
            onDlg.setHeaderText("Choose a bench player to bring ON for "
                    + offChoice.get().getName());
            onDlg.setContentText("On:");
            ((ComboBox<IPlayer>) onDlg.getDialogPane().lookup(".combo-box"))
                    .setConverter(playerConverter());
            Optional<IPlayer> onChoice = onDlg.showAndWait();
            if (!onChoice.isPresent()) return;

            // Apply substitution by mutating the lineup list in place
            // (avoids size validation in setStartingLineup)
            List<IPlayer> lineup = homeTeam.getStartingLineup();
            int idx = lineup.indexOf(offChoice.get());
            if (idx >= 0) {
                lineup.set(idx, onChoice.get());
                match.getEvents().add("SUB - " + offChoice.get().getName()
                        + " ⇄ " + onChoice.get().getName());
            }

            // Ask if user wants another substitution
            Alert again = new Alert(Alert.AlertType.CONFIRMATION,
                    "Make another substitution?", ButtonType.YES, ButtonType.NO);
            again.setHeaderText("Substitution applied");
            Optional<ButtonType> r = again.showAndWait();
            if (!r.isPresent() || r.get() != ButtonType.YES) return;
        }
    }

    private javafx.util.StringConverter<IPlayer> playerConverter() {
        return new javafx.util.StringConverter<IPlayer>() {
            @Override
            public String toString(IPlayer p) {
                if (p == null) return "";
                return "#" + p.getNumber() + " " + p.getName()
                        + " [" + p.getPosition() + "] STA " + p.getStamina();
            }
            @Override
            public IPlayer fromString(String s) { return null; }
        };
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
