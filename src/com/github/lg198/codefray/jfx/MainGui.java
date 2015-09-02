package com.github.lg198.codefray.jfx;

import com.github.lg198.codefray.game.CFGame;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.*;

public class MainGui {

    private CFGame game;
    public OptionsPanel panel;
    private GameBoard board;
    private boolean broadcast = false;
    public BroadcastPanel bpanel;

    public MainGui(CFGame g) {
        game = g;
        board = new GameBoard(game);
        panel = new OptionsPanel(game);
    }

    public MainGui(CFGame g, boolean bc) {
        this(g);
        if (bc) {
            broadcast = true;
            bpanel = new BroadcastPanel(game);
        }
    }

    public AnchorPane build() {
        if (!broadcast) {
            AnchorPane ap = new AnchorPane();
            VBox p = panel.build();
            Canvas c = board.build();
            c.heightProperty().bind(ap.heightProperty());
            c.widthProperty().bind(ap.widthProperty().add(p.widthProperty().negate()));


            ap.getChildren().add(c);
            AnchorPane.setRightAnchor(p, 0d);
            ap.getChildren().add(p);

            return ap;
        } else {
            AnchorPane ap = new AnchorPane();
            VBox p1 = panel.build();
            VBox p2 = bpanel.build();
            Canvas c = board.build();
            c.heightProperty().bind(ap.heightProperty());
            c.widthProperty().bind(ap.widthProperty().add(p1.widthProperty().negate()).add(p2.widthProperty().negate()));
            c.translateXProperty().bind(p2.widthProperty());

            ap.getChildren().add(c);
            AnchorPane.setRightAnchor(p1, 0d);
            ap.getChildren().add(p1);
            AnchorPane.setLeftAnchor(p2, 0d);
            ap.getChildren().add(p2);

            return ap;
        }
    }

    public void update() {
        panel.update();
        board.update();
    }

    public void updateBroadcast() {
        bpanel.update();
    }
}
