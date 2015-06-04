package com.github.lg198.codefray.game.map.gen;

import com.github.lg198.codefray.api.math.Direction;
import com.github.lg198.codefray.api.math.Point;
import com.github.lg198.codefray.game.map.MapTile;
import com.github.lg198.codefray.game.map.WallTile;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class CleanupFilter extends GenFilter {

    MapTile[][] map;
    int width, height;

    @Override
    public void filter(Random r, MapTile[][] map, int width, int height, Map props) {
        this.map = map;
        this.width = width;
        this.height = height;

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (!(map[x][y] instanceof WallTile)) {
                    continue;
                }
                Point p = new Point(x, y);
                boolean lonely = false;
                List<Point> remlist = new ArrayList<>();
                if (clean(p, 0, 4, remlist)) {
                    for (Point rp : remlist) {
                        map[p.getX()][p.getY()] = null;
                    }
                }
            }
        }
    }

    public void cleanup(int length) {

    }

    private boolean clean(Point p, int count, int length, List<Point> toErase) {
        toErase.add(p);
        count++;
        if (count > length) {
            return false;
        }
        for (Direction d : Direction.values()) {
            Point p1 = p.in(d);
            if (p1.inBounds(0, width, 0, height) && map[p1.getX()][p1.getY()] instanceof WallTile) {
                if (!clean(p1, count, length, toErase)) {
                    return false;
                }
            }
        }
        return true;
    }
}
