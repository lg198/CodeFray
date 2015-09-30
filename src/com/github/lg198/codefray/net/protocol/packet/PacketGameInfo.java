package com.github.lg198.codefray.net.protocol.packet;

import com.github.lg198.codefray.net.protocol.CFPacket;
import org.apache.mina.core.buffer.IoBuffer;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;

public class PacketGameInfo extends Packet {

    public String redName, blueName, redControllerName, blueControllerName;
    public boolean gameStarted;
    public int totalHealth;

    @Override
    public void read(byte[] content) throws IOException {
        IoBuffer buff = IoBuffer.wrap(content);
        redName = buff.getPrefixedString(CFPacket.DECODER);
        blueName = buff.getPrefixedString(CFPacket.DECODER);
        redControllerName = buff.getPrefixedString(CFPacket.DECODER);
        blueControllerName = buff.getPrefixedString(CFPacket.DECODER);
        totalHealth = buff.getInt();
        gameStarted = buff.get() == 1 ? true : false;
    }

    @Override
    public byte[] write() throws IOException {
        IoBuffer buff = IoBuffer.allocate(redName.length() + blueName.length() + redControllerName.length() + blueControllerName.length() + 1);
        buff.setAutoExpand(true);

        buff.putPrefixedString(redName, CFPacket.ENCODER);
        buff.putPrefixedString(blueName, CFPacket.ENCODER);
        buff.putPrefixedString(redControllerName, CFPacket.ENCODER);
        buff.putPrefixedString(blueControllerName, CFPacket.ENCODER);
        buff.putInt(totalHealth);
        buff.put(gameStarted ? (byte) 1 : (byte) 0);

        buff.flip();
        return buff.array();
    }
}
