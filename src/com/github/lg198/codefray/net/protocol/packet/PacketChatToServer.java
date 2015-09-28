package com.github.lg198.codefray.net.protocol.packet;

import com.github.lg198.codefray.net.protocol.CFPacket;
import org.apache.mina.core.buffer.IoBuffer;

import java.io.IOException;

public class PacketChatToServer extends Packet {

    public String message;

    @Override
    public void read(byte[] content) throws IOException {
        IoBuffer buff = IoBuffer.wrap(content);

        message = buff.getPrefixedString(CFPacket.DECODER);
    }

    @Override
    public byte[] write() throws IOException {
        IoBuffer buff = IoBuffer.allocate(message.length());
        buff.setAutoExpand(true);

        buff.putPrefixedString(message, CFPacket.ENCODER);

        buff.flip();
        return buff.array();
    }
}
