package com.github.lg198.codefray.view.jfx;


import com.github.lg198.codefray.net.CodeFrayClient;
import com.github.lg198.codefray.net.protocol.packet.PacketChatToServer;
import com.github.lg198.codefray.util.Stylizer;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextBuilder;
import javafx.scene.text.TextFlow;

public class ViewBroadcastPanel {

    private TextFlow logFlow = new TextFlow();
    private ScrollPane flowScroll = new ScrollPane(logFlow);
    private TextField chatField = new TextField();
    private int limit = 100;

    private ViewGui gui;

    public ViewBroadcastPanel(ViewGui gui) {
        this.gui = gui;
    }

    public VBox build() {
        VBox panel = new VBox();
        panel.setPadding(new Insets(10));
        panel.setAlignment(Pos.TOP_CENTER);

        Stylizer.set(logFlow, "-fx-font-family", "Consolas, inherited");

        VBox flowBox = new VBox(new Label("Broadcast Log:"), flowScroll);
        VBox.setVgrow(flowBox, Priority.ALWAYS);
        panel.getChildren().add(flowBox);

        VBox.setMargin(chatField, new Insets(20, 0, 0, 0));
        chatField.setPromptText("Send a chat message");
        chatField.setFocusTraversable(false);
        chatField.setOnAction(event -> {
            if (chatField.getText().trim().isEmpty()) {
                return;
            }
            gui.game.sendChat(chatField.getText().trim());
            chatField.clear();
        });
        panel.getChildren().add(chatField);

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
