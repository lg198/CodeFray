package com.github.lg198.codefray.net.protocol.packet;

import java.io.IOException;

public class PacketGamePause extends Packet {

    public boolean paused;

    @Override
    public void read(byte[] content) throws IOException {
        paused = content[0] == 1 ? true : false;
    }

    @Override
    public byte[] write() throws IOException {
        return new byte[]{paused ? (byte) 1 : (byte) 0};
    }
}
