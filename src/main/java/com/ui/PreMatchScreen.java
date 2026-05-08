package com.ui;

import com.engine.GameState;
import com.interfaces.IPlayer;
import com.interfaces.ITeam;
import com.interfaces.ITactic;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.util.ArrayList;
import java.util.List;

public class PreMatchScreen {

    private ScreenManager manager;
    private BorderPane root;

    public PreMatchScreen(ScreenManager manager) {
        this.manager = manager;
        build();
    }

    private void build() {
        root = new BorderPane();
        root.setStyle("-fx-background-color: " + UIStyles.BG_DARK + ";");
        root.setTop(buildNavBar());

        HBox center = new HBox(16);
        center.setPadding(new Insets(24));
        center.setAlignment(Pos.TOP_CENTER);

        ITeam team = GameState.getInstance().getManagedTeam();
        ITeam opponent = findOpponent(team);
        int teamSize = GameState.getInstance().getSport().getTeamSize();

        List<IPlayer> myLineup = getDefaultLineup(team);

        // Left — managed team lineup
        VBox leftPanel = buildLineupPanel(team.getName() + " (You)", myLineup);

        // Middle — tactic + start
        VBox midPanel = new VBox(12);
        midPanel.setStyle(UIStyles.CARD_STYLE);
        midPanel.setPrefWidth(260);
        midPanel.setAlignment(Pos.TOP_CENTER);

        Label tacticLabel = new Label("Select Tactic:");
        tacticLabel.setStyle(UIStyles.LABEL_STYLE);

        ComboBox<String> tacticBox = new ComboBox<>();
        ITactic currentTactic = team.getTactic();
        if (currentTactic != null) {
            tacticBox.getItems().addAll(currentTactic.getAvailableNames());
            tacticBox.setValue(currentTactic.getTacticName());
        }
        tacticBox.setStyle("-fx-background-color: #0f3460; -fx-text-fill: white;");
        tacticBox.setMinWidth(200);

        Button startBtn = new Button("Start Match");
        startBtn.setStyle(UIStyles.BTN_PRIMARY);
        startBtn.setMinWidth(200);

        startBtn.setOnAction(e -> {
            if (myLineup == null || myLineup.size() != teamSize) {
                showAlert("Lineup is not set. Please set it from the Lineup screen (" + teamSize + " players required).");
                return;
            }
            StringBuilder injuredNames = new StringBuilder();
            for (IPlayer p : myLineup) {
                if (p.isInjured()) {
                    if (injuredNames.length() > 0) injuredNames.append(", ");
                    injuredNames.append(p.getName());
                }
            }
            if (injuredNames.length() > 0) {
                showAlert("Sakat oyuncularla maça çıkamazsınız!\nSakat: " + injuredNames
                        + "\nLütfen Lineup ekranından değiştirin.");
                return;
            }
            try {
                team.setStartingLineup(new ArrayList<>(myLineup));
            } catch (IllegalArgumentException ex) {
                showAlert(ex.getMessage());
                return;
            }
            String chosenTactic = tacticBox.getValue();
            if (chosenTactic != null && !chosenTactic.isEmpty()) {
                try {
                    if (team instanceof com.football.FootballTeam) {
                        team.setTactic(new com.football.FootballTactic(chosenTactic));
                    } else if (team instanceof com.volleyball.VolleyballTeam) {
                        team.setTactic(new com.volleyball.VolleyballTactic(chosenTactic));
                    }
                } catch (Exception ignored) { }
            }
            manager.showMatchScreen();
        });

        midPanel.getChildren().addAll(tacticLabel, tacticBox, startBtn);

        // Right — opponent lineup
        VBox rightPanel = buildLineupPanel(
                opponent != null ? opponent.getName() : "Opponent",
                opponent != null ? getDefaultLineup(opponent) : null);

        center.getChildren().addAll(leftPanel, midPanel, rightPanel);
        root.setCenter(center);
        root.setBottom(buildBackBtn());
    }

    private VBox buildLineupPanel(String title, List<IPlayer> lineup) {
        VBox panel = new VBox(12);
        panel.setStyle(UIStyles.CARD_STYLE);
        panel.setPrefWidth(340);

        Label header = new Label(title);
        header.setStyle(UIStyles.SUBTITLE_STYLE);

        ListView<IPlayer> list = new ListView<>();
        list.setStyle("-fx-background-color: #1a2a5e; -fx-text-fill: white;");
        list.setPrefHeight(520);
        list.setFocusTraversable(false);
        list.setMouseTransparent(true);
        if (lineup != null) list.getItems().addAll(lineup);

        list.setCellFactory(lv -> new ListCell<IPlayer>() {
            @Override
            protected void updateItem(IPlayer item, boolean empty) {
                super.updateItem(item, empty);
                if (item != null) {
                    String injMark = item.isInjured() ? "  🚑" : "";
                    setText("#" + item.getNumber() + "  " + item.getName() + injMark);
                    setStyle((item.isInjured() ? "-fx-text-fill: #ff6b6b;" : "-fx-text-fill: white;")
                            + " -fx-background-color: transparent;");
                } else {
                    setText(null);
                }
            }
        });

        panel.getChildren().addAll(header, list);
        return panel;
    }

    private List<IPlayer> getDefaultLineup(ITeam t) {
        if (t instanceof com.football.FootballTeam) {
            com.football.FootballTeam ft = (com.football.FootballTeam) t;
            if (ft.hasDefaultLineup()) return ft.getDefaultLineup();
        } else if (t instanceof com.volleyball.VolleyballTeam) {
            com.volleyball.VolleyballTeam vt = (com.volleyball.VolleyballTeam) t;
            if (vt.hasDefaultLineup()) return vt.getDefaultLineup();
        }
        return null;
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

    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.WARNING, msg, ButtonType.OK);
        alert.showAndWait();
    }

    private HBox buildNavBar() {
        HBox nav = new HBox();
        nav.setPadding(new Insets(16, 24, 16, 24));
        nav.setStyle("-fx-background-color: " + UIStyles.BG_PANEL + ";");
        nav.setAlignment(Pos.CENTER_LEFT);
        Label title = new Label("Pre-Match Setup");
        title.setStyle(UIStyles.TITLE_STYLE);
        nav.getChildren().add(title);
        return nav;
    }

    private HBox buildBackBtn() {
        HBox bottom = new HBox();
        bottom.setPadding(new Insets(16, 24, 16, 24));
        bottom.setStyle("-fx-background-color: " + UIStyles.BG_PANEL + ";");
        bottom.setAlignment(Pos.CENTER);
        Button back = new Button("Back");
        back.setStyle(UIStyles.BTN_SECONDARY);
        back.setOnAction(e -> manager.showLeagueTableScreen());
        bottom.getChildren().add(back);
        return bottom;
    }

    public BorderPane getRoot() { return root; }
}
