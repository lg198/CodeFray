package com.github.lg198.codefray.net;

import com.github.lg198.codefray.net.protocol.packet.Packet;
import com.github.lg198.codefray.net.protocol.packet.PacketGameInfo;
import com.github.lg198.codefray.net.protocol.packet.PacketHelloServer;
import com.github.lg198.codefray.view.ViewProfile;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;

public class CFClientHandler extends IoHandlerAdapter {

    private ViewProfile profile;

    public CFClientHandler(ViewProfile p) {
        profile = p;
    }

    @Override
    public void sessionOpened(IoSession session) {
        System.out.println("[CLIENT] Connected to server!");
        PacketHelloServer packet = new PacketHelloServer();
        packet.name = profile.username;
        CodeFrayClient.sendPacket(packet);
    }

    @Override
    public void messageReceived(IoSession session, Object message) {
        Packet p = (Packet) message;

        if (p instanceof PacketGameInfo) {

        }
    }

    @Override
    public void sessionClosed(IoSession session) {
        System.out.println("[CLIENT] Disconnected!");
        session.getService().dispose();
    }
}
