package com.ui;

import com.engine.GameState;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ScreenManager {

    private Stage stage;
    private static final int WIDTH = 1280;
    private static final int HEIGHT = 720;

    public ScreenManager(Stage stage) {
        this.stage = stage;
        this.stage.setTitle("Sports Manager — Karsiyaka Seagulls");
        this.stage.setResizable(false);
    }

    public void showStartScreen() {
        StartScreen screen = new StartScreen(this);
        stage.setScene(new Scene(screen.getRoot(), WIDTH, HEIGHT));
        stage.show();
    }

    public void showLeagueTableScreen() {
        LeagueTableScreen screen = new LeagueTableScreen(this);
        stage.setScene(new Scene(screen.getRoot(), WIDTH, HEIGHT));
    }

    public void showScheduleScreen() {
        ScheduleScreen screen = new ScheduleScreen(this);
        stage.setScene(new Scene(screen.getRoot(), WIDTH, HEIGHT));
    }

    public void showSquadScreen() {
        SquadScreen screen = new SquadScreen(this);
        stage.setScene(new Scene(screen.getRoot(), WIDTH, HEIGHT));
    }


    public void showLineupScreen() {
        LineupScreen screen = new LineupScreen(this);
        stage.setScene(new Scene(screen.getRoot(), WIDTH, HEIGHT));
    }
    public void showTrainingScreen() {
        TrainingScreen screen = new TrainingScreen(this);
        stage.setScene(new Scene(screen.getRoot(), WIDTH, HEIGHT));
    }

    public void showPreMatchScreen() {
        PreMatchScreen screen = new PreMatchScreen(this);
        stage.setScene(new Scene(screen.getRoot(), WIDTH, HEIGHT));
    }

    public void showMatchScreen() {
        MatchScreen screen = new MatchScreen(this);
        stage.setScene(new Scene(screen.getRoot(), WIDTH, HEIGHT));
    }

    public void showPostMatchScreen() {
        PostMatchScreen screen = new PostMatchScreen(this);
        stage.setScene(new Scene(screen.getRoot(), WIDTH, HEIGHT));
    }

    public void showSeasonEndScreen() {
        SeasonEndScreen screen = new SeasonEndScreen(this);
        stage.setScene(new Scene(screen.getRoot(), WIDTH, HEIGHT));
    }
}