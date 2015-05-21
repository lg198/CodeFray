package com.github.lg198.codefray.game.map.gen;

import com.github.lg198.codefray.api.game.Team;
import com.github.lg198.codefray.api.math.Direction;
import com.github.lg198.codefray.api.math.Point;
import com.github.lg198.codefray.api.math.Vector;
import com.github.lg198.codefray.game.map.*;

import java.util.Map;
import java.util.Random;

public class CFMapGenerator {


    public static CFMap generate(int width, int height) {
        long seed = System.nanoTime();
        Random mrand = new Random(seed);
        System.out.println("Seed: " + seed);
        if (height%2!=0) {
            throw new IllegalArgumentException("Height must be even!");
        }
        MapTile[][] tiles = new MapTile[width][height];
        int rheight = (int) (height/2) + 1;

        Generator gen = new Generator(mrand, new PreparationFilter(), new FillerFilter());

        gen.generate(tiles, width, rheight);
        copyTopToBottomFlipped(tiles, height/2);

        return new CFMap(tiles);
    }


    private static void copyTopToBottomFlipped(MapTile[][] tiles, int height) {
        for (int x = 0; x < tiles.length; x++) {
            for (int y = 0; y < height; y++) {
                MapTile mt = tiles[x][y];
                if (mt != null) {
                    if (mt instanceof FlagTile) {
                        FlagTile ft = (FlagTile) mt;
                        mt = new FlagTile(ft.getTeam() == Team.RED ? Team.BLUE : Team.RED);
                    } else if (mt instanceof WinTile) {
                        WinTile wt = (WinTile) mt;
                        mt = new WinTile(wt.getTeam() == Team.RED ? Team.BLUE : Team.RED);
                    }
                }
                tiles[tiles.length - x - 1][height + height - y - 1] = mt;
            }

        }
    }

    private static boolean blocksWithin(MapTile[][] tiles, int x, int y, int r2) {
                Point p1 = new Point(x, y);
                for (int xx = 0; xx < tiles.length; xx++) {
                    for (int yy = 0; yy < tiles[xx].length; yy++) {
                        if (!(tiles[xx][yy] instanceof WallTile)) {
                            continue;
                        }
                int d2 = Vector.between(p1, new Point(xx, yy)).getMagnitudeSquared();
                if (d2 <= r2) {
                    return true;
                }
            }
        }
        return false;
    }

    
}
