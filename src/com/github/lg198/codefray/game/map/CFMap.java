package com.github.lg198.codefray.game.map;

import com.github.lg198.codefray.api.game.Team;
import com.github.lg198.codefray.api.game.TileType;
import com.github.lg198.codefray.api.math.Direction;
import com.github.lg198.codefray.api.math.Point;
import com.github.lg198.codefray.api.math.Vector;
import com.github.lg198.codefray.game.golem.CFGolem;
import com.github.lg198.codefray.net.CodeFrayServer;
import com.github.lg198.codefray.net.protocol.packet.PacketGameInfo;
import com.github.lg198.codefray.net.protocol.packet.PacketMapData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CFMap {

    private final MapTile[][] map;
    private final int width, height;
    private final String name, author;
    public int getHeight() {
        return height;
    }
    public int getWidth() {
        return width;
    }
    private final CFGolem[][] golemMap;


    public CFMap(MapTile[][] m, String n, String a) {
        map = m;
        width = map.length;
        height = map[0].length;

        golemMap = new CFGolem[map.length][map[0].length];


        name = n;
        author = a;
    }

    public CFGolem golemAt(Point p) {
        return golemMap[p.getX()][p.getY()];
    }

    public void addGolem(CFGolem g) {
        golemMap[g.getLocation().getX()][g.getLocation().getY()] = g;
    }

    public String getName() {
        return name;
    }

    public String getAuthor() {
        return author;
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
        if (getTile(p1) == null) {
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

    public void moveGolem(CFGolem g, Point oldPoint) {
        if (map[g.getLocation().getX()][g.getLocation().getY()] instanceof GolemHabitat) {
            if (!((GolemHabitat)map[g.getLocation().getX()][g.getLocation().getY()]).onGolemEnter(g)) {
                return;
            }
        }
        golemMap[oldPoint.getX()][oldPoint.getY()] = null;
        golemMap[g.getLocation().getX()][g.getLocation().getY()] = g;
    }

    public void removeGolem(CFGolem g) {
        golemMap[g.getLocation().getX()][g.getLocation().getY()] = null;
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

    public boolean isGolemMoveValid(CFGolem g, Direction d) {
        Point loc = g.getLocation();
        Point after = loc.in(d);
        if (after.getX() < 0 || after.getX() >= width ||
                after.getY() < 0 || after.getY() >= height) {
            return false;
        }
        if (getTile(after) == null) return true;
        if (!(getTile(after) instanceof GolemHabitat)) return false;
        if (!((GolemHabitat)getTile(after)).canGolemEnter(g)) {
            return false;
        }
        if (containsGolem(after)) return false;

        return true;
    }

    public boolean containsGolem(Point p) {
        return golemMap[p.getX()][p.getY()] != null;
    }

    public void writePacket(PacketMapData p) {
        List<int[]> golemEntries = new ArrayList<>();
        for (CFGolem[] arr : golemMap) {
            for (CFGolem g : arr) {
                if (g == null) {
                    continue;
                }
                golemEntries.add(new int[]{g.getType().ordinal(), g.getType().ordinal(), g.getId()});
            }
        }
        p.golems = golemEntries.toArray(new int[0][0]);
        p.mapAuthor = author;
        p.mapName = name;
        p.mapWidth = width;
        p.mapHeight = height;

        int[][][] tiles = new int[width][height][];
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                MapTile mt = map[x][y];
                if (mt == null) {
                    tiles[x][y] = new int[]{0};
                    continue;
                }
                int[] info = new int[]{mt.getTileType().ordinal()};
                if (mt.getTileType() == TileType.FLAG) {
                    FlagTile ft = (FlagTile) mt;
                    info = new int[]{info[0], ft.getTeam().ordinal()};
                } else if (mt.getTileType() == TileType.WIN) {
                    WinTile wt = (WinTile) mt;
                    info = new int[]{info[0], wt.getTeam().ordinal()};
                }
                tiles[x][y] = info;
            }
        }

        p.tiles = tiles;
    }

}
