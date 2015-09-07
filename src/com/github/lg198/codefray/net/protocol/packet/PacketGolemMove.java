package com.github.lg198.codefray.net.protocol.packet;

import org.apache.mina.core.buffer.IoBuffer;

import java.io.IOException;

public class PacketGolemMove extends Packet {

    public int id, x, y;

    @Override
    public void read(byte[] content) throws IOException {
        IoBuffer buff = IoBuffer.wrap(content);

        id = buff.getInt();
        x = buff.getInt();
        y = buff.getInt();
    }

    @Override
    public byte[] write() throws IOException {
        IoBuffer buff = IoBuffer.allocate(Integer.BYTES*3);
        buff.setAutoExpand(true);

        buff.putInt(id);
        buff.putInt(x);
        buff.putInt(y);

        buff.flip();
        return buff.array();
    }
}
