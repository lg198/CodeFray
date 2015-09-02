package com.github.lg198.codefray.net.protocol;

public class CFPacket {

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
