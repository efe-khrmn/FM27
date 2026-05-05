package com.ui;

import com.engine.Fixture;
import com.engine.GameState;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.util.List;

public class ScheduleScreen {

    private ScreenManager manager;
    private BorderPane root;

    public ScheduleScreen(ScreenManager manager) {
        this.manager = manager;
        build();
    }

    private void build() {
        root = new BorderPane();
        root.setStyle("-fx-background-color: " + UIStyles.BG_DARK + ";");

        root.setTop(buildNavBar());

        VBox center = new VBox(8);
        center.setPadding(new Insets(24));

        int totalWeeks = GameState.getInstance().getLeague().getSchedule().getTotalWeeks();
        int currentWeek = GameState.getInstance().getWeek();

        ScrollPane scroll = new ScrollPane();
        scroll.setStyle("-fx-background-color: transparent;");
        scroll.setFitToWidth(true);

        VBox content = new VBox(16);
        content.setStyle("-fx-background-color: " + UIStyles.BG_DARK + ";");
        content.setPadding(new Insets(8));

        for (int w = 1; w <= totalWeeks; w++) {
            List<Fixture> fixtures = GameState.getInstance().getLeague().getSchedule().getWeekFixtures(w);

            VBox weekBox = new VBox(6);
            weekBox.setStyle(UIStyles.CARD_STYLE + (w == currentWeek ? " -fx-border-color: #e94560; -fx-border-width: 1; -fx-border-radius: 8;" : ""));
            weekBox.setPadding(new Insets(12));

            Label weekLabel = new Label("Week " + w + (w == currentWeek ? "  ← Current" : ""));
            weekLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: " + (w == currentWeek ? UIStyles.ACCENT_BLUE : "white") + ";");
            weekBox.getChildren().add(weekLabel);

            for (Fixture f : fixtures) {
                HBox row = new HBox();
                row.setAlignment(Pos.CENTER);
                row.setSpacing(12);

                Label home = new Label(f.getHomeTeam().getName());
                home.setStyle("-fx-text-fill: white; -fx-font-size: 13px; -fx-min-width: 200px; -fx-alignment: center-right;");

                String vsText = "vs";
                if (f.isPlayed()) {
                    Object res = f.getResult();
                    if (res instanceof com.engine.MatchResult) {
                        vsText = ((com.engine.MatchResult) res).getScore().toString();
                    } else if (res != null) {
                        vsText = extractScore(res.toString(),
                                f.getHomeTeam().getName(), f.getAwayTeam().getName());
                    }
                }
                Label vs = new Label(vsText);
                vs.setStyle("-fx-text-fill: " + (f.isPlayed() ? UIStyles.ACCENT_BLUE : "#aaaaaa") + "; -fx-font-size: 13px; -fx-min-width: 80px; -fx-alignment: center;");

                Label away = new Label(f.getAwayTeam().getName());
                away.setStyle("-fx-text-fill: white; -fx-font-size: 13px; -fx-min-width: 200px;");

                row.getChildren().addAll(home, vs, away);
                weekBox.getChildren().add(row);
            }

            content.getChildren().add(weekBox);
        }

        scroll.setContent(content);
        center.getChildren().add(scroll);
        root.setCenter(center);
        root.setBottom(buildBackBtn());
    }

    /** Strips team names and any "| Winner: ..." suffix, leaving just "X - Y" (or sets text). */
    private String extractScore(String full, String home, String away) {
        if (full == null) return "vs";
        String s = full;
        int pipe = s.indexOf('|');
        if (pipe >= 0) s = s.substring(0, pipe);
        if (home != null && s.startsWith(home)) s = s.substring(home.length());
        if (away != null && s.endsWith(away)) s = s.substring(0, s.length() - away.length());
        s = s.trim();
        return s.isEmpty() ? "vs" : s;
    }

    private HBox buildNavBar() {
        HBox nav = new HBox();
        nav.setPadding(new Insets(16, 24, 16, 24));
        nav.setStyle("-fx-background-color: " + UIStyles.BG_PANEL + ";");
        nav.setAlignment(Pos.CENTER_LEFT);
        Label title = new Label("Schedule");
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
