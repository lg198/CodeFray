package com.github.lg198.codefray.jfx;

import com.github.lg198.codefray.api.game.Team;
import com.github.lg198.codefray.game.CFGame;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

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

    private GridPane buildGameBox() {
        GridPane gp = new GridPane();
        gp.setPadding(new Insets(10));
        gp.setStyle("-fx-border-color: black; -fx-border-width: 4px");
        gp.setHgap(6);
        gp.setVgap(10);

        Label title = new Label("Current Game:");
        title.setStyle("-fx-font-size: 20px");
        GridPane.setHalignment(title, HPos.CENTER);
        gp.add(title, 0, 0, 2, 1);

        HBox roundBox = new HBox();
        roundBox.setSpacing(6);
        roundBox.setAlignment(Pos.CENTER);
        Label round = new Label("Round");
        round.setStyle("-fx-font-size: 14px");
        roundNumber.setStyle("-fx-font-size: 15px; -fx-text-fill: lightblue; -fx-font-weight: bold");
        roundBox.getChildren().addAll(round, roundNumber);
        gp.add(roundBox, 0, 1, 2, 1);

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
        GridPane.setHalignment(healthv, HPos.CENTER);
        gp.add(healthv, 0, 2, 2, 1);

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
                game.stop();
            }
        });

        GridPane.setHalignment(pauseButton, HPos.RIGHT);
        gp.add(pauseButton, 0, 3);
        GridPane.setHalignment(stopButton, HPos.LEFT);
        gp.add(stopButton, 1, 3);
        ColumnConstraints cc = new ColumnConstraints();
        cc.setPercentWidth(50);
        gp.getColumnConstraints().addAll(cc, cc);

        return gp;
    }
}
