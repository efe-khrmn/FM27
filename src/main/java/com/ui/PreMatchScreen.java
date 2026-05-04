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
    private List<IPlayer> selectedLineup = new ArrayList<>();

    public PreMatchScreen(ScreenManager manager) {
        this.manager = manager;
        build();
    }

    private void build() {
        root = new BorderPane();
        root.setStyle("-fx-background-color: " + UIStyles.BG_DARK + ";");
        root.setTop(buildNavBar());

        HBox center = new HBox(24);
        center.setPadding(new Insets(24));

        ITeam team = GameState.getInstance().getManagedTeam();
        int teamSize = GameState.getInstance().getSport().getTeamSize();

        // Left — available players
        VBox leftPanel = new VBox(12);
        leftPanel.setStyle(UIStyles.CARD_STYLE);
        leftPanel.setPrefWidth(500);

        Label availLabel = new Label("Available Players");
        availLabel.setStyle(UIStyles.SUBTITLE_STYLE);

        ListView<IPlayer> availList = new ListView<>();
        availList.setStyle("-fx-background-color: #1a2a5e; -fx-text-fill: white;");
        availList.setPrefHeight(400);
        availList.getItems().addAll(team.getAvailablePlayers());
        availList.setCellFactory(lv -> new ListCell<IPlayer>() {
            @Override
            protected void updateItem(IPlayer item, boolean empty) {
                super.updateItem(item, empty);
                if (item != null) {
                    setText("#" + item.getNumber() + " " + item.getName()
                            + " [" + item.getPosition() + "]");
                    setStyle("-fx-text-fill: white; -fx-background-color: transparent;");
                } else {
                    setText(null);
                }
            }
        });
        Button addBtn = new Button("Add to Lineup →");
        addBtn.setStyle(UIStyles.BTN_PRIMARY);

        leftPanel.getChildren().addAll(availLabel, availList, addBtn);

        // Right — selected lineup + tactic
        VBox rightPanel = new VBox(12);
        rightPanel.setStyle(UIStyles.CARD_STYLE);
        rightPanel.setPrefWidth(500);

        Label lineupLabel = new Label("Starting Lineup (0/" + teamSize + ")");
        lineupLabel.setStyle(UIStyles.SUBTITLE_STYLE);

        ListView<IPlayer> lineupList = new ListView<>();
        // PreMatchScreen.build() içinde lineupList oluşturduktan sonra
        ITeam team1 = GameState.getInstance().getManagedTeam();

        // load default lineup if exists
        if (team1 instanceof com.football.FootballTeam) {
            com.football.FootballTeam ft = (com.football.FootballTeam) team;
            if (ft.hasDefaultLineup()) {
                lineupList.getItems().addAll(ft.getDefaultLineup());
                lineupLabel.setText("Starting Lineup (" + lineupList.getItems().size() + "/" + teamSize + ")");
            }
        } else if (team instanceof com.volleyball.VolleyballTeam) {
            com.volleyball.VolleyballTeam vt = (com.volleyball.VolleyballTeam) team;
            if (vt.hasDefaultLineup()) {
                lineupList.getItems().addAll(vt.getDefaultLineup());
                lineupLabel.setText("Starting Lineup (" + lineupList.getItems().size() + "/" + teamSize + ")");
            }
        }
        lineupList.setStyle("-fx-background-color: #1a2a5e; -fx-text-fill: white;");
        lineupList.setPrefHeight(300);
        lineupList.setCellFactory(lv -> new ListCell<IPlayer>() {
            @Override
            protected void updateItem(IPlayer item, boolean empty) {
                super.updateItem(item, empty);
                if (item != null) {
                    setText("#" + item.getNumber() + " " + item.getName()
                            + " [" + item.getPosition() + "]");
                    setStyle("-fx-text-fill: white; -fx-background-color: transparent;");
                } else {
                    setText(null);
                }
            }
        });

        Button removeBtn = new Button("← Remove");
        removeBtn.setStyle(UIStyles.BTN_DANGER);

        // Tactic selector
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

        // Add button logic
        addBtn.setOnAction(e -> {
            IPlayer selected = availList.getSelectionModel().getSelectedItem();
            if (selected != null && !lineupList.getItems().contains(selected)
                    && lineupList.getItems().size() < teamSize) {
                lineupList.getItems().add(selected);
                lineupLabel.setText("Starting Lineup (" + lineupList.getItems().size() + "/" + teamSize + ")");
            }
        });

        removeBtn.setOnAction(e -> {
            IPlayer selected = lineupList.getSelectionModel().getSelectedItem();
            if (selected != null) {
                lineupList.getItems().remove(selected);
                lineupLabel.setText("Starting Lineup (" + lineupList.getItems().size() + "/" + teamSize + ")");
            }
        });
        startBtn.setOnAction(e -> {
            if (lineupList.getItems().size() != teamSize) {
                showAlert("Please select exactly " + teamSize + " players.");
                return;
            }
            try {
                team.setStartingLineup(new ArrayList<>(lineupList.getItems()));
            } catch (IllegalArgumentException ex) {
                showAlert(ex.getMessage());
                return;
            }
            manager.showMatchScreen();
        });

        rightPanel.getChildren().addAll(lineupLabel, lineupList, removeBtn, tacticLabel, tacticBox, startBtn);

        center.getChildren().addAll(leftPanel, rightPanel);
        root.setCenter(center);
        root.setBottom(buildBackBtn());
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
