package com.github.lg198.codefray.net.protocol.packet;

import org.apache.mina.core.buffer.IoBuffer;

import java.io.IOException;

public class PacketGolemDie extends Packet {

    public int id;

    @Override
    public void read(byte[] content) throws IOException {
        IoBuffer buff = IoBuffer.wrap(content);

        id = buff.getInt();
    }

    @Override
    public byte[] write() throws IOException {
        IoBuffer buff = IoBuffer.allocate(Integer.BYTES);

        buff.putInt(id);

        buff.flip();
        return buff.array();
    }
}
