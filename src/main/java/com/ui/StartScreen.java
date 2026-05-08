package com.ui;

import com.data.PlayerLoader;
import com.engine.GameState;
import com.engine.League;
import com.engine.SaveManager;
import com.engine.SportFactory;
import com.interfaces.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static com.utils.PositionUtils.getPositionGroup;

public class StartScreen {

    private ScreenManager manager;
    private BorderPane root;

    public StartScreen(ScreenManager manager) {
        this.manager = manager;
        PlayerLoader.reset();
        build();
    }

    private void build() {
        root = new BorderPane();
        root.setStyle("-fx-background-color: " + UIStyles.BG_DARK + ";");

        VBox center = new VBox(20);
        center.setAlignment(Pos.CENTER);
        center.setPadding(new Insets(40));

        Canvas logo = drawLogo();
        center.getChildren().add(logo);

        // Sport selection
        Label selectLabel = new Label("Select Sport");
        selectLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: white;");

        ComboBox<String> sportBox = new ComboBox<>();
        sportBox.getItems().addAll("Football", "Volleyball");
        sportBox.setValue("Football");
        sportBox.setStyle(
                "-fx-background-color: #0f3460;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 14px;" +
                        "-fx-min-width: 260px;"
        );
        // fix dropdown text color
        sportBox.setCellFactory(lv -> new ListCell<String>() {
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (item != null) {
                    setText(item);
                    setStyle("-fx-text-fill: white; -fx-background-color: #0f3460; -fx-font-size: 14px;");
                }
            }
        });
        sportBox.setButtonCell(new ListCell<String>() {
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (item != null) {
                    setText(item);
                    setStyle("-fx-text-fill: white; -fx-font-size: 14px;");
                }
            }
        });

        // Team selection
        Label teamSelectLabel = new Label("Select Your Team");
        teamSelectLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: white;");

        String[] footballTeamNames = {
                "Karsiyaka Seagulls", "Spartak Bornova", "Olimpik Buca", "Konak City",
                "Bayrakli United", "Bostanli Athletic", "Gaziemir FC", "Narlidere Saint Germain",
                "Balcova Idman Yurdu", "Goztepe Eagles", "Karabaglar Rovers", "Menderes Wanderers"
        };
        String[] volleyballTeamNames = {
                "Karsiyaka Seagulls VB", "Bornova Spikers", "Buca Blockers", "Konak Aces",
                "Bayrakli Setters", "Bostanli Diggers", "Gaziemir Smashers", "Narlidere Jumpers",
                "Balcova Servers", "Goztepe Hitters", "Karabaglar Liberos", "Menderes Nets"
        };

        ComboBox<String> teamBox = new ComboBox<>();
        teamBox.getItems().addAll(footballTeamNames);
        teamBox.setValue(footballTeamNames[0]);
        teamBox.setStyle(
                "-fx-background-color: #0f3460;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 14px;" +
                        "-fx-min-width: 260px;"
        );
        teamBox.setCellFactory(lv -> new ListCell<String>() {
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (item != null) {
                    setText(item);
                    setStyle("-fx-text-fill: white; -fx-background-color: #0f3460; -fx-font-size: 14px;");
                }
            }
        });
        teamBox.setButtonCell(new ListCell<String>() {
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (item != null) {
                    setText(item);
                    setStyle("-fx-text-fill: white; -fx-font-size: 14px;");
                }
            }
        });

        // update team list when sport changes
        sportBox.setOnAction(e -> {
            teamBox.getItems().clear();
            if (sportBox.getValue().equals("Football")) {
                teamBox.getItems().addAll(footballTeamNames);
            } else {
                teamBox.getItems().addAll(volleyballTeamNames);
            }
            teamBox.setValue(teamBox.getItems().get(0));
        });

        // Buttons
        Button newGameBtn = new Button("New Game");
        newGameBtn.setStyle(UIStyles.BTN_PRIMARY);
        newGameBtn.setMinWidth(260);

        Button loadGameBtn = new Button("Load Game");
        loadGameBtn.setStyle(UIStyles.BTN_SECONDARY);
        loadGameBtn.setMinWidth(260);

        newGameBtn.setOnAction(e -> startNewGame(sportBox.getValue(), teamBox.getValue()));
        loadGameBtn.setOnAction(e -> loadGame());

        Label version = new Label("\u00a9Karsiyaka Seagulls");
        version.setStyle("-fx-font-size: 12px; -fx-text-fill: #555555;");

        center.getChildren().addAll(
                selectLabel, sportBox,
                teamSelectLabel, teamBox,
                newGameBtn, loadGameBtn,
                version
        );
        root.setCenter(center);
    }

    private Canvas drawLogo() {
        Canvas canvas = new Canvas(400, 200);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, 400, 200);
        gc.setFill(Color.WHITE);
        gc.fillOval(162, 80, 76, 32);
        gc.fillOval(228, 64, 28, 24);
        gc.fillPolygon(new double[]{254, 272, 254}, new double[]{74, 78, 82}, 3);
        gc.setFill(Color.web(UIStyles.BG_DARK));
        gc.fillOval(242, 69, 6, 6);
        gc.setFill(Color.WHITE);
        gc.fillOval(243, 70, 3, 3);
        gc.setFill(Color.WHITE);
        gc.fillPolygon(new double[]{162,120,80,50,100,155}, new double[]{88,60,48,55,70,88}, 6);
        gc.fillPolygon(new double[]{238,270,310,340,300,242}, new double[]{82,56,44,50,68,82}, 6);
        gc.fillPolygon(new double[]{162,145,130,150}, new double[]{100,118,128,105}, 4);
        gc.fillPolygon(new double[]{165,152,140,160}, new double[]{104,125,138,108}, 4);
        gc.setStroke(Color.WHITE);
        gc.setLineWidth(1.5);
        gc.strokeLine(60, 130, 340, 130);
        gc.setLineWidth(0.8);
        gc.strokeLine(80, 135, 320, 135);
        gc.setFill(Color.WHITE);
        gc.setFont(Font.font("Arial", FontWeight.BLACK, 32));
        gc.fillText("KARSIYAKA", 88, 165);
        gc.setFill(Color.web(UIStyles.ACCENT_BLUE));
        gc.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        gc.fillText("S  E  A  G  U  L  L  S", 118, 190);
        return canvas;
    }

    private void startNewGame(String sportName, String selectedTeamName) {
        ISport sport = SportFactory.create(sportName);
        League league = new League(sport);
        PlayerLoader.reset();

        String[] footballTeamNames = {
                "Karsiyaka Seagulls", "Spartak Bornova", "Olimpik Buca", "Konak City",
                "Bayrakli United", "Bostanli Athletic", "Gaziemir FC", "Narlidere Saint Germain",
                "Balcova Idman Yurdu", "Goztepe Eagles", "Karabaglar Rovers", "Menderes Wanderers"
        };
        String[] volleyballTeamNames = {
                "Karsiyaka Seagulls VB", "Bornova Spikers", "Buca Blockers", "Konak Aces",
                "Bayrakli Setters", "Bostanli Diggers", "Gaziemir Smashers", "Narlidere Jumpers",
                "Balcova Servers", "Goztepe Hitters", "Karabaglar Liberos", "Menderes Nets"
        };
        String[] footballCoachNames = {
                "Cinar Gedizlioglu", "Umut Ege Taner", "Turhan Tunali", "Roberto Mancini",
                "Haciz Yıldırım", "Yusuf Murat Erten", "Ahmet Emre Yagci", "Fatih Terim",
                "Kaya Oguz", "Ersun Yanal", "Senol Gunes", "Aykut Kocaman"
        };
        String[] volleyballCoachNames = {
                "Giovanni Guidetti", "Daniele Santarelli", "Julio Velasco", "Ferhat Akbas",
                "Marco Bonitta", "Stefano Lavarini", "Massimo Barbolini", "Zoran Terzic",
                "Karch Kiraly", "Efe Berk Dagli", "Andrea Anastasi", "Bernardinho Rezende"
        };

        String[] teamNames = sportName.equals("Football") ? footballTeamNames : volleyballTeamNames;
        String[] coachNames = sportName.equals("Football") ? footballCoachNames : volleyballCoachNames;
        Random random = new Random();
        ITeam managedTeam = null;

        for (int t = 0; t < teamNames.length; t++) {
            boolean isManaged = teamNames[t].equals(selectedTeamName);
            ITeam team = sport.createTeam(teamNames[t], "logo" + t);

            if (isManaged) {
                if (team instanceof com.football.FootballTeam)
                    ((com.football.FootballTeam) team).setPlayerManaged(true);
                else if (team instanceof com.volleyball.VolleyballTeam)
                    ((com.volleyball.VolleyballTeam) team).setPlayerManaged(true);
            }

            ICoach coach = createCoach(sportName, coachNames[t], random, true);
            addCoachToTeam(team, coach, sportName);

            List<String> positions = sport.getPositions();
            int squadSize = 18;

            for (int p = 0; p < squadSize; p++) {
                String pos = positions.get(p % positions.size());
                String playerName = PlayerLoader.getNext();
                int age = 18 + random.nextInt(18);

                String group = getPositionGroup(pos);

                int atk, def;

                switch (group) {
                    case "attack":
                        atk = 70 + random.nextInt(30);
                        def = 30 + random.nextInt(40);
                        break;

                    case "midfield":
                        atk = 50 + random.nextInt(40);
                        def = 50 + random.nextInt(40);
                        break;

                    case "defense":
                        atk = 30 + random.nextInt(40);
                        def = 70 + random.nextInt(30);
                        break;

                    case "goalkeeper":
                        atk = 20 + random.nextInt(20);
                        def = 80 + random.nextInt(20);
                        break;

                    default:
                        atk = 40 + random.nextInt(60);
                        def = 40 + random.nextInt(60);
                }

                IPlayer player = createPlayer(
                        sportName,
                        playerName,
                        p + 1,
                        age,
                        pos,
                        atk,
                        def
                );

                addPlayerToTeam(team, player, sportName);
            }

            team.setTactic(createDefaultTactic(sportName));
            league.addTeam(team);
            if (isManaged) managedTeam = team;
        }

        league.startSeason();
        GameState gs = GameState.getInstance();
        gs.newGame(sport, managedTeam);
        gs.setLeague(league);
        manager.showLeagueTableScreen();
    }

    private IPlayer createPlayer(String sportName, String name, int number,
                                 int age, String pos, int atk, int def) {
        if (sportName.equals("Football")) {
            return new com.football.FootballPlayer(name, number, age, pos, atk, def);
        } else {
            return new com.volleyball.VolleyballPlayer(name, number, age, pos, atk, def);
        }
    }

    private ICoach createCoach(String sportName, String name, Random random, boolean isHead) {
        List<String> specs = Arrays.asList("Attack", "Defense");
        int level = 1 + random.nextInt(5);
        if (sportName.equals("Football")) {
            return new com.football.FootballCoach(name, level, isHead, specs);
        } else {
            return new com.volleyball.VolleyballCoach(name, level, isHead, specs);
        }
    }

    private void addCoachToTeam(ITeam team, ICoach coach, String sportName) {
        if (sportName.equals("Football")) {
            ((com.football.FootballTeam) team).addCoach(coach);
        } else {
            ((com.volleyball.VolleyballTeam) team).addCoach(coach);
        }
    }

    private void addPlayerToTeam(ITeam team, IPlayer player, String sportName) {
        if (sportName.equals("Football")) {
            ((com.football.FootballTeam) team).addPlayer(player);
        } else {
            ((com.volleyball.VolleyballTeam) team).addPlayer(player);
        }
    }

    private ITactic createDefaultTactic(String sportName) {
        if (sportName.equals("Football")) {
            return new com.football.FootballTactic("4-4-2");
        } else {
            return new com.volleyball.VolleyballTactic("5-1");
        }
    }

    private void loadGame() {
        try {
            SaveManager.load();
            manager.showLeagueTableScreen();
        } catch (Exception e) {
            System.out.println("No save file found.");
        }
    }

    public BorderPane getRoot() { return root; }
}
