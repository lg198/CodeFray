package com.github.lg198.codefray.net.protocol.packet;

import com.github.lg198.codefray.net.protocol.CFPacket;
import org.apache.mina.core.buffer.IoBuffer;

import java.io.IOException;

public class PacketChatToClient extends Packet {

    public String message;
    public String name;


    @Override
    public void read(byte[] content) throws IOException {
        IoBuffer buff = IoBuffer.wrap(content);

        name = buff.getPrefixedString(CFPacket.DECODER);
        message = buff.getPrefixedString(CFPacket.DECODER);
    }

    @Override
    public byte[] write() throws IOException {
        IoBuffer buff = IoBuffer.allocate(message.length() + name.length());
        buff.setAutoExpand(true);

        buff.putPrefixedString(name, CFPacket.ENCODER);
        buff.putPrefixedString(message, CFPacket.ENCODER);

        buff.flip();
        return buff.array();
    }
}
