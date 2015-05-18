package com.github.lg198.codefray.game.map;

import com.github.lg198.codefray.api.game.TileType;
import com.github.lg198.codefray.api.math.Point;

public class MapTile {

    private Point pos;
    private TileType type;

    public MapTile(TileType t) {
        type = t;
    }

    public Point getMapPosition() {
        return pos;
    }

    public void setMapPosition(Point p) {
        pos = p;
    }

    public TileType getTileType() {
        return type;
    }
}
