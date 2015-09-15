package com.github.lg198.codefray.net.protocol.packet;

import org.apache.mina.core.buffer.IoBuffer;

import java.io.IOException;

public class PacketMapUpdate extends Packet {

    public int x, y;
    public int type;
    public int[] data;


    @Override
    public void read(byte[] content) throws IOException {
        IoBuffer buffer = IoBuffer.wrap(content);

        x = buffer.getInt();
        y = buffer.getInt();
        type = buffer.getInt();
        int length = buffer.getInt();
        data = new int[length];
        for (int i = 0; i < length; i++) {
            data[i] = buffer.getInt();
        }
    }

    @Override
    public byte[] write() throws IOException {
        IoBuffer buff = IoBuffer.allocate(Integer.BYTES*(4 + data.length));
        buff.setAutoExpand(true);

        buff.putInt(x);
        buff.putInt(y);
        buff.putInt(type);
        buff.putInt(data.length);
        for (int i = 0; i < data.length; i++) {
            buff.putInt(data[i]);
        }

        buff.flip();
        return buff.array();
    }
}
