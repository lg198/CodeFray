package com.github.lg198.codefray.load;

import com.github.lg198.codefray.api.game.Team;
import com.github.lg198.codefray.api.golem.GolemType;
import com.github.lg198.codefray.api.math.Point;
import com.github.lg198.codefray.game.map.*;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class MapLoader {


    public static CFMap loadMap(File f) {
        try {
            DataInputStream dis = new DataInputStream(new FileInputStream(f));

            String mapName = dis.readUTF();
            String mapAuthor = dis.readUTF();
            int mapWidth = dis.readInt();
            int mapHeight = dis.readInt();

            MapTile[][] tiles = new MapTile[mapWidth][mapHeight];
            for (int i = 0; i < mapHeight*mapWidth; i++) {
                int x = dis.readInt(), y = dis.readInt();
                int type = dis.readInt();
                if (type == 1) {
                    tiles[x][y] = new WallTile();
                    tiles[x][y].setMapPosition(new Point(x, y));
                } else if (type == 2) {
                    int team = dis.readInt();
                    FlagTile tile = new FlagTile(Team.values()[team]);
                    tile.setMapPosition(new Point(x, y));
                    tiles[x][y] = tile;
                } else if (type == 3) {
                    int team = dis.readInt();
                    WinTile tile = new WinTile(Team.values()[team]);
                    tile.setMapPosition(new Point(x, y));
                    tiles[x][y] = tile;
                }
            }

            int gcount = dis.readInt();

            for (int i = 0; i < gcount; i++) {
                int x = dis.readInt(), y = dis.readInt();
                int type = dis.readInt(), team = dis.readInt();
                GolemSpawnTile tile = new GolemSpawnTile(GolemType.values()[type], Team.values()[team]);
                tile.setMapPosition(new Point(x, y));
                tiles[x][y] = tile;
            }

            dis.close();

            CFMap map = new CFMap(tiles, mapName, mapAuthor);

            String error = MapValidator.validate(map);

            if (error == null) {
                return map;
            }

            throw new LoadException("Invalid map. " + error);
        } catch (IOException e) {
            throw new LoadException("Could not load map!", e);
        }
    }
}
