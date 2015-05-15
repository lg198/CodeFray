package com.github.lg198.codefray.game.map;

import com.github.lg198.codefray.api.game.Team;
import com.github.lg198.codefray.api.math.Point;
import com.github.lg198.codefray.api.math.Vector;

import java.util.ArrayList;
import java.util.List;

public class CFMap {

    private final MapTile[][] map;
    private final int width, height;

    public CFMap(MapTile[][] m) {
        map = m;
        width = map.length;
        height = map[0].length;
    }

    public void setTile(Point p, MapTile mt) {
        map[p.getX()][p.getY()] = mt;
        if (mt!=null) {
            mt.setMapPosition(p);
        }
    }

    public MapTile getTile(Point p) {
        return map[p.getX()][p.getY()];
    }

    public void move(Point p1, Point p2) {
        if (getTile(p2) != null || getTile(p1) == null) {
            return;
        }
        MapTile temp = getTile(p1);
        setTile(p2, temp);
        setTile(p1, null);
    }

    public <T extends MapTile> List<T> getTilesOfType(Class<T> type) {
        List<T> tiles = new ArrayList<T>();
        for (MapTile[] mta : map) {
            for (MapTile mt : mta) {
                if (mt == null) {
                    continue;
                }
                if (mt.getClass().getName().equals(type.getName())) {
                    tiles.add((T) mt);
                }
            }
        }
        return tiles;
    }

    public <T extends MapTile> List<T> getTilesOfType(Class<T> type, Point p, int radius2) {
        List<T> tiles = new ArrayList<T>();
        for (MapTile[] mta : map) {
            for (MapTile mt : mta) {
                if (mt == null) {
                    continue;
                }
                if (Vector.between(mt.getMapPosition(), p).getMagnitudeSquared() > radius2) {
                    continue;
                }
                if (mt.getClass().getName().equals(type.getName())) {
                    tiles.add((T) mt);
                }
            }
        }
        return tiles;
    }

    public Point getFlag(Team t) {
        List<FlagTile> tiles = getTilesOfType(FlagTile.class);
        for (FlagTile ft : tiles) {
            if (ft.getTeam() == t) {
                return ft.getMapPosition();
            }
        }
        return null;
    }
    
}