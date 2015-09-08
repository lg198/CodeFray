package com.github.lg198.codefray.net.protocol.packet;

import java.io.IOException;

public class PacketChatToClient extends Packet {
    @Override
    public void read(byte[] content) throws IOException {
        
    }

    @Override
    public byte[] write() throws IOException {
        return new byte[0];
    }
}
