package com.ui; // Hatalı package ismi düzeltildi

import com.engine.GameState;
import com.interfaces.IPlayer;
import com.interfaces.ISport;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
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
        table.setStyle(UIStyles.TABLE_STYLE);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // Name Column
        TableColumn<IPlayer, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getName()));
        nameCol.setMinWidth(200);

        // Number Column
        TableColumn<IPlayer, Integer> numCol = new TableColumn<>("#");
        numCol.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getNumber()).asObject());
        numCol.setMaxWidth(50);

        // Position Column
        TableColumn<IPlayer, String> posCol = new TableColumn<>("Pos");
        posCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getPosition()));
        posCol.setMaxWidth(60);

        // Status Column
        TableColumn<IPlayer, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(data -> {
            IPlayer p = data.getValue();
            return new SimpleStringProperty(p.isInjured() ? "Injured (" + p.getInjuryGames() + ")" : "Available");
        });
        statusCol.setMinWidth(100);

        table.getColumns().addAll(nameCol, numCol, posCol, statusCol);

        // Dynamic Columns
        for (String overallType : sport.getOverallTypes()) {
            TableColumn<IPlayer, Integer> col = new TableColumn<>(capitalize(overallType));
            col.setCellValueFactory(data -> {
                int val = data.getValue().getOveralls().getOrDefault(overallType, 0);
                return new SimpleIntegerProperty(val).asObject();
            });
            col.setMaxWidth(80);
            table.getColumns().add(col);
        }

        table.getItems().addAll(squad);

        // Row factory with empty check
        table.setRowFactory(tv -> new TableRow<IPlayer>() {
            @Override
            protected void updateItem(IPlayer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setStyle("");
                } else if (item.isInjured()) {
                    setStyle("-fx-background-color: #4a1a1a; -fx-text-background-color: white;");
                } else {
                    setStyle("");
                }
            }
        });

        center.getChildren().add(table);
        root.setCenter(center);
        root.setBottom(buildBackBtn());
    }

    private String capitalize(String s) {
        if (s == null || s.isEmpty()) return s;
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }

    private HBox buildNavBar() {
        HBox nav = new HBox();
        nav.setPadding(new Insets(16, 24, 16, 24));
        nav.setStyle("-fx-background-color: " + UIStyles.BG_PANEL + ";");
        nav.setAlignment(Pos.CENTER_LEFT);

        String teamName = (GameState.getInstance().getManagedTeam() != null) ?
                GameState.getInstance().getManagedTeam().getName() : "Unknown Team";

        Label title = new Label("Squad — " + teamName);
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