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

    public static final Map<Integer, Class<? extends Packet>> PACKETS = new HashMap<Integer, Class<? extends Packet>>();
    public static final Map<Class<? extends Packet>, Integer> REV_PACKETS = new HashMap<>();
    static {
        registerPacket(PacketHelloServer.class);
        registerPacket(PacketGameInfo.class);
        registerPacket(PacketMapData.class);
        registerPacket(PacketRoundUpdate.class);
        registerPacket(PacketGolemUpdate.class);
        registerPacket(PacketGolemMove.class);
        registerPacket(PacketGolemDie.class);
        registerPacket(PacketChatToServer.class);
        registerPacket(PacketChatToClient.class);
        registerPacket(PacketGameEnd.class);
        registerPacket(PacketGamePause.class);
    }
    private static int PID_COUNTER = 0;
    private static void registerPacket(Class<? extends Packet> p) {
        PACKETS.put(PID_COUNTER++, p);
        REV_PACKETS.put(p, PID_COUNTER-1);
    }


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
