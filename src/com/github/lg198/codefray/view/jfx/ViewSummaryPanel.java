package com.github.lg198.codefray.view.jfx;

import com.github.lg198.codefray.util.Stylizer;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

public class ViewSummaryPanel {

    public Label round = new Label("0");
    public Label status = new Label("Not Started");


    public VBox build() {
        VBox panel = new VBox();
        panel.setPadding(new Insets(15));
        panel.setSpacing(10);
        panel.setAlignment(Pos.TOP_CENTER);

        VBox info = new VBox();
        info.setSpacing(8);
        info.setAlignment(Pos.CENTER_LEFT);
        Stylizer.set(info, "-fx-border-color", "black");
        Stylizer.set(info, "-fx-border-width", "2px");

        Label roundTitle = new Label("Round:");
        Stylizer.set(roundTitle, "-fx-font-size", "16px");
        Stylizer.set(round, "-fx-font-size", "16px");
        Stylizer.set(round, "-fx-font-weight", "bold");
        HBox roundBox = new HBox(roundTitle, round);
        roundBox.setSpacing(5);
        info.getChildren().add(roundBox);

        Stylizer.set(status, "-fx-font-size", "16px");
        status.setTextFill(Color.RED);
        info.getChildren().add(status);

        panel.getChildren().add(info);

        return panel;
    }

    public void statusStart() {
        status.setTextFill(Color.GREEN);
        status.setText("Running");
    }

    public void statusPause() {
        status.setTextFill(Color.ORANGERED);
        status.setText("Paused");
    }
}
