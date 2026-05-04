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
        FOOTBALL_FORMATIONS.put("4-4-2", Arrays.asList("GK","LB","CB","CB","RB","CM","CM","LW","RW","ST","ST"));
        FOOTBALL_FORMATIONS.put("3-5-2", Arrays.asList("GK","CB","CB","CB","CDM","CM","CM","LW","RW","ST","ST"));
        FOOTBALL_FORMATIONS.put("4-2-3-1", Arrays.asList("GK","LB","CB","CB","RB","CDM","CDM","CAM","LW","RW","ST"));

        VOLLEYBALL_FORMATIONS.put("Standard 6", Arrays.asList("S","OH","OH","MB","MB","OPP"));
        VOLLEYBALL_FORMATIONS.put("With Libero", Arrays.asList("S","OH","OH","MB","L","OPP"));
    }

    private List<String> slotPositions;
    private List<ComboBox<IPlayer>> slotCombos;
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

        rebuildSlots(pitch);

        formationBox.setOnAction(e -> {
            slotPositions = formations.get(formationBox.getValue());
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
        clearBtn.setOnAction(e -> { for (ComboBox<IPlayer> c : slotCombos) c.setValue(null); refreshAllInfo(); });
        saveBtn.setOnAction(e -> saveLineup());

        actions.getChildren().addAll(autoBtn, clearBtn, saveBtn);

        content.getChildren().addAll(topBar, pitch, actions);

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
        slotCombos = new ArrayList<>();
        slotInfoLabels = new ArrayList<>();

        // Group rows for visual pitch — football: GK / DEF / MID / ATT
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
            // Volleyball: front row (3) and back row (3) — first 3 back, last 3 front, simple split
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
        box.setPrefWidth(170);
        box.setPadding(new Insets(8));
        box.setStyle("-fx-background-color: #16235a; -fx-background-radius: 8; -fx-border-color: #2a3a7a; -fx-border-radius: 8;");

        Label posLabel = new Label(pos);
        posLabel.setStyle("-fx-text-fill: #ffd166; -fx-font-weight: bold; -fx-font-size: 14px;");

        ComboBox<IPlayer> combo = new ComboBox<>(FXCollections.observableArrayList(availablePlayers));
        combo.setPrefWidth(150);
        combo.setConverter(new StringConverter<IPlayer>() {
            @Override public String toString(IPlayer p) {
                if (p == null) return "-- pick --";
                double eff = effectiveAt(p, pos);
                return "#" + p.getNumber() + " " + p.getName() + " [" + p.getPosition() + "] " + Math.round(eff);
            }
            @Override public IPlayer fromString(String s) { return null; }
        });

        Label info = new Label("");
        info.setStyle("-fx-text-fill: #cfd6e4; -fx-font-size: 11px;");

        combo.setOnAction(e -> {
            // Prevent same player twice
            IPlayer chosen = combo.getValue();
            if (chosen != null) {
                for (int i = 0; i < slotCombos.size(); i++) {
                    if (i != slotIdx && chosen.equals(slotCombos.get(i).getValue())) {
                        slotCombos.get(i).setValue(null);
                    }
                }
            }
            refreshAllInfo();
        });

        slotCombos.add(combo);
        slotInfoLabels.add(info);

        box.getChildren().addAll(posLabel, combo, info);
        return box;
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
        for (int i = 0; i < slotCombos.size(); i++) {
            ComboBox<IPlayer> c = slotCombos.get(i);
            Label info = slotInfoLabels.get(i);
            String pos = slotPositions.get(i);
            IPlayer p = c.getValue();
            VBox parent = (VBox) c.getParent();
            if (p == null) {
                info.setText("(empty)");
                parent.setStyle("-fx-background-color: #16235a; -fx-background-radius: 8; -fx-border-color: #2a3a7a; -fx-border-radius: 8;");
                continue;
            }
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
            info.setText(p.getPosition() + " → " + pos + " | " + Math.round(eff) + " (" + tag + ")");
            parent.setStyle("-fx-background-color: #16235a; -fx-background-radius: 8; -fx-border-color: " + border + "; -fx-border-width: 2; -fx-border-radius: 8;");
        }
    }

    private void autoFill() {
        List<IPlayer> used = new ArrayList<>();

        for (int i = 0; i < slotPositions.size(); i++) {
            String pos = slotPositions.get(i);

            ComboBox<IPlayer> combo = slotCombos.get(i);

            IPlayer best = null;
            double bestScore = -1;

            for (IPlayer p : combo.getItems()) {
                if (used.contains(p)) continue;

                double score = effectiveAt(p, pos);
                if (score > bestScore) {
                    bestScore = score;
                    best = p;
                }
            }

            if (best != null) {
                used.add(best);

                // 🔥 CRITICAL FIX: aynı referansı set et
                combo.setValue(best);
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
            for (int i = 0; i < existing.size(); i++) slotCombos.get(i).setValue(existing.get(i));
            refreshAllInfo();
        }
    }

    private void saveLineup() {
        List<IPlayer> chosen = new ArrayList<>();
        for (ComboBox<IPlayer> c : slotCombos) {
            IPlayer p = c.getValue();
            if (p == null) {
                showAlert("Please fill every slot before saving.");
                return;
            }
            chosen.add(p);
        }
        if (team instanceof FootballTeam) ((FootballTeam) team).saveDefaultLineup(chosen);
        else if (team instanceof VolleyballTeam) ((VolleyballTeam) team).saveDefaultLineup(chosen);

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
