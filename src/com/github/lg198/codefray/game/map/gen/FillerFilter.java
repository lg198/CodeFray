package com.github.lg198.codefray.game.map.gen;

import com.github.lg198.codefray.api.math.Direction;
import com.github.lg198.codefray.game.map.MapTile;
import com.github.lg198.codefray.game.map.WallTile;

import java.util.Map;
import java.util.Random;

public class FillerFilter extends GenFilter {
    @Override
    public void filter(Random r, MapTile[][] map, int width, int height, Map props) {
        for (int x = 0; x < map.length; x++) {
            for (int y = 0; y < map[x].length; y++) {
                if (map[x][y] == null) {
                    map[x][y] = new WallTile();
                }
            }
        }

        for (int x = 0; x < map.length; x++) {
            for (int y = 0; y < map[x].length; y++) {
                if (map[x][y] instanceof WallTile) {
                    clearAround(map, x, y);
                }
            }
        }
    }

    private void clearAround(MapTile[][] map, int x, int y) {
        for (Direction d : Direction.values()) {
            if (d.getXChange() + x >= 0 && d.getXChange() + y < map.length) {
                if (d.getYChange() + y >= 0 && d.getYChange() + y < map[0].length){
                    map[x+d.getXChange()][y+d.getYChange()] = null;
                }
            }
        }
    }
}
