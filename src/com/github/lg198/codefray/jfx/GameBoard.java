package com.github.lg198.codefray.jfx;

import com.github.lg198.codefray.api.game.Team;
import com.github.lg198.codefray.api.math.Point;
import com.github.lg198.codefray.game.CFGame;
import com.github.lg198.codefray.game.golem.CFGolem;
import com.github.lg198.codefray.game.map.FlagTile;
import com.github.lg198.codefray.game.map.MapTile;
import com.github.lg198.codefray.game.map.WallTile;
import com.github.lg198.codefray.game.map.WinTile;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.paint.Color;

public class GameBoard {

    private Canvas render = new Canvas();
    private CFGame game;

    private double gridSize = 50, minGridSize = 10, maxGridSize = 60;
    private double transx = 0, transy = 0;

    public GameBoard(CFGame g) {
        game = g;

        testsprite = new Image(
                ResourceManager.getIcon("testsprite.png"),
                32, 32, true, true);
    }

    private Image testsprite;

    public void setGame(CFGame g) {
        game = g;

        redraw();
    }

    public Canvas build() {
        ChangeListener<Number> cl = new ChangeListener<Number>() {

            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                redraw();
            }
        };

        render.heightProperty().addListener(cl);
        render.widthProperty().addListener(cl);

        EventHandler mh = new EventHandler<MouseEvent>() {

            private double dx = 0, dy = 0, otx = transx, oty = transy;
            private boolean dragged = false;

            @Override
            public void handle(MouseEvent e) {
                if (e.getEventType() == MouseEvent.MOUSE_RELEASED) {
                    render.setCursor(Cursor.DEFAULT);
                    dragged = false;
                    return;
                }
                if (e.getEventType() == MouseEvent.MOUSE_PRESSED) {
                    dragged = false;
                    dx = e.getX(); dy = e.getY();
                    otx = transx; oty = transy;
                    return;
                }
                if (e.getEventType() == MouseEvent.MOUSE_CLICKED) {
                    System.out.println("dragged: " + (!dragged) + " paused: " + game.isPaused());
                    if (!dragged && game.isPaused()) {
                        Point p = clickToCell(e.getX(), e.getY());
                        System.out.println(p);
                        redraw();
                        CFGolem g = game.golemAt(p);
                        if (g != null) {
                            game.getGui().panel.golemSelected(g);
                            highlightCell(p);
                        } else {
                            game.getGui().panel.removeGolemBox();
                        }

                        return;
                    }
                    render.setCursor(Cursor.DEFAULT);
                    return;
                }
                if (!dragged) {
                    render.setCursor(Cursor.CLOSED_HAND);
                    dragged = true;
                }
                transx = otx + (e.getX() - dx);
                transy = oty + (e.getY() - dy);

                redraw();
            }
        };

        render.setOnMouseDragged(mh);
        render.setOnMousePressed(mh);
        render.setOnMouseClicked(mh);
        render.setOnMouseReleased(mh);

        render.setOnScroll(new EventHandler<ScrollEvent>() {

            @Override
            public void handle(ScrollEvent scrollEvent) {
                if (scrollEvent.getDeltaY() < 0) {
                    gridSize -= 5;
                    if (gridSize < minGridSize) {
                        gridSize = minGridSize;
                    }
                } else {
                    gridSize += 5;
                    if (gridSize > maxGridSize) {
                        gridSize = maxGridSize;
                    }
                }

                redraw();
            }
        });

        return render;
    }

    private Point clickToCell(double x, double y) {
        y -= transy; x -= transx;
        int ycount = 0, xcount = 0;
        while (y >= gridSize/10) {
            y -= (gridSize/10d) + gridSize;
            ycount++;
        }
        while ( x >= gridSize/10) {
            x  -= (gridSize/10d) + gridSize;
            xcount++;
        }
        return new Point(xcount - 1, ycount - 1);
    }

    private GraphicsContext gc() {
        return render.getGraphicsContext2D();
    }

    private void translateToCell(Point p, boolean padding) {
        gc().save();
        double pad = gridSize/10;
        double ipad = padding ? pad : 0;
        gc().translate(ipad + p.getX() * (gridSize + pad), ipad + p.getY() * (gridSize + pad));
    }

    public void redraw() {
        gc().clearRect(0, 0, render.getWidth(), render.getHeight());

        gc().save();

        gc().translate(transx, transy);

        double pad = gridSize / 10;
        double width = game.getMap().getWidth()*(pad+gridSize) + pad;
        double height = game.getMap().getHeight()*(pad+gridSize) + pad;

        gc().setFill(Color.WHITESMOKE);
        gc().fillRect(0, 0, width, height);

        for (int x = 0; x < game.getMap().getWidth(); x++) {
            for (int y = 0; y < game.getMap().getHeight(); y++) {
                Point p = new Point(x, y);
                translateToCell(p, true);
                renderCell(new Point(x, y));
                gc().restore();
            }
        }

        gc().setFill(Color.BLACK);
        for (int x = 0; x <= game.getMap().getWidth(); x++) {
            gc().fillRect(x * (pad+gridSize), 0, pad, height);
        }
        for (int y = 0; y <= game.getMap().getHeight(); y++) {
            gc().fillRect(0, y * (pad+gridSize), width, pad);
        }
        gc().restore();

        if (highlighted != null) {
            highlightCell(highlighted);
        }
    }

    private void renderCell(Point p) {
        MapTile mt = game.getMap().getTile(p);
        if (mt == null) {
            gc().setFill(Color.WHITESMOKE);
            gc().fillRect(0, 0, gridSize, gridSize);
        }
        if (game.golemAt(p) != null) {
            gc().drawImage(testsprite, 0, 0, gridSize, gridSize);
        } else if (mt instanceof WallTile) {
            gc().setFill(Color.DARKGRAY);
            gc().fillRect(0, 0, gridSize, gridSize);
        } else if (mt instanceof FlagTile) {
            FlagTile ft = (FlagTile) mt;
            gc().setFill(ft.getTeam() == Team.RED ? Color.RED : Color.BLUE);
            gc().fillRect(0, 0, gridSize, gridSize);
            gc().setStroke(Color.DARKGRAY);
            gc().setLineWidth(gridSize/15);
            gc().strokeLine(0, 0, gridSize, gridSize);
            gc().strokeLine(0, gridSize, gridSize, 0);
        } else if (mt instanceof WinTile) {
            WinTile wt = (WinTile) mt;
            gc().setFill(wt.getTeam() == Team.RED ? Color.RED : Color.BLUE);
            gc().fillRect(0, 0, gridSize, gridSize);
        }
    }

    private Point highlighted;
    private void highlightCell(Point p) {
        highlighted = p;
        translateToCell(p, false);
        gc().translate(transx, transy);

        double pad = gridSize/10;

        gc().setFill(Color.GOLD);
        gc().fillRect(0, 0, pad*2 + gridSize, pad); //Top
        gc().fillRect(pad + gridSize, 0, pad, pad*2 + gridSize); //Right
        gc().fillRect(0, pad + gridSize, pad*2 + gridSize, pad); //Bottom
        gc().fillRect(0, 0, pad, pad*2 + gridSize); //Left

        gc().restore();
    }

    public void update() {
        redraw();
        highlighted = null;
    }

}
