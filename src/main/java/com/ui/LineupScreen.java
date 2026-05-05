package com.ui;

import com.engine.GameState;
import com.football.FootballPlayer;
import com.football.FootballTeam;
import com.interfaces.IPlayer;
import com.interfaces.ISport;
import com.interfaces.ITeam;
import com.volleyball.VolleyballPlayer;
import com.volleyball.VolleyballTeam;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.util.StringConverter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Detailed lineup builder.
 *
 * Each formation has a fixed list of position slots. For each slot the user
 * picks a player from a ComboBox. The combo entries show, for the player at
 * THAT slot's position, the effective overall after the position penalty —
 * so you can see at a glance who fits where.
 *
 * Color coding for the chosen player:
 *   GREEN  = native position (no penalty)
 *   YELLOW = same group, different position (-5%)
 *   ORANGE = adjacent group (-10%)
 *   RED    = far group (-30%)
 */
public class LineupScreen {

    private final ScreenManager manager;
    private BorderPane root;

    // Formation -> ordered list of position slots
    private static final Map<String, List<String>> FOOTBALL_FORMATIONS = new LinkedHashMap<>();
    private static final Map<String, List<String>> VOLLEYBALL_FORMATIONS = new LinkedHashMap<>();
    static {
        FOOTBALL_FORMATIONS.put("4-3-3", Arrays.asList("GK","LB","CB","CB","RB","CDM","CM","CAM","LW","ST","RW"));
        FOOTBALL_FORMATIONS.put("4-4-2", Arrays.asList("GK","LB","CB","CB","RB","CM","CM","LW","ST","ST","RW"));
        FOOTBALL_FORMATIONS.put("3-5-2", Arrays.asList("GK","CB","CB","CB","CDM","CM","CM","LW","ST","ST","RW"));
        FOOTBALL_FORMATIONS.put("5-3-2", Arrays.asList("GK","LB","CB","CB","CB","RB","CDM","CM","CAM","ST","ST"));

        VOLLEYBALL_FORMATIONS.put("Standard 6", Arrays.asList("S","OH","OH","MB","MB","OPP"));
        VOLLEYBALL_FORMATIONS.put("With Libero", Arrays.asList("S","OH","OH","MB","L","OPP"));
    }

    private List<String> slotPositions;
    private String currentFormationName;
    private List<Button> slotButtons;
    private List<IPlayer> slotSelections;
    private List<Label> slotInfoLabels;
    private List<IPlayer> availablePlayers;
    private ITeam team;
    private ISport sport;

    public LineupScreen(ScreenManager manager) {
        this.manager = manager;
        build();
    }

    private void build() {
        root = new BorderPane();
        root.setStyle("-fx-background-color: " + UIStyles.BG_DARK + ";");
        root.setTop(buildNavBar());

        team = GameState.getInstance().getManagedTeam();
        sport = GameState.getInstance().getSport();
        availablePlayers = new ArrayList<>(team.getAvailablePlayers());

        Map<String, List<String>> formations = isFootball() ? FOOTBALL_FORMATIONS : VOLLEYBALL_FORMATIONS;
        String defaultFormation = formations.keySet().iterator().next();
        // Use the team's saved tactic if it matches an available formation
        if (team.getTactic() != null && formations.containsKey(team.getTactic().getTacticName())) {
            defaultFormation = team.getTactic().getTacticName();
        }
        currentFormationName = defaultFormation;
        slotPositions = formations.get(defaultFormation);

        VBox content = new VBox(16);
        content.setPadding(new Insets(20));

        // Formation selector
        HBox topBar = new HBox(12);
        topBar.setAlignment(Pos.CENTER_LEFT);
        Label fLabel = new Label("Formation:");
        fLabel.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");
        ComboBox<String> formationBox = new ComboBox<>(FXCollections.observableArrayList(formations.keySet()));
        formationBox.setValue(defaultFormation);
        formationBox.setStyle("-fx-font-size: 13px;");

        Label legend = new Label("● Green = native  ● Yellow = same group  ● Orange = adjacent  ● Red = out of position");
        legend.setStyle("-fx-text-fill: #cfd6e4; -fx-font-size: 12px;");

        topBar.getChildren().addAll(fLabel, formationBox, new Label("   "), legend);

        // Pitch grid
        VBox pitch = new VBox(10);
        pitch.setStyle(UIStyles.CARD_STYLE);
        pitch.setPadding(new Insets(16));

        ScrollPane pitchScroll = new ScrollPane(pitch);
        pitchScroll.setFitToWidth(true);
        pitchScroll.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        pitchScroll.setPrefViewportHeight(520);

        rebuildSlots(pitch);

        formationBox.setOnAction(e -> {
            currentFormationName = formationBox.getValue();
            slotPositions = formations.get(currentFormationName);
            rebuildSlots(pitch);
        });

        // Action buttons
        HBox actions = new HBox(12);
        actions.setAlignment(Pos.CENTER);
        Button autoBtn = new Button("Auto-Fill (Best Fit)");
        autoBtn.setStyle(UIStyles.BTN_SECONDARY);
        Button clearBtn = new Button("Clear");
        clearBtn.setStyle(UIStyles.BTN_DANGER);
        Button saveBtn = new Button("Save Default Lineup");
        saveBtn.setStyle(UIStyles.BTN_PRIMARY);

        autoBtn.setOnAction(e -> autoFill());
        clearBtn.setOnAction(e -> {
            for (int i = 0; i < slotSelections.size(); i++) slotSelections.set(i, null);
            refreshAllInfo();
        });
        saveBtn.setOnAction(e -> saveLineup());

        actions.getChildren().addAll(autoBtn, clearBtn, saveBtn);

        content.getChildren().addAll(topBar, pitchScroll, actions);

        // Preload existing default lineup if size matches
        preloadExistingLineup();

        root.setCenter(content);
        root.setBottom(buildBackBtn());
    }

    private boolean isFootball() {
        return team instanceof FootballTeam;
    }

    private void rebuildSlots(VBox pitch) {
        pitch.getChildren().clear();
        slotButtons = new ArrayList<>();
        slotInfoLabels = new ArrayList<>();
        slotSelections = new ArrayList<>();
        for (int i = 0; i < slotPositions.size(); i++) {
            slotSelections.add(null);
            slotButtons.add(null);
            slotInfoLabels.add(null);
        }

        List<List<Integer>> rows = computeRows();
        for (List<Integer> row : rows) {
            HBox rowBox = new HBox(14);
            rowBox.setAlignment(Pos.CENTER);
            for (int idx : row) {
                rowBox.getChildren().add(buildSlotBox(idx));
            }
            pitch.getChildren().add(rowBox);
        }
        refreshAllInfo();
    }

    private List<List<Integer>> computeRows() {
        List<List<Integer>> rows = new ArrayList<>();
        if (isFootball()) {
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

    private VBox buildSlotBox(int slotIdx) {
        String pos = slotPositions.get(slotIdx);
        VBox box = new VBox(4);
        box.setAlignment(Pos.CENTER);
        box.setPrefWidth(180);
        box.setPadding(new Insets(8));
        box.setStyle("-fx-background-color: #16235a; -fx-background-radius: 8; -fx-border-color: #2a3a7a; -fx-border-radius: 8;");

        Label posLabel = new Label(pos);
        posLabel.setStyle("-fx-text-fill: #ffd166; -fx-font-weight: bold; -fx-font-size: 14px;");

        Button pickBtn = new Button("-- pick --");
        pickBtn.setPrefWidth(165);
        pickBtn.setStyle("-fx-background-color: #1f2d6e; -fx-text-fill: white;");
        pickBtn.setOnAction(e -> openPlayerPicker(slotIdx));

        Label info = new Label("(empty)");
        info.setStyle("-fx-text-fill: #cfd6e4; -fx-font-size: 11px;");
        info.setWrapText(true);

        slotButtons.set(slotIdx, pickBtn);
        slotInfoLabels.set(slotIdx, info);

        box.getChildren().addAll(posLabel, pickBtn, info);
        return box;
    }

    private void openPlayerPicker(int slotIdx) {
        String pos = slotPositions.get(slotIdx);
        // Build sorted list of players by effective overall at this position (desc)
        List<IPlayer> sorted = new ArrayList<>(availablePlayers);
        sorted.sort((a, b) -> Double.compare(effectiveAt(b, pos), effectiveAt(a, pos)));

        List<String> labels = new ArrayList<>();
        labels.add("(clear)");
        for (IPlayer p : sorted) {
            labels.add(playerLabel(p, pos));
        }

        ChoiceDialog<String> dlg = new ChoiceDialog<>(labels.get(0), labels);
        dlg.setTitle("Pick player for " + pos);
        dlg.setHeaderText("Select player for slot: " + pos);
        dlg.setContentText("Player:");
        dlg.showAndWait().ifPresent(choice -> {
            if ("(clear)".equals(choice)) {
                slotSelections.set(slotIdx, null);
            } else {
                int idx = labels.indexOf(choice) - 1;
                if (idx >= 0 && idx < sorted.size()) {
                    IPlayer chosen = sorted.get(idx);
                    // Remove from any other slot
                    for (int i = 0; i < slotSelections.size(); i++) {
                        if (i != slotIdx && chosen.equals(slotSelections.get(i))) {
                            slotSelections.set(i, null);
                        }
                    }
                    slotSelections.set(slotIdx, chosen);
                }
            }
            refreshAllInfo();
        });
    }

    private String playerLabel(IPlayer p, String pos) {
        double eff = effectiveAt(p, pos);
        return "#" + p.getNumber() + " " + p.getName() + " [" + p.getPosition() + "] OVR " + Math.round(eff);
    }

    private double effectiveAt(IPlayer p, String pos) {
        if (p instanceof FootballPlayer) return ((FootballPlayer) p).getEffectiveOverall(pos);
        if (p instanceof VolleyballPlayer) return ((VolleyballPlayer) p).getEffectiveOverall(pos);
        return 0;
    }

    private double baseOverall(IPlayer p) {
        return effectiveAt(p, p.getPosition());
    }

    private void refreshAllInfo() {
        for (int i = 0; i < slotButtons.size(); i++) {
            Button btn = slotButtons.get(i);
            Label info = slotInfoLabels.get(i);
            String pos = slotPositions.get(i);
            IPlayer p = slotSelections.get(i);
            VBox parent = (VBox) btn.getParent();
            if (p == null) {
                btn.setText("-- pick --");
                info.setText("(empty)");
                parent.setStyle("-fx-background-color: #16235a; -fx-background-radius: 8; -fx-border-color: #2a3a7a; -fx-border-radius: 8;");
                continue;
            }
            btn.setText(playerLabel(p, pos));
            double base = baseOverall(p);
            double eff = effectiveAt(p, pos);
            String tag;
            String border;
            if (p.getPosition().equals(pos)) { tag = "NATIVE"; border = "#22c55e"; }
            else {
                double ratio = eff / Math.max(base, 0.01);
                if (ratio >= 0.94) { tag = "same group -5%"; border = "#eab308"; }
                else if (ratio >= 0.89) { tag = "adjacent -10%"; border = "#f97316"; }
                else { tag = "out of position -30%"; border = "#ef4444"; }
            }
            info.setText(p.getPosition() + " → " + pos + " | OVR " + Math.round(eff) + " (" + tag + ")");
            parent.setStyle("-fx-background-color: #16235a; -fx-background-radius: 8; -fx-border-color: " + border + "; -fx-border-width: 2; -fx-border-radius: 8;");
        }
    }

    private void autoFill() {
        for (int i = 0; i < slotSelections.size(); i++) slotSelections.set(i, null);
        List<IPlayer> used = new ArrayList<>();
        for (int i = 0; i < slotPositions.size(); i++) {
            String pos = slotPositions.get(i);
            IPlayer best = null; double bestScore = -1;
            for (IPlayer p : availablePlayers) {
                if (used.contains(p)) continue;
                double s = effectiveAt(p, pos);
                if (s > bestScore) { bestScore = s; best = p; }
            }
            if (best != null) {
                used.add(best);
                slotSelections.set(i, best);
            }
        }
        refreshAllInfo();
    }

    private void preloadExistingLineup() {
        List<IPlayer> existing = null;
        if (team instanceof FootballTeam && ((FootballTeam) team).hasDefaultLineup()) {
            existing = ((FootballTeam) team).getDefaultLineup();
        } else if (team instanceof VolleyballTeam && ((VolleyballTeam) team).hasDefaultLineup()) {
            existing = ((VolleyballTeam) team).getDefaultLineup();
        }
        if (existing != null && existing.size() == slotPositions.size()) {
            for (int i = 0; i < existing.size(); i++) slotSelections.set(i, existing.get(i));
            refreshAllInfo();
        }
    }

    private void saveLineup() {
        List<IPlayer> chosen = new ArrayList<>();
        for (IPlayer p : slotSelections) {
            if (p == null) {
                showAlert("Please fill every slot before saving.");
                return;
            }
            chosen.add(p);
        }
        if (team instanceof FootballTeam) ((FootballTeam) team).saveDefaultLineup(chosen);
        else if (team instanceof VolleyballTeam) ((VolleyballTeam) team).saveDefaultLineup(chosen);

        // Persist the chosen formation as the team's tactic so PreMatch / Match
        // screens don't reset it back to the first available formation.
        if (currentFormationName != null) {
            try {
                if (team instanceof FootballTeam) {
                    team.setTactic(new com.football.FootballTactic(currentFormationName));
                } else if (team instanceof VolleyballTeam) {
                    team.setTactic(new com.volleyball.VolleyballTactic(currentFormationName));
                }
            } catch (Exception ignored) { /* don't block save on tactic mismatch */ }
        }

        Alert a = new Alert(Alert.AlertType.INFORMATION, "Default lineup saved.", ButtonType.OK);
        a.showAndWait();
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
        Label title = new Label("Lineup Builder — " + GameState.getInstance().getManagedTeam().getName());
        title.setStyle(UIStyles.TITLE_STYLE);
        nav.getChildren().add(title);
        return nav;
    }

    private HBox buildBackBtn() {
        HBox bottom = new HBox();
        bottom.setPadding(new Insets(16, 24, 16, 24));
        bottom.setStyle("-fx-background-color: " + UIStyles.BG_PANEL + ";");
        bottom.setAlignment(Pos.CENTER);
        Button back = new Button("Back to League Table");
        back.setStyle(UIStyles.BTN_SECONDARY);
        back.setOnAction(e -> manager.showLeagueTableScreen());
        bottom.getChildren().add(back);
        return bottom;
    }

    public BorderPane getRoot() { return root; }
}
