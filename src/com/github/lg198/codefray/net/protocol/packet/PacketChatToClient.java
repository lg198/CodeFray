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

        name = buff.getString(CFPacket.DECODER);
        message = buff.getString(CFPacket.DECODER);
    }

    @Override
    public byte[] write() throws IOException {
        IoBuffer buff = IoBuffer.allocate(message.length() + name.length());
        buff.setAutoExpand(true);

        buff.putString(name, CFPacket.ENCODER);
        buff.putString(message, CFPacket.ENCODER);

        buff.flip();
        return buff.array();
    }
}
