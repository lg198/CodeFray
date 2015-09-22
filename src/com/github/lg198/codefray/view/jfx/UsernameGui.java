package com.github.lg198.codefray.view.jfx;


import com.github.lg198.codefray.jfx.CodeFrayApplication;
import com.github.lg198.codefray.net.CodeFrayClient;
import com.github.lg198.codefray.util.ErrorAlert;
import com.github.lg198.codefray.util.Stylizer;
import com.github.lg198.codefray.view.ViewGame;
import com.github.lg198.codefray.view.ViewProfile;
import javafx.application.Platform;
import javafx.geometry.HPos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

import java.io.IOException;

public class UsernameGui {

    public TextField name = new TextField();

    public GridPane build() {
        GridPane grid = new GridPane();
        grid.setHgap(6);
        grid.setVgap(15);

        Label nameLabel = new Label("Username:");
        Stylizer.set(nameLabel, "-fx-font-size", "14px");
        Stylizer.set(name, "-fx-font-size", "14px");
        name.setPromptText("Ex: njonas");

        GridPane.setHalignment(nameLabel, HPos.LEFT);
        GridPane.setHalignment(name, HPos.LEFT);
        GridPane.setHgrow(name, Priority.ALWAYS);

        grid.add(nameLabel, 0, 0);
        grid.add(name, 1, 0);

        Button enter = new Button("Enter");
        Stylizer.set(enter, "-fx-font-size", "14px");
        enter.setOnAction(event -> {
            if (name.getText().trim().isEmpty()) {
                return;
            }
            grid.getScene().getWindow().hide();
            ViewGame game = new ViewGame(new ViewProfile(name.getText()));
            try {
                CodeFrayClient.start("bc.codefraygame.com", game.profile);
            } catch (IOException e) {
                ErrorAlert.createAlert("Error", "Connection Error", "CodeFray failed to connect to the broadcast server. It may be down, or there might not be a game streaming right now.", e);
            }
        });

        GridPane.setColumnSpan(enter, 2);
        grid.add(enter, 0, 1);

        return grid;
    }
}
