package com.github.lg198.codefray.net.protocol.packet;

import org.apache.mina.core.buffer.IoBuffer;

import java.io.IOException;

public class PacketGameEnd extends Packet {

    public int type, reason, winner, guilty;

    public long rounds, timeInSeconds;
    public int redLeft, blueLeft;
    public double redHealthPercent, blueHealthPercent;

    @Override
    public void read(byte[] content) throws IOException {
        IoBuffer buff = IoBuffer.wrap(content);

        type = buff.getInt();
        if (type == 0) {
            reason = buff.getInt();
            winner = buff.getInt();
        } else if (type == 1) {
            reason = buff.getInt();
            guilty = buff.getInt();
        }
        rounds = buff.getLong();
        timeInSeconds = buff.getLong();
        redLeft = buff.getInt();
        blueLeft = buff.getInt();
        redHealthPercent = buff.getDouble();
        blueHealthPercent = buff.getDouble();
    }

    @Override
    public byte[] write() throws IOException {
        IoBuffer buff = IoBuffer.allocate(Integer.BYTES);

        buff.putInt(type);
        if (type == 0) {
            buff.putInt(reason);
            buff.putInt(winner);
        } else if (type == 1) {
            buff.putInt(reason);
            buff.putInt(winner);
        }
        buff.putLong(rounds);
        buff.putLong(timeInSeconds);
        buff.putInt(redLeft);
        buff.putInt(blueLeft);
        buff.putDouble(redHealthPercent);
        buff.putDouble(blueHealthPercent);

        buff.flip();
        return buff.array();
    }
}
