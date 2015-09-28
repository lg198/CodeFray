package com.github.lg198.codefray.net.protocol.packet;

import com.github.lg198.codefray.net.protocol.CFPacket;
import org.apache.mina.core.buffer.IoBuffer;

import java.io.IOException;

public class PacketMapData extends Packet {

    public String mapName, mapAuthor;
    public int mapWidth, mapHeight;
    public int[][][] tiles;
    public int[][] golems;

    @Override
    public void read(byte[] content) throws IOException {
        IoBuffer buff = IoBuffer.wrap(content);

        mapName = buff.getPrefixedString(CFPacket.DECODER);
        mapAuthor = buff.getPrefixedString(CFPacket.DECODER);

        mapWidth = buff.getInt();
        mapHeight = buff.getInt();

        tiles = new int[mapWidth][mapHeight][2];
        for (int i = 0; i < mapHeight*mapWidth; i++) {
            int x = buff.getInt(), y = buff.getInt();
            int type = buff.getInt();
            tiles[x][y][0] = type;
            if (type == 3 || type == 2) {
                tiles[x][y][1] = buff.getInt();
            }
        }

        int gc = buff.getInt();
        golems = new int[gc][5];
        for (int i = 0; i < gc; i++) {
            int x = buff.getInt(), y = buff.getInt();
            int type = buff.getInt(), team = buff.getInt(), id = buff.getInt();
            golems[i][0] = x;
            golems[i][1] = y;
            golems[i][2] = type;
            golems[i][3] = team;
            golems[i][4] = id;
        }
    }

    @Override
    public byte[] write() throws IOException {
        IoBuffer buff = IoBuffer.allocate(5);
        buff.setAutoExpand(true);

        buff.putPrefixedString(mapName, CFPacket.ENCODER);
        buff.putPrefixedString(mapAuthor, CFPacket.ENCODER);

        buff.putInt(mapWidth);
        buff.putInt(mapHeight);

        for (int x = 0; x < tiles.length; x++) {
            for (int y = 0; y < tiles[x].length; y++) {
                buff.putInt(x);
                buff.putInt(y);
                buff.putInt(tiles[x][y][0]);
                if (tiles[x][y][0]==3 || tiles[x][y][0] == 2) {
                    buff.putInt(tiles[x][y][1]);
                }
            }
        }

        buff.putInt(golems.length);
        for (int i = 0; i < golems.length; i++) {
            int[] g = golems[i];
            buff.putInt(g[0]);
            buff.putInt(g[1]);
            buff.putInt(g[2]);
            buff.putInt(g[3]);
            buff.putInt(g[4]);
        }

        buff.flip();
        return buff.array();
    }
}
