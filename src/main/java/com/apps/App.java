package com.apps;

import com.ui.ScreenManager;
import javafx.application.Application;
import javafx.stage.Stage;

public class
App extends Application {

    @Override
    public void start(Stage primaryStage) {
        ScreenManager manager = new ScreenManager(primaryStage);
        manager.showStartScreen();
    }

    public static void main(String[] args) {
        launch(args);
    }
}