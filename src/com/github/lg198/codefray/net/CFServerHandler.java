package com.github.lg198.codefray.net;

import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;

public class CFServerHandler extends IoHandlerAdapter {

    @Override
    public void sessionOpened(IoSession session) {
        System.out.println("[SERVER] Client connected!");
    }

    @Override
    public void messageReceived(IoSession session, Object message) {

    }

    @Override
    public void sessionClosed(IoSession session) {
        System.out.println("[SERVER] Client disconnected!");
        session.getService().dispose();
    }
}