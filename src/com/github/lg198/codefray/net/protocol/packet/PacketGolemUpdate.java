package com.github.lg198.codefray.net.protocol.packet;

import org.apache.mina.core.buffer.IoBuffer;

import java.io.IOException;

public class PacketGolemUpdate extends Packet {

    public int id, health;


    @Override
    public void read(byte[] content) throws IOException {
        IoBuffer buff = IoBuffer.wrap(content);

        id = buff.getInt();
        health = buff.getInt();
    }

    @Override
    public byte[] write() throws IOException {
        IoBuffer buff = IoBuffer.allocate(Integer.BYTES*2);
        buff.setAutoExpand(true);

        buff.putInt(id);
        buff.putInt(health);

        buff.flip();
        return buff.array();
    }
}
