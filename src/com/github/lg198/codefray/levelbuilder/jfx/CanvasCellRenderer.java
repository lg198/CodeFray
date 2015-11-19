package com.github.lg198.codefray.levelbuilder.jfx;

import javafx.scene.canvas.GraphicsContext;

public interface CanvasCellRenderer {

    void render(int x, int y, int size, GraphicsContext gc);
}
