package com.github.lg198.codefray.game.map.gen;

import com.github.lg198.codefray.game.map.MapTile;

import java.util.Map;
import java.util.Random;

public abstract class GenFilter {

    public abstract void filter(Random r, MapTile[][] map, int width, int height, Map props);
}
