package com.github.lg198.codefray.net.protocol;

import com.github.lg198.codefray.net.protocol.packet.Packet;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoderAdapter;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;

import java.util.Arrays;

public class CFProtocolEncoder extends ProtocolEncoderAdapter {

    @Override
    public void encode(IoSession session, Object message, ProtocolEncoderOutput output) throws Exception {
        Packet up = (Packet) message;

        if (!CFPacket.REV_PACKETS.containsKey(up.getClass())) {
            throw new IllegalArgumentException("Class " + up.getClass().getSimpleName() + " is not a registered packet!");
        }

        int id = CFPacket.REV_PACKETS.get(up.getClass());
        CFPacket packet = new CFPacket(id);
        packet.content = up.write();
        System.out.println(Arrays.toString(packet.content));
        packet.size = packet.content.length;

        //System.out.printf("Encoding packet with id of %d and size of %d.\n", packet.id, packet.size);

        IoBuffer buffer = IoBuffer.allocate(Integer.BYTES * 2 + packet.size);
        buffer.putInt(packet.id);
        buffer.putInt(packet.size);
        buffer.put(packet.content);
        buffer.flip();

        output.write(buffer);
    }
}
