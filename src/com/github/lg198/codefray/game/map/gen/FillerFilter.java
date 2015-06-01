package com.github.lg198.codefray.game.map.gen;

import com.github.lg198.codefray.api.math.Direction;
import com.github.lg198.codefray.game.map.FlagTile;
import com.github.lg198.codefray.game.map.MapTile;
import com.github.lg198.codefray.game.map.WallTile;
import com.github.lg198.codefray.game.map.WinTile;

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
                if (map[x][y] instanceof FlagTile || map[x][y] instanceof WinTile) {
                    clearAround(map, x, y);
                }
            }
        }
    }

    private void clearAround(MapTile[][] map, int x, int y) {
        for (Direction d : Direction.values()) {
            //first layer
            if (d.getXChange() + x >= 0 && d.getXChange() + x < map.length) {
                if (d.getYChange() + y >= 0 && d.getYChange() + y < map[0].length){
                    map[x+d.getXChange()][y+d.getYChange()] = null;
                }
            }
            //second layer
            if (d.getXChange()*2 + x >= 0 && d.getXChange()*2 + x < map.length) {
                if (d.getYChange()*2 + y >= 0 && d.getYChange()*2 + y < map[0].length){
                    int cx = x+d.getXChange()*2, cy = y+d.getYChange()*2;
                    map[cx][cy] = null;
                    if (cx < x+2 && cy == y-2) { //top => right
                        map[cx+1][cy] = null;
                    } else if (cy < y +2 && cx == x+2) { // right => down
                        map[cx][cy+1] = null;
                    } else if (cx > x+2 && cy == y+2) { //bottom => left
                        map[cx-1][cy] = null;
                    } else if (cy > y+2 && cx == x-2) { //left => up
                        map[cx][cy - 1] = null;
                    }
                }
            }
        }
    }
}
