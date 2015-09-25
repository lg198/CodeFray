package com.github.lg198.codefray.net.protocol.packet;

import com.github.lg198.codefray.net.protocol.CFPacket;
import org.apache.mina.core.buffer.IoBuffer;

import java.io.IOException;

public class PacketHelloServer extends Packet {

    public String name;

    @Override
    public void read(byte[] content) throws IOException {
        name = new String(content, CFPacket.CHARSET);
    }

    @Override
    public byte[] write() throws IOException {
        return name.getBytes(CFPacket.CHARSET);
    }
}
