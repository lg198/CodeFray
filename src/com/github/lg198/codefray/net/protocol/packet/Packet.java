package com.github.lg198.codefray.net.protocol.packet;

import com.github.lg198.codefray.net.protocol.PacketListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public abstract class Packet {

    public Packet() {}

    public abstract void read(byte[] content) throws IOException;
    public abstract byte[] write() throws IOException;

}
