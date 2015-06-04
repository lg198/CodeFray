package com.github.lg198.codefray.game.map.gen;

import com.github.lg198.codefray.api.math.Direction;
import com.github.lg198.codefray.api.math.Point;
import com.github.lg198.codefray.game.map.MapTile;
import com.github.lg198.codefray.game.map.WallTile;

import java.util.*;

public class CarveFilter extends GenFilter {

    public int width, height;
    public Random random;
    public MapTile[][] map;


    @Override
    public void filter(Random r, MapTile[][] map, int width, int height, Map props) {
        this.width = width;
        this.height = height;
        this.random = r;
        this.map = map;

        for (int i = 0; i < (int) ((width * height) * 0.05); i++) {
            int rx = random.nextInt(width);
            int ry = random.nextInt(height);
            if (!(map[rx][ry] instanceof WallTile)) {
                i--;
                continue;
            }
            launchMiner(new Point(rx, ry));
        }
    }

    public void launchMiner(Point p) {
        map[p.getX()][p.getY()] = null;
        mine(p);
    }

    public void mine(Point p) {
        /*int cont = random.nextInt(20);
        if (cont == 0) {
            return;
        }*/
        List<Direction> dirs = Arrays.asList(Direction.values());
        Collections.shuffle(dirs, random);
        Direction chosen = null;
        for (Direction d : dirs) {
            if (p.in(d).inBounds(0, width, 0, height) && map[p.in(d).getX()][p.in(d).getY()] instanceof WallTile) {
                map[p.getX()][p.getY()] = null;
                chosen = d;
                break;
            }
        }
        if (chosen != null) {
            mine(p.in(chosen));
        }
    }

}
