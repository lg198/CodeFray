package com.github.lg198.codefray.net.protocol.packet;

import com.github.lg198.codefray.net.protocol.CFPacket;
import org.apache.mina.core.buffer.IoBuffer;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;

public class PacketGameInfo extends Packet {

    public String redName, blueName, redControllerName, blueControllerName;

    @Override
    public void read(byte[] content) throws IOException {
        IoBuffer buff = IoBuffer.wrap(content);
        redName = buff.getString(CFPacket.DECODER);
        blueName = buff.getString(CFPacket.DECODER);
        redControllerName = buff.getString(CFPacket.DECODER);
        blueControllerName = buff.getString(CFPacket.DECODER);
    }

    @Override
    public byte[] write() throws IOException {
        IoBuffer buff = IoBuffer.allocate(redName.length() + blueName.length() + redControllerName.length() + blueControllerName.length());
        buff.setAutoExpand(true);

        buff.putString(redName, CFPacket.ENCODER);
        buff.putString(blueName, CFPacket.ENCODER);
        buff.putString(redControllerName, CFPacket.ENCODER);
        buff.putString(blueControllerName, CFPacket.ENCODER);

        buff.flip();
        return buff.array();
    }
}
