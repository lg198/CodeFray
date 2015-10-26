package com.github.lg198.codefray.jfx;

import com.github.lg198.codefray.game.CFGame;
import com.github.lg198.codefray.util.Stylizer;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;

public class BroadcastPanel {

    private VBox panel = new VBox();
    private Label viewersLabel = new Label("0");
    private TextFlow logFlow = new TextFlow();
    private ScrollPane flowScroll = new ScrollPane(logFlow);
    private int limit = 100;

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
        logFlow.setMaxWidth(350);
        logFlow.setTextAlignment(TextAlignment.LEFT);
        flowScroll.setMaxWidth(350);
        flowScroll.setMaxHeight(Double.MAX_VALUE);
        log.getChildren().add(flowScroll);
        VBox.setVgrow(flowScroll, Priority.ALWAYS);

        return log;
    }

    public void update() {
    }

    public void setViewers(int v) {
        viewersLabel.setText(v + "");
    }

    public void addLine(Text... ts) {
        for (int i = 0; i < ts.length; i++) {
            if (logFlow.getChildren().size() >= limit) {
                logFlow.getChildren().remove(0);
            }

            if (i + 1 == ts.length) ts[i].setText(ts[i].getText() + "\n");

            Stylizer.set(ts[i], "-fx-font-family", "Consolas, inherited");

            logFlow.getChildren().add(ts[i]);
        }
        logFlow.layout();
        flowScroll.layout();
        flowScroll.setVvalue(1.0f);
    }

    public void addChatMessage(String uname, String message) {
        addLine(Stylizer.text(
                uname + ": ",
                "-fx-fill", "black",
                "-fx-font-weight", "bold"
        ), Stylizer.text(
                message,
                "-fx-fill", "black"
        ));
    }

}
