package com.github.lg198.codefray.jfx;

import com.github.lg198.codefray.game.CFGame;
import com.github.lg198.codefray.util.AccumulatorLogger;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class BroadcastPanel {

    private VBox panel = new VBox();
    private TextArea logArea = new TextArea();
    private Label viewersLabel = new Label("0");
    public AccumulatorLogger logger = new AccumulatorLogger();

    private final CFGame game;

    public BroadcastPanel(CFGame g) {
        game = g;
    }

    public VBox build() {
        panel.setAlignment(Pos.CENTER);
        panel.setSpacing(20);
        panel.setPadding(new Insets(10));

        panel.getChildren().add(buildInfoBox());
        panel.getChildren().add(buildLogBox());


        return panel;
    }

    private VBox buildInfoBox() {
        VBox info = new VBox();
        info.setSpacing(6);
        info.setStyle("-fx-border-color: black; -fx-border-width: 4px");
        info.setAlignment(Pos.CENTER);

        Label title = new Label("Broadcast:");
        title.setStyle("-fx-font-size: 20px");

        HBox viewersBox = new HBox();
        viewersBox.setAlignment(Pos.CENTER);
        viewersBox.setSpacing(3);
        viewersBox.getChildren().add(new Label("Viewers:"));
        viewersLabel.setStyle("-fx-font-weight: bold;");
        viewersBox.getChildren().add(viewersLabel);
        info.getChildren().add(viewersBox);

        return info;
    }

    private VBox buildLogBox() {
        VBox log = new VBox();
        log.setSpacing(3);
        log.setAlignment(Pos.CENTER);

        log.getChildren().add(new Label("Broadcast Log:"));
        logArea.setMaxWidth(200);
        logger.setListener((String s) -> {
            logArea.setText(s);
            logArea.setScrollTop(Double.MAX_VALUE);
        });
        log.getChildren().add(logArea);

        return log;
    }

    public void update() {

    }

}
