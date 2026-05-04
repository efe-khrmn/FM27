package com.ui;

import com.engine.GameState;
import com.interfaces.IPlayer;
import com.interfaces.ISport;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.util.List;
import java.util.Map;

public class SquadScreen {

    private ScreenManager manager;
    private BorderPane root;

    public SquadScreen(ScreenManager manager) {
        this.manager = manager;
        build();
    }

    private void build() {
        root = new BorderPane();
        root.setStyle("-fx-background-color: " + UIStyles.BG_DARK + ";");
        root.setTop(buildNavBar());

        VBox center = new VBox(16);
        center.setPadding(new Insets(24));

        ISport sport = GameState.getInstance().getSport();
        List<IPlayer> squad = GameState.getInstance().getManagedTeam().getSquad();

        TableView<IPlayer> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.getStylesheets().add(
                getClass().getResource("/styles.css").toExternalForm()
        );

        // # column
        TableColumn<IPlayer, Void> numCol = new TableColumn<>("#");
        numCol.setMaxWidth(50);
        numCol.setCellFactory(col -> new TableCell<IPlayer, Void>() {
            @Override protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (!empty && getIndex() < squad.size()) {
                    setText(String.valueOf(squad.get(getIndex()).getNumber()));
                    setStyle("-fx-text-fill: white; -fx-alignment: center;");
                } else { setText(null); }
            }
        });

        // Name
        TableColumn<IPlayer, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getName()));
        nameCol.setMinWidth(180);
        nameCol.setCellFactory(col -> new TableCell<IPlayer, String>() {
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (item != null && !empty) {
                    setText(item);
                    setStyle("-fx-text-fill: white; -fx-font-weight: bold;");
                } else { setText(null); }
            }
        });

        // Position
        TableColumn<IPlayer, String> posCol = new TableColumn<>("Pos");
        posCol.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getPosition()));
        posCol.setMaxWidth(60);
        posCol.setCellFactory(col -> new TableCell<IPlayer, String>() {
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (item != null && !empty) {
                    setText(item);
                    setStyle("-fx-text-fill: white; -fx-alignment: center;");
                } else { setText(null); }
            }
        });

        // Age
        TableColumn<IPlayer, String> ageCol = new TableColumn<>("Age");
        ageCol.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(
                        String.valueOf(data.getValue().getAge())));
        ageCol.setMaxWidth(50);
        ageCol.setCellFactory(col -> new TableCell<IPlayer, String>() {
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (item != null && !empty) {
                    setText(item);
                    setStyle("-fx-text-fill: white; -fx-alignment: center;");
                } else { setText(null); }
            }
        });

        // Stamina
        TableColumn<IPlayer, String> staminaCol = new TableColumn<>("Stamina");
        staminaCol.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(
                        String.valueOf(data.getValue().getStamina())));
        staminaCol.setMaxWidth(70);
        staminaCol.setCellFactory(col -> new TableCell<IPlayer, String>() {
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (item != null && !empty) {
                    setText(item);
                    setStyle("-fx-text-fill: white; -fx-alignment: center;");
                } else { setText(null); }
            }
        });

        // Compat
        TableColumn<IPlayer, String> compatCol = new TableColumn<>("Compat");
        compatCol.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(
                        String.valueOf(data.getValue().getTacticCompatibility())));
        compatCol.setMaxWidth(70);
        compatCol.setCellFactory(col -> new TableCell<IPlayer, String>() {
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (item != null && !empty) {
                    setText(item);
                    setStyle("-fx-text-fill: white; -fx-alignment: center;");
                } else { setText(null); }
            }
        });

        // Status
        TableColumn<IPlayer, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(
                        data.getValue().isInjured()
                                ? "Injured (" + data.getValue().getInjuryGames() + ")"
                                : "Available"));
        statusCol.setMaxWidth(120);
        statusCol.setCellFactory(col -> new TableCell<IPlayer, String>() {
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (item != null && !empty) {
                    setText(item);
                    setStyle(item.startsWith("Injured")
                            ? "-fx-text-fill: #e74c3c; -fx-alignment: center;"
                            : "-fx-text-fill: #2ecc71; -fx-alignment: center;");
                } else { setText(null); }
            }
        });

        table.getColumns().addAll(numCol, nameCol, posCol, ageCol, staminaCol, compatCol, statusCol);

        // Dynamic overall columns — read directly from getOveralls() map
        for (String overallType : sport.getOverallTypes()) {
            TableColumn<IPlayer, String> col = new TableColumn<>(
                    overallType.substring(0, 1).toUpperCase() + overallType.substring(1));
            col.setMaxWidth(80);
            col.setCellValueFactory(data -> {
                Map<String, Integer> overalls = data.getValue().getOveralls();
                int val = overalls.containsKey(overallType) ? overalls.get(overallType) : 0;
                return new javafx.beans.property.SimpleStringProperty(String.valueOf(val));
            });
            col.setCellFactory(c -> new TableCell<IPlayer, String>() {
                @Override protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (item != null && !empty) {
                        setText(item);
                        setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-alignment: center;");
                    } else { setText(null); }
                }
            });
            table.getColumns().add(col);
        }

        table.getItems().addAll(squad);

        // injured row highlight
        table.setRowFactory(tv -> new TableRow<IPlayer>() {
            @Override protected void updateItem(IPlayer item, boolean empty) {
                super.updateItem(item, empty);
                if (item != null && item.isInjured()) {
                    setStyle("-fx-background-color: #3a1a1a;");
                } else {
                    setStyle("");
                }
            }
        });

        center.getChildren().add(table);
        root.setCenter(center);
        root.setBottom(buildBackBtn());
    }

    private HBox buildNavBar() {
        HBox nav = new HBox();
        nav.setPadding(new Insets(16, 24, 16, 24));
        nav.setStyle("-fx-background-color: " + UIStyles.BG_PANEL + ";");
        nav.setAlignment(Pos.CENTER_LEFT);
        Label title = new Label("Squad — " + GameState.getInstance().getManagedTeam().getName());
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