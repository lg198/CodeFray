package com.github.lg198.codefray.game.map.gen;

import com.github.lg198.codefray.game.map.MapTile;

import java.util.*;

public class Generator {

    private Random random;
    private List<GenFilter> filters = new ArrayList<GenFilter>();

    public Generator(Random r) {
        random = r;
    }

    public Generator(Random r, GenFilter... f) {
        this(r);
        filters = (List<GenFilter>) Arrays.asList(f);
    }

    public Random getRandom() {
        return random;
    }

    public void addFilter(GenFilter f) {
        filters.add(f);
    }

    public void removeFilter(GenFilter f) {
        filters.remove(f);
    }

    public void generate(MapTile[][] tiles, int width, int height) {
        Map m = new HashMap();
        for (GenFilter f : filters) {
            f.filter(random, tiles, width, height, m);
        }
    }
}
