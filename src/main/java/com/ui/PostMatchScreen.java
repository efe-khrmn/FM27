package com.ui;

import com.engine.GameState;
import com.interfaces.IPlayer;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.util.List;

public class PostMatchScreen {

    private ScreenManager manager;
    private BorderPane root;

    public PostMatchScreen(ScreenManager manager) {
        this.manager = manager;
        build();
    }

    private void build() {
        root = new BorderPane();
        root.setStyle("-fx-background-color: " + UIStyles.BG_DARK + ";");
        root.setTop(buildNavBar());

        VBox center = new VBox(20);
        center.setPadding(new Insets(40));
        center.setAlignment(Pos.TOP_CENTER);

        // Result
        Object result = GameState.getInstance().getLastMatchResult();
        Label resultLabel = new Label(result != null ? result.toString() : "Match result unavailable");
        resultLabel.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: " + UIStyles.ACCENT_BLUE + ";");
        resultLabel.setWrapText(true);

        // Injured players
        Label injuryTitle = new Label("Injuries:");
        injuryTitle.setStyle(UIStyles.SUBTITLE_STYLE);

        VBox injuryBox = new VBox(6);
        injuryBox.setStyle(UIStyles.CARD_STYLE);
        injuryBox.setPadding(new Insets(12));

        List<IPlayer> squad = GameState.getInstance().getManagedTeam().getSquad();
        boolean anyInjury = false;
        for (IPlayer p : squad) {
            if (p.isInjured()) {
                Label l = new Label(p.getName() + " — out for " + p.getInjuryGames() + " match(es)");
                l.setStyle("-fx-text-fill: #e74c3c; -fx-font-size: 13px;");
                injuryBox.getChildren().add(l);
                anyInjury = true;
            }
        }
        if (!anyInjury) {
            Label none = new Label("No injuries this match.");
            none.setStyle("-fx-text-fill: #2ecc71; -fx-font-size: 13px;");
            injuryBox.getChildren().add(none);
        }

        // Continue button
        Button continueBtn = new Button("Continue to Next Week");
        continueBtn.setStyle(UIStyles.BTN_PRIMARY);
        continueBtn.setMinWidth(250);
        continueBtn.setOnAction(e -> {
            GameState.getInstance().nextPhase();
            if (GameState.getInstance().getLeague().isSeasonOver()) {
                manager.showSeasonEndScreen();
            } else {
                manager.showLeagueTableScreen();
            }
        });

        center.getChildren().addAll(resultLabel, injuryTitle, injuryBox, continueBtn);
        root.setCenter(center);
    }

    private HBox buildNavBar() {
        HBox nav = new HBox();
        nav.setPadding(new Insets(16, 24, 16, 24));
        nav.setStyle("-fx-background-color: " + UIStyles.BG_PANEL + ";");
        nav.setAlignment(Pos.CENTER_LEFT);
        Label title = new Label("Post Match");
        title.setStyle(UIStyles.TITLE_STYLE);
        nav.getChildren().add(title);
        return nav;
    }

    public BorderPane getRoot() { return root; }
}
