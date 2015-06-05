package com.github.lg198.codefray.game.map.gen;

import com.github.lg198.codefray.api.math.Direction;
import com.github.lg198.codefray.api.math.Point;
import com.github.lg198.codefray.api.math.Vector;
import com.github.lg198.codefray.game.map.MapTile;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class PointSelectionFilter extends GenFilter {


    @Override
    public void filter(Random r, MapTile[][] map, int width, int height, Map props) {
        int num = (int) (width * height * 0.005);
        int spread = (int) Math.pow((width * height * 0.01), 2);
        System.out.println("Generating " + num + " points...");
        List<Point> specials = new ArrayList<>();
        outerLoop:
        for (int round = 0; round < num; round++) {
            int rx = r.nextInt(width), ry = r.nextInt(height);
            Point p1 = new Point(rx, ry);
            for (Point p2 : specials) {
                if (Vector.between(p1, p2).getMagnitudeSquared() < spread) {
                    round--;
                    continue outerLoop;
                }
            }
            specials.add(p1);
            System.out.println("adding " + p1);
        }
        props.put("tunnelPoints", specials);

        Point flag = (Point) props.get("flagLocation");
        Point win = (Point) props.get("winLocation");

        Point ftc, ftw, wtc, wtf;
        if (flag.getX() < win.getX()) {
            ftc = flag.in(Direction.SOUTH, 2);
            ftw = flag.in(Direction.EAST, 2);
            wtc = win.in(Direction.SOUTH, 2);
            wtf = win.in(Direction.WEST, 2);
        } else {
            ftc = flag.in(Direction.SOUTH, 2);
            ftw = flag.in(Direction.WEST, 2);
            wtc = win.in(Direction.SOUTH, 2);
            wtf = win.in(Direction.EAST, 2);
        }

        props.put("flagToCenterLocation", ftc);
        props.put("flagToWinLocation", ftw);
        props.put("winToCenterLocation", wtc);
        props.put("winToFlagLocation", wtf);
    }
}
