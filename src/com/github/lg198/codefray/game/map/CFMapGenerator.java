package com.github.lg198.codefray.game.map;

import com.github.lg198.codefray.api.game.Team;
import com.github.lg198.codefray.api.math.Direction;

import java.util.Map;

public class CFMapGenerator {


    public CFMap generate(int width, int height, Map<Direction, Team> m, double fillPct) {
        if (height%2!=0) {
            throw new IllegalArgumentException("Height must be even!");
        }
        MapTile[][] tiles = new MapTile[width][height];
        int btf = (int) (((width*height)/2) * fillPct);
    }
    
}
