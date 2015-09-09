package com.github.lg198.codefray.net.protocol;

import com.github.lg198.codefray.net.protocol.packet.*;

import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.util.HashMap;
import java.util.Map;

public class CFPacket {

    public static final Charset CHARSET;
    public static final CharsetDecoder DECODER;
    public static final CharsetEncoder ENCODER;

    static {
        CHARSET = Charset.forName("UTF-8");
        DECODER = CHARSET.newDecoder();
        ENCODER = CHARSET.newEncoder();
    }

    public static final Map<Integer, Class<? extends Packet>> PACKETS = new HashMap<Integer, Class<? extends Packet>>() {
        {
            put(0, PacketHelloServer.class);
            put(1, PacketGameInfo.class);
            put(2, PacketMapData.class);
            put(3, PacketRoundUpdate.class);
            put(4, PacketTileUpdate.class);
            put(5, PacketGolemUpdate.class);
            put(6, PacketGolemMove.class);
            put(7, PacketGolemDie.class);
            put(8, PacketChatToServer.class);
            put(9, PacketChatToClient.class);

            put(10, PacketGameEnd.class);

        }
    };


    public int id, size;
    public byte[] content;

    public CFPacket(int i) {
        id = i;
    }

    public CFPacket(int i, int s) {
        id = i;
        size = s;
    }

    public CFPacket(int i, byte[] b) {
        id = i;
        content = b;
        size = content.length;
    }
}
