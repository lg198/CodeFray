package com.github.lg198.codefray.levelbuilder.jfx;

import com.github.lg198.codefray.levelbuilder.LoadedCell;
import com.github.lg198.codefray.levelbuilder.LoadedGolem;
import com.github.lg198.codefray.levelbuilder.LoadedMap;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Toggle;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

public class MapGui {

    public Canvas canvas = new Canvas(640, 480);
    public final CanvasGrid grid;
    public final LoadedMap map;

    public final EditPane pane;

    public double transx, transy, scale = 1, zx, zy;

    public int lastX, lastY;
    public boolean shiftDown = false;
    public boolean firstClick = true;

    public boolean mirrored = false;

    public Image
            redDefense = buildImage("reddefense"),
            redRunner = buildImage("redrunner"),
            redAssault = buildImage("redassault"),
            blueRunner = buildImage("bluerunner"),
            blueDefense = buildImage("bluedefense"),
            blueAssault = buildImage("blueassault");
    public Image redWin = buildImage("win");
    public Image redFlag = buildImage("flag");
    public Image blueWin, blueFlag;

    public Image buildImage(String name) {
        return new Image(
                MapGui.class.getResource("/com/github/lg198/codefray/levelbuilder/res/" + name + ".png").toExternalForm(),
                32, 32, true, true);
    }


    public MapGui(int gridSize, int borderSize, LoadedMap map) {
        grid = new CanvasGrid(map.cells.length, map.cells[0].length, gridSize, borderSize, Color.BLACK, this::renderCell);
        this.map = map;
        pane = new EditPane(this);

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

    public Canvas build() {
        ChangeListener<Number> cl = new ChangeListener<Number>() {

            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                render();
            }
        };

        canvas.heightProperty().addListener(cl);
        canvas.widthProperty().addListener(cl);

        EventHandler<MouseEvent> eh = new EventHandler<MouseEvent>() {
            double ox, oy;
            boolean dragged = false;

            @Override
            public void handle(MouseEvent event) {
                if (event.getEventType() == MouseEvent.MOUSE_PRESSED) {
                    ox = event.getX();
                    oy = event.getY();
                } else if (event.getEventType() == MouseEvent.MOUSE_DRAGGED && event.isControlDown()) {
                    dragged = true;
                    transx += event.getX() - ox;
                    transy += event.getY() - oy;
                    ox = event.getX();
                    oy = event.getY();
                } else {
                    if (!dragged) {
                        double x = (event.getX() - transx) / scale, y = (event.getY() - transy) / scale;
                        int ycount = -1, xcount = -1;
                        while (y >= grid.bwidth) {
                            y -= grid.bwidth + grid.gsize;
                            ycount++;
                        }
                        while (x >= grid.bwidth) {
                            x -= grid.bwidth + grid.gsize;
                            xcount++;
                        }
                        if (xcount == -1) {
                            xcount = 0;
                        }
                        if (ycount == -1) {
                            ycount = 0;
                        }

                        onClick(xcount, ycount, event.isShiftDown(), event.getButton() == MouseButton.SECONDARY);
                    }
                    dragged = false;
                }
                render();
            }
        };

        canvas.setOnMousePressed(eh);
        canvas.setOnMouseDragged(eh);
        canvas.setOnMouseClicked(eh);

        canvas.setOnScroll(event -> {
            if (!event.isControlDown()) {
                return;
            }
            zx = event.getX();
            zy = event.getY();
            if (event.getDeltaY() > 0) {
                scale *= 1.15;
            } else {
                scale /= 1.15;
            }
            render();
        });

        return canvas;
    }

    public void render() {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        gc.save();
        //gc.translate(-canvas.getWidth() / 2, -canvas.getHeight() / 2);
        gc.scale(scale, scale);
        gc.translate(transx / scale, (transy) / scale);
        //gc.translate(canvas.getWidth() / 2, canvas.getHeight() / 2);
        grid.render(gc);
        gc.restore();
    }

    private void renderCell(int x, int y, int size, GraphicsContext gc) {
        if (map.golemMap[x][y] != null) {
            LoadedGolem g = map.golemMap[x][y];
            Image gi = null;
            if (g.team == 0) {
                switch (g.type) {
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
                switch (g.type) {
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
            gc.drawImage(gi, 0, 0, size, size);
            return;
        }

        LoadedCell lc = map.cells[x][y];
        if (lc == null) {
            int halfway = (int) ((double) map.getHeight() / 2.0);
            Color bg;
            if (y < halfway) {
                bg = Color.RED.deriveColor(0, 1, 1, 0.1);
            } else {
                bg = Color.BLUE.deriveColor(0, 1, 1, 0.1);
            }
            gc.setFill(bg);
            gc.fillRect(0, 0, size, size);
            return;
        }

        if (lc.type == 1) {
            gc.setFill(Color.GRAY);
            gc.fillRect(0, 0, size, size);
            return;
        }

        if (lc.type == 2) {
            gc.drawImage(lc.team == 0 ? redFlag : blueFlag, 0, 0, size, size);
            return;
        }

        if (lc.type == 3) {
            gc.drawImage(lc.team == 0 ? redWin : blueWin, 0, 0, size, size);
            return;
        }
    }

    private void onClick(int x, int y, boolean shift, boolean left) {
        if (x < 0 || x >= map.getWidth()
                || y < 0 || y >= map.getHeight()) {
            return;
        }

        Toggle t = pane.group.getSelectedToggle();
        if (map.golemMap[x][y] != null) {
            map.golemMap[x][y] = null;
        }
        if (t == pane.empty) {
            map.set(x, y, null);
        } else if (t == pane.wall) {
            if (left) {
                map.set(x, y, null);
                return;
            }
            if (shift) {
                if (!shiftDown) {
                    shiftDown = true;
                    firstClick = true;
                }
                if (firstClick) {
                    map.set(x, y, new LoadedCell(x, y, 1, -1));
                    lastX = x;
                    lastY = y;
                    firstClick = false;
                } else {
                    if (lastX == x) {
                        for (int i = Math.min(lastY, y); i <= Math.max(lastY, y); i++) {
                            map.set(x, i, new LoadedCell(x, i, 1, -1));
                        }
                    } else if (lastY == y) {
                        for (int i = Math.min(lastX, x); i <= Math.max(lastX, x); i++) {
                            map.set(i, y, new LoadedCell(i, y, 1, -1));
                        }
                    }
                }
            } else {
                if (mirrored) map.setMirrored(x, y, new LoadedCell(x, y, 1, -1));
                else map.set(x, y, new LoadedCell(x, y, 1, -1));
                shiftDown = false;
                firstClick = true;
            }

            lastX = x;
            lastY = y;
        } else if (t == pane.redFlag) {
            map.set(x, y, new LoadedCell(x, y, 2, 0));
        } else if (t == pane.blueFlag) {
            map.set(x, y, new LoadedCell(x, y, 2, 1));
        } else if (t == pane.redWin) {
            map.set(x, y, new LoadedCell(x, y, 3, 0));
        } else if (t == pane.blueWin) {
            map.set(x, y, new LoadedCell(x, y, 3, 1));
        } else if (t == pane.redRunner) {
            map.set(x, y, null);
            map.addGolem(new LoadedGolem(x, y, 0, 0));
        } else if (t == pane.blueRunner) {
            map.set(x, y, null);
            map.addGolem(new LoadedGolem(x, y, 0, 1));
        } else if (t == pane.redDefender) {
            map.set(x, y, null);
            map.addGolem(new LoadedGolem(x, y, 1, 0));
        } else if (t == pane.blueDefender) {
            map.set(x, y, null);
            map.addGolem(new LoadedGolem(x, y, 1, 1));
        } else if (t == pane.redAssault) {
            map.set(x, y, null);
            map.addGolem(new LoadedGolem(x, y, 2, 0));
        } else if (t == pane.blueAssault) {
            map.set(x, y, null);
            map.addGolem(new LoadedGolem(x, y, 2, 1));
        }

        render();
    }
}
