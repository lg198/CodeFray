package com.github.lg198.codefray.game.map.gen;


import com.github.lg198.codefray.api.math.Direction;
import com.github.lg198.codefray.api.math.Point;
import com.github.lg198.codefray.game.map.MapTile;

import java.util.*;

public class PathFilter extends GenFilter {

    int width, height;
    MapTile[][] map;

    @Override
    public void filter(Random r, MapTile[][] map, int width, int height, Map props) {
        this.width = width;
        this.height = height;
        this.map = map;
    }

    private void path(Point from, Point to) {
        Queue<Point> pathQueue = new LinkedList<>();
        pathQueue.add(from);
        Map<Point, Point> cameFrom = new HashMap<>();
        while (!pathQueue.isEmpty()) {
            Point p = pathQueue.poll();
            if (p.equals(to)) {
                break;
            }
            for (Direction d : Direction.values()) {
                Point p1 = p.in(d);
                if (!cameFrom.containsKey(p1)) {
                    pathQueue.add(p1);
                    cameFrom.put(p1, p);
                }
            }
        }

        Point current = to;
        List<Point> path = new ArrayList<>();
        path.add(current);
        while (!current.equals(from)) {
            current = cameFrom.get(current);
            path.add(0, current);
        }
        clearPath(path);
    }

    private void clearPath(List<Point> path) {
        for (Point p : path) {

        }
    }

    private void clearCircle(Point p, int r) {
        
    }
}
