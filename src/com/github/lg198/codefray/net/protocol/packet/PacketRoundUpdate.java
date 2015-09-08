package com.github.lg198.codefray.net.protocol.packet;

import org.apache.mina.core.buffer.IoBuffer;

import java.io.IOException;

public class PacketRoundUpdate extends Packet {

    public long round;

    @Override
    public void read(byte[] content) throws IOException {
        IoBuffer buff = IoBuffer.wrap(content);

        round = buff.getLong();
    }

    @Override
    public byte[] write() throws IOException {
        IoBuffer buff = IoBuffer.allocate(Long.BYTES);
        buff.setAutoExpand(true);

        buff.putLong(round);

        buff.flip();
        return buff.array();
    }
}
