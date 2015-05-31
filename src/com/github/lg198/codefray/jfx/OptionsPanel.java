package com.github.lg198.codefray.jfx;

import com.github.lg198.codefray.api.game.Team;
import com.github.lg198.codefray.game.CFGame;
import com.github.lg198.codefray.game.golem.CFGolem;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.util.ListIterator;

public class OptionsPanel {

    private CFGame game;

    public OptionsPanel(CFGame g) {
        game = g;
    }

    private VBox vbox = new VBox();

    private Slider speedSlider = new Slider();
    private ProgressBar blueHealth, redHealth;

    private Label roundNumber = new Label("1");

    public void update() {
        roundNumber.setText("" + game.getRound());
        double bh = game.getPercentHealth(Team.BLUE);
        double rh = game.getPercentHealth(Team.RED);
        blueHealth.setProgress(bh);
        redHealth.setProgress(rh);
    }

    public VBox build() {
        vbox.setSpacing(20);
        vbox.setPadding(new Insets(10));
        vbox.getChildren().add(buildGameBox());

        Label speedLabel = new Label("Clock Speed:");
        speedSlider.setMin(30);
        speedSlider.setMax(120);
        speedSlider.setBlockIncrement(10);
        speedSlider.setValue(45);
        speedSlider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> val, Number n1, Number n2) {
                double newSpeed = (Double) val.getValue();
                newSpeed = 60d / newSpeed;
                newSpeed *= 1000d;
                game.setClockSpeed((int) newSpeed);
            }
        });
        HBox speedBox = new HBox();
        speedBox.setSpacing(10);
        speedBox.getChildren().addAll(speedLabel, speedSlider);
        vbox.getChildren().add(speedBox);

        return vbox;
    }

    private VBox buildGameBox() {
        VBox box = new VBox();
        box.setPadding(new Insets(10));
        box.setStyle("-fx-border-color: black; -fx-border-width: 4px");
        box.setSpacing(10);
        box.setAlignment(Pos.CENTER);

        Label title = new Label("Current Game:");
        title.setStyle("-fx-font-size: 20px");
        box.getChildren().add(title);

        HBox roundBox = new HBox();
        roundBox.setSpacing(6);
        roundBox.setAlignment(Pos.CENTER);
        Label round = new Label("Round");
        round.setStyle("-fx-font-size: 14px");
        roundNumber.setStyle("-fx-font-size: 15px; -fx-text-fill: lightblue; -fx-font-weight: bold");
        roundBox.getChildren().addAll(round, roundNumber);
        box.getChildren().add(roundBox);

        VBox healthv = new VBox();
        healthv.setAlignment(Pos.CENTER);
        HBox redhealthh = new HBox(), bluehealthh = new HBox();
        redhealthh.setAlignment(Pos.CENTER);
        bluehealthh.setAlignment(Pos.CENTER);
        redhealthh.setSpacing(3);
        bluehealthh.setSpacing(3);
        healthv.setSpacing(6);

        redHealth = new ProgressBar();
        redHealth.setProgress(1);
        blueHealth = new ProgressBar();
        blueHealth.setProgress(1);

        redHealth.setStyle("-fx-accent: red");
        blueHealth.setStyle("-fx-accent: blue");

        redhealthh.getChildren().addAll(new Label("Red Health:"), redHealth);
        bluehealthh.getChildren().addAll(new Label("Blue Health:"), blueHealth);
        healthv.getChildren().addAll(redhealthh, bluehealthh);
        box.getChildren().add(healthv);

        final Button pauseButton = new Button("Pause");
        pauseButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                if (game.isPaused()) {
                    pauseButton.setText("Pause");
                    game.unpause();
                } else {
                    pauseButton.setText("Resume");
                    game.pause();
                }
            }
        });
        Button stopButton = new Button("Stop");
        stopButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                game.stop(null);
            }
        });

        HBox buttons = new HBox();
        buttons.setSpacing(6);
        buttons.setAlignment(Pos.CENTER);
        buttons.getChildren().addAll(pauseButton, stopButton);
        box.getChildren().add(buttons);

        return box;
    }

    public GridPane buildGolemBox(CFGolem g) {
        GridPane gp = new GridPane();
        gp.setPadding(new Insets(10));
        gp.setStyle("-fx-border-color: black; -fx-border-width: 4px");
        gp.setHgap(6);
        gp.setVgap(10);

        Label title = new Label("Selected Golem:");
        title.setStyle("-fx-font-size: 20px");
        GridPane.setHalignment(title, HPos.CENTER);
        gp.add(title, 0, 0, 2, 1);

        Label id = new Label("Id:");
        id.setStyle("-fx-font-size: 16px");
        Label idtext = new Label(""+g.getId());
        idtext.setStyle("-fx-font-size: 16px; -fx-text-fill: aquamarine");
        GridPane.setHalignment(id, HPos.RIGHT);
        GridPane.setHalignment(idtext, HPos.LEFT);
        gp.add(id, 0, 1);
        gp.add(idtext, 1, 1);

        Label team = new Label(g.getTeam().name().substring(0, 1) + g.getTeam().name().substring(1).toLowerCase());
        team.setStyle("-fx-font-size: 16px; -fx-text-fill: " + g.getTeam().name());
        GridPane.setHalignment(team, HPos.CENTER);
        gp.add(team, 0, 2, 2, 1);

        ProgressBar health = new ProgressBar((double) g.getHealth() / (double) g.getType().getMaxHealth());
        health.setPrefHeight(16);
        health.setStyle("-fx-accent: " + g.getTeam().name().toLowerCase());
        GridPane.setHalignment(health, HPos.CENTER);
        gp.add(health, 0, 3, 2, 1);

        gp.getProperties().put("golemBox", true);

        ColumnConstraints cc = new ColumnConstraints();
        cc.setPercentWidth(50);
        gp.getColumnConstraints().addAll(cc, cc);


        return gp;
    }

    public void removeGolemBox() {
        ListIterator<Node> ni = vbox.getChildren().listIterator();
        while (ni.hasNext()) {
            Node n = ni.next();
            if (n.getProperties().containsKey("golemBox")) {
                ni.remove();
            }
        }
    }

    public void golemSelected(CFGolem g) {
        removeGolemBox();

        vbox.getChildren().add(buildGolemBox(g));
    }
}
