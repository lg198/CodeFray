package com.github.lg198.codefray.net.protocol;

import com.github.lg198.codefray.net.protocol.packet.Packet;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.CumulativeProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;

import java.io.IOException;
import java.util.Arrays;

public class CFProtocolDecoder extends CumulativeProtocolDecoder {

    protected boolean doDecode(IoSession session, IoBuffer in, ProtocolDecoderOutput out) throws Exception {
        //System.out.println("[DECODER] BEING CALLED!");
        try {
            int loc = in.position();
            int id = in.getInt();
            int size = in.getInt();
            if (in.remaining() < size) {
                in.position(loc);
                return false;
            }

            //System.out.println("[DECODER] ENOUGH TO READ " + CFPacket.PACKETS.get(id).getSimpleName());

            CFPacket incomplete = new CFPacket(id, size);

            incomplete.content = new byte[incomplete.size];
            in.get(incomplete.content);

            //System.out.println("[DECODER] Received content: " + Arrays.toString(incomplete.content));

            if (!CFPacket.PACKETS.containsKey(incomplete.id)) {
                System.err.println("[DECODER] NO PACKET OF ID " + incomplete.id);
                throw new IOException("Non-existing packet read!");
            }

            Class<? extends Packet> c = CFPacket.PACKETS.get(incomplete.id);

            try {
                Packet p = c.newInstance();
                p.read(incomplete.content);
                out.write(p);
                //System.out.println("[DECODER] WRITING " + p.getClass().getSimpleName());
                session.removeAttribute("decoder.header");
                return true;
            } catch (Exception e) {
                e.printStackTrace(System.err);
                throw new Exception("Error while attempting to instantiate class.", e);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("");
        }
    }
}