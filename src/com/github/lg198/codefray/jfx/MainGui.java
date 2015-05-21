package com.github.lg198.codefray.jfx;

import com.github.lg198.codefray.game.CFGame;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.*;

public class MainGui {

    private CFGame game;
    public OptionsPanel panel;
    private GameBoard board;

    public MainGui(CFGame g) {
        game = g;
        board = new GameBoard(game);
        panel = new OptionsPanel(game);
    }

    public AnchorPane build() {
        AnchorPane ap = new AnchorPane();
        VBox p = panel.build();
        Canvas c = board.build();
        c.heightProperty().bind(ap.heightProperty());
        c.widthProperty().bind(ap.widthProperty().add(p.widthProperty().negate()));

        ap.getChildren().add(c);
        AnchorPane.setRightAnchor(p, 0d);
        ap.getChildren().add(p);

        return ap;
    }

    public void update() {
        panel.update();
        board.update();
    }
}
