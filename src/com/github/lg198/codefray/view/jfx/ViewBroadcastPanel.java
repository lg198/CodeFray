package com.github.lg198.codefray.view.jfx;


import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextBuilder;
import javafx.scene.text.TextFlow;

public class ViewBroadcastPanel {

    private TextFlow logFlow = new TextFlow();
    private ScrollPane flowScroll = new ScrollPane(logFlow);
    private int limit = 100;


    public VBox build() {
        VBox panel = new VBox();
        panel.setPadding(new Insets(10));
        panel.setAlignment(Pos.TOP_CENTER);

        VBox flowBox = new VBox(new Label("Broadcast Log:"), flowScroll);
        panel.getChildren().add(flowBox);

        return panel;
    }

    public void addLine(Text... ts) {
        for (int i = 0; i < ts.length; i++) {
            if (logFlow.getChildren().size() >= limit) {
                logFlow.getChildren().remove(0);
            }

            if (i + 1 == ts.length) ts[i].setText(ts[i].getText() + "\n");

            logFlow.getChildren().add(ts[i]);
        }
        logFlow.layout();
        flowScroll.layout();
        flowScroll.setVvalue(1.0f);
    }
}
