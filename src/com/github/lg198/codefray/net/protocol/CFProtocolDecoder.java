package com.github.lg198.codefray.net.protocol;

import com.github.lg198.codefray.net.protocol.packet.Packet;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.CumulativeProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;

import java.io.IOException;

public class CFProtocolDecoder extends CumulativeProtocolDecoder {

    protected boolean doDecode(IoSession session, IoBuffer in, ProtocolDecoderOutput out) throws Exception {
        if (!session.containsAttribute("decoder.header")) {
            if (in.remaining() < Integer.BYTES*2) {
                return false;
            }
            int id = in.getInt(), size = in.getInt();
            session.setAttribute("decoder.header", new CFPacket(id, size));
            return false;
        }

        CFPacket incomplete = (CFPacket) session.getAttribute("decoder.header");
        if (in.remaining() < incomplete.size) {
            return false;
        }

        incomplete.content = new byte[incomplete.size];
        in.get(incomplete.content);

        if (!CFPacket.PACKETS.containsKey(incomplete.id)) {
            throw new IOException("Non-existing packet read!");
        }

        Class<? extends Packet> c = CFPacket.PACKETS.get(incomplete.id);

        try {
            Packet p = c.newInstance();
            p.read(incomplete.content);
            out.write(p);
        } catch (Exception e) {
            throw new Exception("Error while attempting to instantiate class.", e);
        }


        session.removeAttribute("decoder.header");
        return true;
    }
}