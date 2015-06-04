package com.github.lg198.codefray.game.map.gen;


import com.github.lg198.codefray.api.math.Direction;
import com.github.lg198.codefray.api.math.Point;
import com.github.lg198.codefray.game.map.MapTile;

import java.util.*;

public class PathFilter extends GenFilter {
    @Override
    public void filter(Random r, MapTile[][] map, int width, int height, Map props) {

    }

    public void path(Point from, Point to) {
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
                    //TODO: FINISH ALGORITHM. THIS PART IS DONE, BUT PATH MUST BE MADE
                    /*current = goal
                    path = [current]
                    while current != start:
                    current = came_from[current]
                    path.append(current)
                    path.reverse()*/
                }
            }
        }
    }
}
