package com.github.lg198.codefray.levelbuilder.jfx;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

public class CanvasGrid {

    public int width, height, gsize, bwidth;
    public Paint borderPaint;
    public CanvasCellRenderer renderer;

    public CanvasGrid(int w, int h, int gs, int bw, Paint bp, CanvasCellRenderer r) {
        width = w;
        height = h;
        gsize = gs;
        bwidth = bw;
        borderPaint = bp;
        renderer = r;
    }

    private int realWidth() {
        return gsize*width + bwidth*(width + 1);
    }

    private int realHeight() {
        return gsize*height + bwidth*(height + 1);
    }

    public void render(GraphicsContext gc) {
        gc.setFill(borderPaint);
        gc.setStroke(Color.TRANSPARENT);

        for (int x = 0; x <= width; x++) {
            int rx = x*(bwidth + gsize);
            gc.fillRect(rx, 0, bwidth, realHeight());
        }

        for (int y = 0; y <= height; y++) {
            int ry = y*(bwidth + gsize);
            gc.fillRect(0, ry, realWidth(), bwidth);
        }


        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int[] coords = gridOffset(x, y);
                gc.translate(coords[0], coords[1]);
                renderer.render(x, y, gsize, gc);
                gc.translate(-coords[0], -coords[1]);
            }
        }
    }

    private int[] gridOffset(int x, int y) {
        int[] i = new int[2];
        i[0] = bwidth + x*(gsize + bwidth);
        i[1] = bwidth + y*(gsize + bwidth);
        return i;
    }
}
