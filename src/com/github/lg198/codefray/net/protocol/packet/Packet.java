package com.github.lg198.codefray.net.protocol.packet;

import java.io.IOException;

public abstract class Packet {

    public Packet() {}

    public abstract void read(byte[] content) throws IOException;
    public abstract byte[] write() throws IOException;
}
