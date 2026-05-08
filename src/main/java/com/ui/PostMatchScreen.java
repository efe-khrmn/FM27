package com.ui;

import com.engine.GameState;
import com.engine.League;
import com.interfaces.IPlayer;
import com.interfaces.ISport;
import com.interfaces.ITeam;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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
            simulateAIMatches();
            decrementInjuries();
            // Always advance the week and reset phase to TRAINING_WEEK,
            // regardless of the previous phase. This ensures Week 2, 3, ... start correctly.
            if (GameState.getInstance().getLeague().isSeasonOver()) {
                GameState.getInstance().setPhase(com.engine.Phase.SEASON_END);
            } else {
                GameState.getInstance().advanceWeek();
            }

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
    private void simulateAIMatches() {
        int week = GameState.getInstance().getWeek();
        ITeam managedTeam = GameState.getInstance().getManagedTeam();
        ISport sport = GameState.getInstance().getSport();
        League league = GameState.getInstance().getLeague();

        List<com.engine.Fixture> fixtures = league.getSchedule().getWeekFixtures(week);

        for (com.engine.Fixture f : fixtures) {
            if (f.isPlayed()) continue;
            if (f.getHomeTeam().equals(managedTeam) || f.getAwayTeam().equals(managedTeam)) continue;

            int[] result = simulateByStrength(f.getHomeTeam(), f.getAwayTeam(), sport);
            league.updateStandings(f.getHomeTeam(), result[0], f.getAwayTeam(), result[1]);
            f.setResult(f.getHomeTeam().getName() + " " + result[0] + " - " + result[1] + " " + f.getAwayTeam().getName());
        }

        updateManagedTeamStandings();
    }

    private int[] simulateByStrength(ITeam home, ITeam away, ISport sport) {
        double homeAttack = getTeamAverageOverall(home, "attack");
        double homeDefense = getTeamAverageOverall(home, "defense");
        double awayAttack = getTeamAverageOverall(away, "attack");
        double awayDefense = getTeamAverageOverall(away, "defense");

        // home advantage bonus
        homeAttack *= 1.05;
        homeDefense *= 1.05;

        // score probability based on attack vs opponent defense
        double homeScoreChance = homeAttack / (homeAttack + awayDefense);
        double awayScoreChance = awayAttack / (awayAttack + homeDefense);

        Random random = new Random();

        if (sport.getSportName().equals("Football")) {
            // football: 0-5 goals per team
            int homeGoals = 0, awayGoals = 0;
            for (int i = 0; i < 5; i++) {
                if (random.nextDouble() < homeScoreChance * 0.4) homeGoals++;
                if (random.nextDouble() < awayScoreChance * 0.4) awayGoals++;
            }
            return new int[]{homeGoals, awayGoals};

        } else {
            // volleyball: first to 3 sets
            int homeSets = 0, awaySets = 0;
            while (homeSets < 3 && awaySets < 3) {
                if (random.nextDouble() < homeScoreChance) homeSets++;
                else awaySets++;
            }
            return new int[]{homeSets, awaySets};
        }
    }

    private double getTeamAverageOverall(ITeam team, String overallKey) {
        List<IPlayer> squad = team.getSquad();
        if (squad.isEmpty()) return 50;
        double total = 0;
        int count = 0;
        for (IPlayer player : squad) {
            if (player.getOveralls().containsKey(overallKey)) {
                total += player.getOveralls().get(overallKey);
                count++;
            }
        }
        return count == 0 ? 50 : total / count;
    }

    private void setAILineup(ITeam team, int size) {
        List<IPlayer> available = team.getAvailablePlayers();
        List<IPlayer> lineup = new ArrayList<>();
        for (int i = 0; i < Math.min(size, available.size()); i++) {
            lineup.add(available.get(i));
        }
        if (lineup.size() == size) {
            try { team.setStartingLineup(lineup); } catch (Exception ignored) {}
        }
    }

    private void updateManagedTeamStandings() {
        GameState gs = GameState.getInstance();
        ITeam homeTeam = gs.getLastHomeTeam();
        ITeam awayTeam = gs.getLastAwayTeam();
        int homeScore = gs.getLastHomeScore();
        int awayScore = gs.getLastAwayScore();

        if (homeTeam == null || awayTeam == null || homeScore == -1) return;

        League league = gs.getLeague();
        int week = gs.getWeek();

        List<com.engine.Fixture> fixtures = league.getSchedule().getWeekFixtures(week);
        for (com.engine.Fixture f : fixtures) {
            if (f.isPlayed()) continue;
            if ((f.getHomeTeam().equals(homeTeam) && f.getAwayTeam().equals(awayTeam)) ||
                    (f.getHomeTeam().equals(awayTeam) && f.getAwayTeam().equals(homeTeam))) {
                league.updateStandings(homeTeam, homeScore, awayTeam, awayScore);
                int fHome = f.getHomeTeam().equals(homeTeam) ? homeScore : awayScore;
                int fAway = f.getHomeTeam().equals(homeTeam) ? awayScore : homeScore;
                f.setResult(f.getHomeTeam().getName() + " " + fHome + " - " + fAway + " " + f.getAwayTeam().getName());
                break;
            }
        }
    }
    private void decrementInjuries() {
        for (ITeam team : GameState.getInstance().getLeague().getTeams()) {
            for (IPlayer player : team.getSquad()) {
                if (player.isInjured()) {
                    player.decrementInjury();
                }
            }
        }
    }

    public BorderPane getRoot() { return root; }
}
