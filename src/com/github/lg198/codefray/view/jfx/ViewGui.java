package com.github.lg198.codefray.view.jfx;


import com.github.lg198.codefray.jfx.GameBoard;
import com.github.lg198.codefray.view.ViewGame;
import javafx.scene.canvas.Canvas;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

public class ViewGui {

    public ViewBroadcastPanel broadcast = new ViewBroadcastPanel();
    public ViewSummaryPanel summary = new ViewSummaryPanel();
    public GameBoard board;
    public ViewGame game;

    public ViewGui(ViewGame game) {
        this.game = game;
        board = new GameBoard(game);
    }

    public AnchorPane build() {
        AnchorPane ap = new AnchorPane();

        VBox bbox = broadcast.build();
        VBox sbox = summary.build();

        AnchorPane.setLeftAnchor(bbox, 0.0);
        AnchorPane.setRightAnchor(sbox, 0.0);

        Canvas c = board.build();
        c.widthProperty().bind(ap.widthProperty().add(bbox.widthProperty().negate()).add(sbox.widthProperty().negate()));
        c.heightProperty().bind(ap.heightProperty());
        c.translateXProperty().bind(bbox.widthProperty());

        ap.getChildren().addAll(bbox, c, sbox);

        return ap;
    }
}
