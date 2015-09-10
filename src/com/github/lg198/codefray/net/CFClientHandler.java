package com.github.lg198.codefray.net;

import com.github.lg198.codefray.net.protocol.packet.Packet;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;

public class CFClientHandler extends IoHandlerAdapter {

    @Override
    public void sessionOpened(IoSession session) {
        System.out.println("[CLIENT] Connected to server!");
    }

    @Override
    public void messageReceived(IoSession session, Object message) {
        Packet p = (Packet) message;
    }

    @Override
    public void sessionClosed(IoSession session) {
        System.out.println("[CLIENT] Disconnected!");
        session.getService().dispose();
    }
}
