package com.ui;

import com.engine.GameState;
import com.football.FootballTeam;
import com.interfaces.IPlayer;
import com.interfaces.ITeam;
import com.interfaces.ITactic;
import com.volleyball.VolleyballTeam;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class PreMatchScreen {

    private ScreenManager manager;
    private BorderPane root;

    private static final Map<String, List<String>> FOOTBALL_FORMATIONS = new LinkedHashMap<>();
    private static final Map<String, List<String>> VOLLEYBALL_FORMATIONS = new LinkedHashMap<>();
    static {
        FOOTBALL_FORMATIONS.put("4-3-3", Arrays.asList("GK","LB","CB","CB","RB","CDM","CM","CAM","LW","ST","RW"));
        FOOTBALL_FORMATIONS.put("4-4-2", Arrays.asList("GK","LB","CB","CB","RB","CM","CM","LW","ST","ST","RW"));
        FOOTBALL_FORMATIONS.put("3-5-2", Arrays.asList("GK","CB","CB","CB","CDM","CM","CM","LW","ST","ST","RW"));
        FOOTBALL_FORMATIONS.put("5-3-2", Arrays.asList("GK","LB","CB","CB","CB","RB","CDM","CM","CAM","ST","ST"));

        VOLLEYBALL_FORMATIONS.put("5-1", Arrays.asList("S","OH","OH","MB","MB","OPP"));
        VOLLEYBALL_FORMATIONS.put("6-2", Arrays.asList("S","OH","OH","MB","MB","OPP"));
        VOLLEYBALL_FORMATIONS.put("4-2", Arrays.asList("S","OH","OH","MB","L","OPP"));
    }

    public PreMatchScreen(ScreenManager manager) {
        this.manager = manager;
        build();
    }

    private void build() {
        root = new BorderPane();
        root.setStyle("-fx-background-color: " + UIStyles.BG_DARK + ";");
        root.setTop(buildNavBar());

        ITeam team = GameState.getInstance().getManagedTeam();
        ITeam opponent = findOpponent(team);
        int teamSize = GameState.getInstance().getSport().getTeamSize();

        List<IPlayer> myLineup = getDefaultLineup(team);
        List<IPlayer> oppLineup = opponent != null ? getDefaultLineup(opponent) : null;

        HBox center = new HBox(20);
        center.setPadding(new Insets(20));
        center.setAlignment(Pos.TOP_CENTER);

        VBox leftPitchHolder = new VBox();
        leftPitchHolder.getChildren().add(buildPitchPanel(team, myLineup, true));
        VBox rightPitch = buildPitchPanel(opponent, oppLineup, false);

        // Middle controls (tactic + start)
        VBox midPanel = new VBox(12);
        midPanel.setStyle(UIStyles.CARD_STYLE);
        midPanel.setPrefWidth(220);
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
        tacticBox.setMinWidth(180);

        // Live update of the pitch when the tactic selection changes
        tacticBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal == null || newVal.isEmpty()) return;
            try {
                if (team instanceof FootballTeam) {
                    team.setTactic(new com.football.FootballTactic(newVal));
                } else if (team instanceof VolleyballTeam) {
                    team.setTactic(new com.volleyball.VolleyballTactic(newVal));
                }
            } catch (Exception ignored) { }
            leftPitchHolder.getChildren().setAll(buildPitchPanel(team, myLineup, true));
        });

        Button startBtn = new Button("Start Match");
        startBtn.setStyle(UIStyles.BTN_PRIMARY);
        startBtn.setMinWidth(180);

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
                    if (team instanceof FootballTeam) {
                        team.setTactic(new com.football.FootballTactic(chosenTactic));
                    } else if (team instanceof VolleyballTeam) {
                        team.setTactic(new com.volleyball.VolleyballTactic(chosenTactic));
                    }
                } catch (Exception ignored) { }
            }
            manager.showMatchScreen();
        });

        midPanel.getChildren().addAll(tacticLabel, tacticBox, startBtn);

        center.getChildren().addAll(leftPitchHolder, midPanel, rightPitch);
        root.setCenter(center);
        root.setBottom(buildBackBtn());
    }

    private VBox buildPitchPanel(ITeam team, List<IPlayer> lineup, boolean managed) {
        VBox panel = new VBox(10);
        panel.setStyle(UIStyles.CARD_STYLE);
        panel.setPrefWidth(420);
        panel.setPadding(new Insets(14));

        String teamName = team != null ? team.getName() : "Opponent";
        String formationName = team != null && team.getTactic() != null ? team.getTactic().getTacticName() : "";

        Label header = new Label(teamName + (managed ? " (You)" : "") + (formationName.isEmpty() ? "" : "  —  " + formationName));
        header.setStyle(UIStyles.SUBTITLE_STYLE);

        VBox pitch = new VBox(10);
        pitch.setAlignment(Pos.CENTER);
        pitch.setStyle("-fx-background-color: #0f5132; -fx-background-radius: 8; -fx-border-color: #1f7a4d; -fx-border-radius: 8;");
        pitch.setPadding(new Insets(14));

        List<String> slots = formationSlotsFor(team, lineup);
        if (lineup == null || slots == null || lineup.size() != slots.size()) {
            Label none = new Label("No lineup set");
            none.setStyle("-fx-text-fill: #ffd166;");
            pitch.getChildren().add(none);
        } else {
            boolean football = team instanceof FootballTeam;
            List<List<Integer>> rows = computeRows(slots, football);
            for (List<Integer> row : rows) {
                if (row.isEmpty()) continue;
                HBox rowBox = new HBox(10);
                rowBox.setAlignment(Pos.CENTER);
                for (int idx : row) {
                    rowBox.getChildren().add(buildSlotCard(slots.get(idx), lineup.get(idx)));
                }
                pitch.getChildren().add(rowBox);
            }
        }

        panel.getChildren().addAll(header, pitch);
        return panel;
    }

    private VBox buildSlotCard(String pos, IPlayer p) {
        VBox box = new VBox(2);
        box.setAlignment(Pos.CENTER);
        box.setPrefWidth(110);
        box.setPadding(new Insets(6));
        box.setStyle("-fx-background-color: #16235a; -fx-background-radius: 6; -fx-border-color: #2a3a7a; -fx-border-radius: 6;");

        Label posLabel = new Label(pos);
        posLabel.setStyle("-fx-text-fill: #ffd166; -fx-font-weight: bold; -fx-font-size: 11px;");

        Label num = new Label("#" + p.getNumber());
        num.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 13px;");

        String injMark = p.isInjured() ? " 🚑" : "";
        Label name = new Label(p.getName() + injMark);
        name.setStyle((p.isInjured() ? "-fx-text-fill: #ff6b6b;" : "-fx-text-fill: white;") + " -fx-font-size: 11px;");
        name.setWrapText(true);

        box.getChildren().addAll(posLabel, num, name);
        return box;
    }

    private List<String> formationSlotsFor(ITeam team, List<IPlayer> lineup) {
        if (team == null) return null;
        Map<String, List<String>> formations = team instanceof FootballTeam ? FOOTBALL_FORMATIONS : VOLLEYBALL_FORMATIONS;
        String name = team.getTactic() != null ? team.getTactic().getTacticName() : null;
        if (name != null && formations.containsKey(name)) return formations.get(name);
        // fallback: pick formation matching lineup size
        if (lineup != null) {
            for (List<String> slots : formations.values()) {
                if (slots.size() == lineup.size()) return slots;
            }
        }
        return formations.values().iterator().next();
    }

    private List<List<Integer>> computeRows(List<String> slotPositions, boolean football) {
        List<List<Integer>> rows = new ArrayList<>();
        if (football) {
            List<Integer> gk = new ArrayList<>(), def = new ArrayList<>(), mid = new ArrayList<>(), att = new ArrayList<>();
            for (int i = 0; i < slotPositions.size(); i++) {
                String p = slotPositions.get(i);
                if (p.equals("GK")) gk.add(i);
                else if (p.equals("CB") || p.equals("LB") || p.equals("RB")) def.add(i);
                else if (p.equals("CM") || p.equals("CDM") || p.equals("CAM")) mid.add(i);
                else att.add(i);
            }
            rows.add(att); rows.add(mid); rows.add(def); rows.add(gk);
        } else {
            List<Integer> front = new ArrayList<>(), back = new ArrayList<>();
            for (int i = 0; i < slotPositions.size(); i++) {
                String p = slotPositions.get(i);
                if (p.equals("L") || p.equals("S")) back.add(i);
                else front.add(i);
            }
            rows.add(front); rows.add(back);
        }
        return rows;
    }

    private List<IPlayer> getDefaultLineup(ITeam t) {
        if (t instanceof FootballTeam) {
            FootballTeam ft = (FootballTeam) t;
            if (ft.hasDefaultLineup()) return ft.getDefaultLineup();
        } else if (t instanceof VolleyballTeam) {
            VolleyballTeam vt = (VolleyballTeam) t;
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
