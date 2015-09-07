package com.github.lg198.codefray.net.protocol.packet;

import org.apache.mina.core.buffer.IoBuffer;

import java.io.IOException;

public class PacketTileUpdate extends Packet {

    public int x, y, type, team;

    @Override
    public void read(byte[] content) throws IOException {
        IoBuffer buff = IoBuffer.wrap(content);

        x = buff.getInt();
        y = buff.getInt();
        type = buff.getInt();
        if (type == 2 || type == 3) {
            team = buff.getInt();
        }
    }

    @Override
    public byte[] write() throws IOException {
        IoBuffer buff = IoBuffer.allocate(Integer.BYTES*3);
        buff.setAutoExpand(true);

        buff.putInt(x);
        buff.putInt(y);
        buff.putInt(type);
        if (type == 2 || type == 3) {
            buff.putInt(team);
        }

        buff.flip();
        return buff.array();
    }
}
