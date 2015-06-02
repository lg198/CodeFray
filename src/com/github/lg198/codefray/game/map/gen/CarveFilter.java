package com.github.lg198.codefray.game.map.gen;

import com.github.lg198.codefray.api.math.Direction;
import com.github.lg198.codefray.api.math.Point;
import com.github.lg198.codefray.game.map.MapTile;

import java.util.Map;
import java.util.Random;

public class CarveFilter extends GenFilter {

    public int width, height;
    public Random random;


    @Override
    public void filter(Random r, MapTile[][] map, int width, int height, Map props) {
        this.width = width;
        this.height = height;
        this.random = r;

    }

    public void lanuchMiner(int sx, int sy) {

    }

    public void mine(Point p) {
        for (Direction d : Direction.values()) {
            if (p.in(d).inBounds(0, width, 0, height)) {

            }
        }
    }

}
