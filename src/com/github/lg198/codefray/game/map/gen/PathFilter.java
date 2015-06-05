package com.github.lg198.codefray.game.map.gen;


import com.github.lg198.codefray.api.math.Direction;
import com.github.lg198.codefray.api.math.Point;
import com.github.lg198.codefray.game.map.MapTile;
import com.github.lg198.codefray.game.map.WallTile;

import java.util.*;

public class PathFilter extends GenFilter {

    public static int PATH_RADIUS = 3;

    int width, height;
    MapTile[][] map;

    @Override
    public void filter(Random r, MapTile[][] map, int width, int height, Map props) {
        this.width = width;
        this.height = height;
        this.map = map;

        List<Point> tunnelPoints = (List<Point>) props.get("tunnelPoints");
        Collections.shuffle(tunnelPoints, r);

        for (int i = 0; i < tunnelPoints.size(); i++) {
            if (i + 1 < tunnelPoints.size()) {
                Point p1 = tunnelPoints.get(i);
                Point p2 = tunnelPoints.get(i + 1);
                path(p1, p2);
            }
        }
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
            clearCircle(p, PATH_RADIUS);
        }
    }

    private void clearCircle(Point p, int r) {
        for (int y = p.getY() - r; y < p.getY(); y++) { //top half
            int onEachSide = (r + 1) - (p.getY() - y);
            for (int x = p.getX() - onEachSide; x <= p.getX() + onEachSide; x++) {
                Point p1 = new Point(x, y);
                if (p1.inBounds(0, width, 0, height) && map[x][y] instanceof WallTile) {
                    map[x][y] = null;
                }
            }
        }
        for (int x = p.getX() - r; x <= p.getX() + r; x++) { //center
            Point p1 = new Point(x, p.getY());
            if (p1.inBounds(0, width, 0, height) && map[x][p.getY()] instanceof WallTile) {
                map[x][p.getY()] = null;
            }
        }
        for (int y = p.getY() + r; y > p.getY(); y--) { //bottom half
            int onEachSide = (r + 1) - (y - p.getY());
            for (int x = p.getX() - onEachSide; x <= p.getX() + onEachSide; x++) {
                Point p1 = new Point(x, y);
                if (p1.inBounds(0, width, 0, height) && map[x][y] instanceof WallTile) {
                    map[x][y] = null;
                }
            }
        }
    }
}
