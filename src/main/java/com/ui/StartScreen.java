package com.ui;

import com.engine.GameState;
import com.engine.League;
import com.engine.SaveManager;
import com.engine.SportFactory;
import com.football.FootballCoach;
import com.football.FootballPlayer;
import com.football.FootballTeam;
import com.interfaces.ISport;
import com.interfaces.ITeam;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.Arrays;
import java.util.Random;

public class StartScreen {

    private ScreenManager manager;
    private BorderPane root;

    public StartScreen(ScreenManager manager) {
        this.manager = manager;
        build();
    }

    private void build() {
        root = new BorderPane();
        root.setStyle("-fx-background-color: " + UIStyles.BG_DARK + ";");

        // Center content
        VBox center = new VBox(24);
        center.setAlignment(Pos.CENTER);
        center.setPadding(new Insets(40));

        // Logo canvas
        Canvas logo = drawLogo();
        center.getChildren().add(logo);

        // Sport selection
        Label selectLabel = new Label("Select Sport");
        selectLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #aaaaaa;");

        ComboBox<String> sportBox = new ComboBox<>();
        sportBox.getItems().addAll("Football", "Volleyball");
        sportBox.setValue("Football");
        sportBox.setStyle("-fx-background-color: #0f3460; -fx-text-fill: white; -fx-font-size: 14px; -fx-min-width: 200px;");

        // Buttons
        Button newGameBtn = new Button("New Game");
        newGameBtn.setStyle(UIStyles.BTN_PRIMARY);
        newGameBtn.setMinWidth(200);

        Button loadGameBtn = new Button("Load Game");
        loadGameBtn.setStyle(UIStyles.BTN_SECONDARY);
        loadGameBtn.setMinWidth(200);

        newGameBtn.setOnAction(e -> startNewGame(sportBox.getValue()));
        loadGameBtn.setOnAction(e -> loadGame());

        // Version label
        Label version = new Label("©Karsiyaka Seagulls");
        version.setStyle("-fx-font-size: 12px; -fx-text-fill: #555555;");

        center.getChildren().addAll(selectLabel, sportBox, newGameBtn, loadGameBtn, version);
        root.setCenter(center);
    }

    private Canvas drawLogo() {
        Canvas canvas = new Canvas(400, 200);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        // Background transparent
        gc.clearRect(0, 0, 400, 200);

        // Seagull body
        gc.setFill(Color.WHITE);
        gc.fillOval(162, 80, 76, 32);

        // Head
        gc.fillOval(228, 64, 28, 24);

        // Beak
        gc.fillPolygon(new double[]{254, 272, 254}, new double[]{74, 78, 82}, 3);

        // Eye
        gc.setFill(Color.web(UIStyles.BG_DARK));
        gc.fillOval(242, 69, 6, 6);
        gc.setFill(Color.WHITE);
        gc.fillOval(243, 70, 3, 3);

        // Left wing
        gc.setFill(Color.WHITE);
        gc.fillPolygon(
                new double[]{162, 120, 80,  50, 100, 155},
                new double[]{88,  60,  48,  55, 70,  88}, 6);

        // Right wing
        gc.fillPolygon(
                new double[]{238, 270, 310, 340, 300, 242},
                new double[]{82,  56,  44,  50,  68,  82}, 6);

        // Tail
        gc.fillPolygon(
                new double[]{162, 145, 130, 150},
                new double[]{100, 118, 128, 105}, 4);
        gc.fillPolygon(
                new double[]{165, 152, 140, 160},
                new double[]{104, 125, 138, 108}, 4);

        // Decorative lines
        gc.setStroke(Color.WHITE);
        gc.setLineWidth(1.5);
        gc.strokeLine(60, 130, 340, 130);
        gc.setLineWidth(0.8);
        gc.strokeLine(80, 135, 320, 135);

        // Main text
        gc.setFill(Color.WHITE);
        gc.setFont(Font.font("Arial", FontWeight.BLACK, 32));
        gc.fillText("KARSIYAKA", 88, 165);

        // Sub text
        gc.setFill(Color.web(UIStyles.ACCENT_BLUE));
        gc.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        gc.fillText("S  E  A  G  U  L  L  S", 118, 190);

        return canvas;
    }

    private void startNewGame(String sportName) {
        ISport sport = SportFactory.create(sportName);
        League league = new League(sport);

        // generate 12 teams
        String[] teamNames = {
                "Karsiyaka Seagulls", "Spartak Bornova", "Olimpik Buca", "Konak City",
                "Bayrakli United", "Bostanli Athletic", "Gaziemir FC", "Narlidere Saint Germain",
                "Balcova Idman Yurdu", "Goztepe Eagles", "Karabaglar Rovers", "Menderes Wanderers"
        };

        Random random = new Random();
        String[] positions = {"GK","CB","CB","LB","RB","CM","CM","CAM","LW","RW","ST",
                "CB","CM","ST","LW"};

        ITeam managedTeam = null;

        for (int t = 0; t < teamNames.length; t++) {
            boolean isManaged = t == 0;
            FootballTeam team = new FootballTeam(teamNames[t], "logo" + t, isManaged);

            // add head coach
            team.addCoach(new FootballCoach(
                    "Coach " + teamNames[t], 1 + random.nextInt(5), true,
                    Arrays.asList("Attack", "Defense")
            ));

            // add 15 players
            for (int p = 0; p < 15; p++) {
                String pos = positions[p % positions.length];
                int age = 18 + random.nextInt(18);
                int atk = 40 + random.nextInt(40);
                int def = 40 + random.nextInt(40);
                team.addPlayer(new FootballPlayer(
                        "Player " + (p+1) + " " + teamNames[t],
                        p + 1, age, pos, atk, def
                ));
            }

            league.addTeam(team);
            if (isManaged) managedTeam = team;
        }

        league.startSeason();

        GameState gs = GameState.getInstance();
        gs.newGame(sport, managedTeam);
        gs.setLeague(league);

        manager.showLeagueTableScreen();
    }

    private void loadGame() {
        try {
            SaveManager.load();
            manager.showLeagueTableScreen();
        } catch (Exception e) {
            // show error
            System.out.println("No save file found.");
        }
    }

    public BorderPane getRoot() { return root; }
}