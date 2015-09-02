package com.github.lg198.codefray.net.protocol;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoderAdapter;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;

public class CFProtocolEncoder extends ProtocolEncoderAdapter {

    @Override
    public void encode(IoSession session, Object message, ProtocolEncoderOutput output) throws Exception {
        CFPacket packet = (CFPacket) message;

        IoBuffer buffer = IoBuffer.allocate(Integer.BYTES*2 + packet.size, false);
        buffer.putInt(packet.id);
        buffer.putInt(packet.size);
        buffer.put(packet.content);
        buffer.flip();

        output.write(buffer);
    }
}
