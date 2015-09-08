package com.github.lg198.codefray.net.protocol.packet;

import java.io.IOException;

public class PacketHelloServer extends Packet {

    public String name;

    @Override
    public void read(byte[] content) throws IOException {

    }

    @Override
    public byte[] write() throws IOException {
        return new byte[0];
    }
}
