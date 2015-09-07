package com.github.lg198.codefray.net.protocol;

import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;

public class CFPacket {

    public static final Charset CHARSET;
    public static final CharsetDecoder DECODER;
    public static final CharsetEncoder ENCODER;

    static {
        CHARSET = Charset.forName("UTF-8");
        DECODER = CHARSET.newDecoder();
        ENCODER = CHARSET.newEncoder();
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
