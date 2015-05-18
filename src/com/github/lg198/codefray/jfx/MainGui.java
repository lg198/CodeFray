package com.github.lg198.codefray.jfx;

import com.github.lg198.codefray.game.CFGame;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class MainGui {

    private CFGame game;
    private OptionsPanel panel;
    private GameBoard board;

    public MainGui(CFGame g) {
        game = g;
        board = new GameBoard(game);
        panel = new OptionsPanel(game);
    }

    public HBox build() {
        HBox sp = new HBox();
        VBox p = panel.build();
        //p.prefWidthProperty().bind(sp.widthProperty().divide(3));
        Canvas c = board.build();
        VBox cb = new VBox();
        cb.getChildren().add(c);
        c.heightProperty().bind(cb.heightProperty());
        c.widthProperty().bind(cb.widthProperty());

        cb.prefWidthProperty().bind(sp.widthProperty().multiply(2d/3));
        sp.getChildren().addAll(cb, p);
        return sp;
    }

    public void update() {
        panel.update();
        board.update();
    }
}
