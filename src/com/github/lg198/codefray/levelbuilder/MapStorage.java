package com.github.lg198.codefray.levelbuilder;

import java.io.*;
import java.util.ArrayList;

public class MapStorage {

    public static LoadedMap loadMap(InputStream is) throws IOException {
        DataInputStream dis = new DataInputStream(is);
        String mapName = dis.readUTF();
        String mapAuthor = dis.readUTF();
        int mapWidth = dis.readInt();
        int mapHeight = dis.readInt();

        LoadedMap map = new LoadedMap(mapWidth, mapHeight);
        map.name = mapName;
        map.author = mapAuthor;
        for (int indx = 0; indx < mapWidth*mapHeight; indx++) {
            LoadedCell cell = new LoadedCell();
            cell.x = dis.readInt();
            cell.y = dis.readInt();
            cell.type = dis.readInt();
            if (cell.type == 2 || cell.type == 3) {
                cell.team = dis.readInt();
            }
            if (cell.type != 0) {
                map.set(cell.x, cell.y, cell);
            }
        }

        int golemAmount = dis.readInt();
        for (int i = 0; i < golemAmount; i++) {
            LoadedGolem golem = new LoadedGolem();
            golem.x = dis.readInt();
            golem.y = dis.readInt();
            golem.type = dis.readInt();
            golem.team = dis.readInt();
            map.addGolem(golem);
        }

        dis.close();
        return map;
    }

    public static void writeMap(LoadedMap map, OutputStream os) throws IOException {
        DataOutputStream dos = new DataOutputStream(os);

        dos.writeUTF(map.name);
        dos.writeUTF(map.author);
        dos.writeInt(map.cells.length);
        dos.writeInt(map.cells[0].length);

        for (int x = 0; x < map.getWidth(); x++) {
            for (int y = 0; y < map.getHeight(); y++) {
                dos.writeInt(x);
                dos.writeInt(y);
                if (map.cells[x][y] == null) {
                    dos.writeInt(0);
                    continue;
                }
                LoadedCell cell = map.cells[x][y];
                dos.writeInt(cell.type);
                if (cell.type == 2 || cell.type == 3) {
                    dos.writeInt(cell.team);
                }
            }
        }

        dos.writeInt(map.golemCount());

        for (LoadedGolem golem : map.getGolems()) {
            dos.writeInt(golem.x);
            dos.writeInt(golem.y);
            dos.writeInt(golem.type);
            dos.writeInt(golem.team);
        }

        dos.close();
    }
}
