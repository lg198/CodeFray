package com.github.lg198.codefray.net;

import com.github.lg198.codefray.net.protocol.CFPacket;
import com.github.lg198.codefray.net.protocol.CFProtocolFactory;
import com.github.lg198.codefray.net.protocol.packet.Packet;
import com.github.lg198.codefray.view.ViewProfile;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

import java.io.IOException;
import java.net.InetSocketAddress;

public class CodeFrayClient {

    private static NioSocketConnector connector;

    public static void start(String address, ViewProfile profile) throws IOException {
        connector = new NioSocketConnector();

        connector.getFilterChain().addLast("codec", new ProtocolCodecFilter(new CFProtocolFactory()));

        connector.setHandler(new CFClientHandler(profile));

        connector.connect(new InetSocketAddress(address, CodeFrayServer.PORT));
    }

    public static void sendPacket(Packet p) {
        if (!CFPacket.REV_PACKETS.containsKey(p.getClass())) {
            throw new IllegalArgumentException("Class " + p.getClass().getSimpleName() + " is not a registered packet!");
        }

        int id = CFPacket.REV_PACKETS.get(p.getClass());
        CFPacket packet = new CFPacket(id);
        try {
            packet.content = p.write();
            packet.size = packet.content.length;
            connector.broadcast(packet);
        } catch (IOException e) {
            throw new RuntimeException("Failed to write or send packet " + id);
        }
    }
}
