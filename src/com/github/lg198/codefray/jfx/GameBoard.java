package com.github.lg198.codefray.jfx;

import com.github.lg198.codefray.api.game.Team;
import com.github.lg198.codefray.api.math.Point;
import com.github.lg198.codefray.game.CFGame;
import com.github.lg198.codefray.game.GameBoardProvider;
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
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.paint.Color;

public class GameBoard {

    private Canvas render = new Canvas();
    private GameBoardProvider game;

    private double gridSize = 50, minGridSize = 10, maxGridSize = 60;
    private double transx = 0, transy = 0;

    private Image redFlag, blueFlag, redWin, blueWin,
            redRunner, blueRunner, redAssault, blueAssault, redDefense, blueDefense;

    public GameBoard(GameBoardProvider g) {
        game = g;

        redRunner = new Image(
                ResourceManager.getIcon("redrunner.png"),
                32, 32, true, true);

        blueRunner = new Image(
                ResourceManager.getIcon("bluerunner.png"),
                32, 32, true, true);

        redAssault = new Image(
                ResourceManager.getIcon("redassault.png"),
                32, 32, true, true);

        blueAssault = new Image(
                ResourceManager.getIcon("blueassault.png"),
                32, 32, true, true);

        redDefense = new Image(
                ResourceManager.getIcon("reddefense.png"),
                32, 32, true, true);

        blueDefense = new Image(
                ResourceManager.getIcon("bluedefense.png"),
                32, 32, true, true);

        redFlag = new Image(
                ResourceManager.getIcon("flag.png"),
                32, 32, true, true);

        blueFlag = new WritableImage((int) redFlag.getWidth(), (int) redFlag.getHeight());
        for (int x = 0; x < blueFlag.getWidth(); x++) {
            for (int y = 0; y < blueFlag.getHeight(); y++) {
                if (redFlag.getPixelReader().getColor(x, y).getRed() == 1.0) {
                    ((WritableImage) blueFlag).getPixelWriter().setColor(x, y, Color.rgb(0, 0, 255));
                } else {
                    ((WritableImage) blueFlag).getPixelWriter().setColor(x, y, redFlag.getPixelReader().getColor(x, y));
                }
            }
        }

        redWin = new Image(
                ResourceManager.getIcon("win.png"),
                32, 32, true, true);

        blueWin = new WritableImage((int) redWin.getWidth(), (int) redWin.getHeight());
        for (int x = 0; x < blueWin.getWidth(); x++) {
            for (int y = 0; y < blueWin.getHeight(); y++) {
                if (redWin.getPixelReader().getColor(x, y).getRed() == 1.0) {
                    ((WritableImage) blueWin).getPixelWriter().setColor(x, y, Color.BLUE);
                } else if (redWin.getPixelReader().getColor(x, y).getBlue() == 1.0) {
                    ((WritableImage) blueWin).getPixelWriter().setColor(x, y, Color.RED);
                } else {
                    ((WritableImage) blueWin).getPixelWriter().setColor(x, y, redWin.getPixelReader().getColor(x, y));
                }
            }
        }
    }


    public void setGame(GameBoardProvider g) {
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
                    dx = e.getX();
                    dy = e.getY();
                    otx = transx;
                    oty = transy;
                    return;
                }
                if (e.getEventType() == MouseEvent.MOUSE_CLICKED) {
                    if (!dragged && game.isPaused()) {
                        Point p = clickToCell(e.getX(), e.getY());
                        redraw();
                        int gid = game.golemIdAt(p);
                        if (gid > -1) {
                            game.selectGolem(gid);
                            if (highlighted != null) {
                                update();
                            }
                            highlightCell(p);
                        } else {
                            game.deselectGolem();
                            update();
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
        y -= transy;
        x -= transx;
        int ycount = 0, xcount = 0;
        while (y >= gridSize / 10) {
            y -= (gridSize / 10d) + gridSize;
            ycount++;
        }
        while (x >= gridSize / 10) {
            x -= (gridSize / 10d) + gridSize;
            xcount++;
        }
        return new Point(xcount - 1, ycount - 1);
    }

    private GraphicsContext gc() {
        return render.getGraphicsContext2D();
    }

    private void translateToCell(Point p, boolean padding) {
        gc().save();
        double pad = gridSize / 10;
        double ipad = padding ? pad : 0;
        gc().translate(ipad + p.getX() * (gridSize + pad), ipad + p.getY() * (gridSize + pad));
    }

    public void redraw() {
        gc().clearRect(0, 0, render.getWidth(), render.getHeight());

        gc().save();

        gc().translate(transx, transy);

        double pad = gridSize / 10;
        double width = game.getMapWidth() * (pad + gridSize) + pad;
        double height = game.getMapHeight() * (pad + gridSize) + pad;

        gc().setFill(Color.WHITESMOKE);
        gc().fillRect(0, 0, width, height);

        for (int x = 0; x < game.getMapWidth(); x++) {
            for (int y = 0; y < game.getMapHeight(); y++) {
                Point p = new Point(x, y);
                translateToCell(p, true);
                renderCell(new Point(x, y));
                gc().restore();
            }
        }

        gc().setFill(Color.BLACK);
        for (int x = 0; x <= game.getMapWidth(); x++) {
            gc().fillRect(x * (pad + gridSize), 0, pad, height);
        }
        for (int y = 0; y <= game.getMapHeight(); y++) {
            gc().fillRect(0, y * (pad + gridSize), width, pad);
        }
        gc().restore();

        if (highlighted != null) {
            highlightCell(highlighted);
        }
    }

    private void renderCell(Point p) {
        int mt = game.getMapTileAt(p);
        if (mt == 0) {
            gc().setFill(Color.WHITESMOKE);
            gc().fillRect(0, 0, gridSize, gridSize);
        }
        if (game.golemIdAt(p) > -1) {
            int gid = game.golemIdAt(p);
            Image gi = null;
            if (game.golemTeam(gid) == Team.RED) {
                switch (game.golemType(gid)) {
                    case 0:
                        gi = redRunner;
                        break;
                    case 1:
                        gi = redDefense;
                        break;
                    case 2:
                        gi = redAssault;
                        break;
                }
            } else {
                switch (game.golemType(gid)) {
                    case 0:
                        gi = blueRunner;
                        break;
                    case 1:
                        gi = blueDefense;
                        break;
                    case 2:
                        gi = blueAssault;
                        break;
                }
            }
            gc().drawImage(gi, 0, 0, gridSize, gridSize);
        } else if (mt == 1) {
            gc().setFill(Color.DARKGRAY);
            gc().fillRect(0, 0, gridSize, gridSize);
        } else if (mt == 2) {
            if (game.getMapTileTeam(p) == Team.RED) {
                gc().drawImage(redFlag, 0, 0, gridSize, gridSize);
            } else {
                gc().drawImage(blueFlag, 0, 0, gridSize, gridSize);

            }
        } else if (mt == 3) {
            gc().drawImage(game.getMapTileTeam(p) == Team.RED ? redWin : blueWin, 0, 0, gridSize, gridSize);
        }
    }

    private Point highlighted;

    private void highlightCell(Point p) {
        highlighted = p;
        translateToCell(p, false);
        gc().translate(transx, transy);

        double pad = gridSize / 10;

        gc().setFill(Color.GOLD);
        gc().fillRect(0, 0, pad * 2 + gridSize, pad); //Top
        gc().fillRect(pad + gridSize, 0, pad, pad * 2 + gridSize); //Right
        gc().fillRect(0, pad + gridSize, pad * 2 + gridSize, pad); //Bottom
        gc().fillRect(0, 0, pad, pad * 2 + gridSize); //Left

        gc().restore();
    }

    public void update() {
        highlighted = null;
        redraw();
    }

}
