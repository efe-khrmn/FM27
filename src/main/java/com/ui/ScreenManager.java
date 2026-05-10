package com.ui;

import com.engine.GameState;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ScreenManager {

    private Stage stage;
    private static final int WIDTH = 1280;
    private static final int HEIGHT = 720;

    public ScreenManager(Stage stage) {
        this.stage = stage;
        this.stage.setTitle("Sports Manager — Karsiyaka Seagulls");
        this.stage.setResizable(true);
        this.stage.setMinWidth(WIDTH);
        this.stage.setMinHeight(HEIGHT);
    }

    private void setScreen(Parent root) {
        Scene currentScene = stage.getScene();
        double sceneWidth = currentScene != null ? currentScene.getWidth() : WIDTH;
        double sceneHeight = currentScene != null ? currentScene.getHeight() : HEIGHT;
        double stageWidth = stage.getWidth();
        double stageHeight = stage.getHeight();
        boolean wasMaximized = stage.isMaximized();

        stage.setScene(new Scene(root, sceneWidth, sceneHeight));

        if (wasMaximized) {
            stage.setMaximized(true);
        } else if (stageWidth > 0 && stageHeight > 0) {
            stage.setWidth(stageWidth);
            stage.setHeight(stageHeight);
        }
    }

    public void showStartScreen() {
        StartScreen screen = new StartScreen(this);
        setScreen(screen.getRoot());
        stage.show();
    }

    public void showLeagueTableScreen() {
        LeagueTableScreen screen = new LeagueTableScreen(this);
        setScreen(screen.getRoot());
    }

    public void showScheduleScreen() {
        ScheduleScreen screen = new ScheduleScreen(this);
        setScreen(screen.getRoot());
    }

    public void showSquadScreen() {
        SquadScreen screen = new SquadScreen(this);
        setScreen(screen.getRoot());
    }


    public void showLineupScreen() {
        LineupScreen screen = new LineupScreen(this);
        setScreen(screen.getRoot());
    }
    public void showTrainingScreen() {
        TrainingScreen screen = new TrainingScreen(this);
        setScreen(screen.getRoot());
    }

    public void showPreMatchScreen() {
        PreMatchScreen screen = new PreMatchScreen(this);
        setScreen(screen.getRoot());
    }

    public void showMatchScreen() {
        MatchScreen screen = new MatchScreen(this);
        setScreen(screen.getRoot());
    }

    public void showPostMatchScreen() {
        PostMatchScreen screen = new PostMatchScreen(this);
        setScreen(screen.getRoot());
    }

    public void showSeasonEndScreen() {
        SeasonEndScreen screen = new SeasonEndScreen(this);
        setScreen(screen.getRoot());
    }
}