package com.github.lg198.codefray.net.protocol;


import com.github.lg198.codefray.net.protocol.packet.Packet;

public interface PacketListener<T extends Packet> {

    void packetReceived(T packet);
}
