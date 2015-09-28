package com.github.lg198.codefray.view.jfx;


import com.github.lg198.codefray.jfx.CodeFrayApplication;
import com.github.lg198.codefray.net.CodeFrayClient;
import com.github.lg198.codefray.util.ErrorAlert;
import com.github.lg198.codefray.util.Stylizer;
import com.github.lg198.codefray.view.ViewGame;
import com.github.lg198.codefray.view.ViewProfile;
import javafx.application.Platform;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

import java.io.IOException;

public class UsernameGui {

    public TextField name = new TextField(), address = new TextField();

    public GridPane build() {
        GridPane grid = new GridPane();
        grid.setHgap(6);
        grid.setVgap(15);
        grid.setPadding(new Insets(15));

        Label addressLabel = new Label("Address:");
        Stylizer.set(addressLabel, "-fx-font-size", "14px");
        Stylizer.set(address, "-fx-font-size", "14px");
        address.setText("bc.codefraygame.com");

        GridPane.setHalignment(addressLabel, HPos.LEFT);
        GridPane.setHalignment(address, HPos.LEFT);
        GridPane.setHgrow(address, Priority.ALWAYS);

        grid.add(addressLabel, 0, 0);
        grid.add(address, 1, 0);

        Label nameLabel = new Label("Username:");
        Stylizer.set(nameLabel, "-fx-font-size", "14px");
        Stylizer.set(name, "-fx-font-size", "14px");

        GridPane.setHalignment(nameLabel, HPos.LEFT);
        GridPane.setHalignment(name, HPos.LEFT);
        GridPane.setHgrow(name, Priority.ALWAYS);

        grid.add(nameLabel, 0, 1);
        grid.add(name, 1, 1);

        Button enter = new Button("Enter");
        Stylizer.set(enter, "-fx-font-size", "14px");
        enter.setOnAction(event -> {
            if (name.getText().trim().isEmpty() || address.getText().trim().isEmpty()) {
                return;
            }
            Thread t = new Thread(() -> {
                ViewGame game = new ViewGame(new ViewProfile(name.getText()));
                try {
                    CodeFrayClient.start(address.getText().trim(), game.profile);
                } catch (IOException e) {
                    e.printStackTrace();
                    ErrorAlert.createAlert("Error", "Connection Error", "CodeFray failed to connect to the broadcast server. It may be down, or there might not be a game streaming right now.", e).showAndWait();
                }
            });
            t.setName("CF-Viewer-Launch");
            t.setDaemon(true);
            t.start();
        });

        GridPane.setColumnSpan(enter, 2);
        GridPane.setHalignment(enter, HPos.RIGHT);
        grid.add(enter, 0, 2);

        name.requestFocus();

        return grid;
    }
}
