package com.github.lg198.codefray.net;

import com.github.lg198.codefray.api.game.Team;
import com.github.lg198.codefray.game.CFGame;
import com.github.lg198.codefray.net.protocol.packet.Packet;
import com.github.lg198.codefray.net.protocol.packet.PacketGameInfo;
import com.github.lg198.codefray.net.protocol.packet.PacketHelloServer;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;

public class CFServerHandler extends IoHandlerAdapter {

    private CFGame game;

    public CFServerHandler(CFGame g) {
        game = g;
    }

    @Override
    public void sessionOpened(IoSession session) {
        System.out.println("[SERVER] Client connected!");
        CodeFrayServer.ServerClient client = new CodeFrayServer.ServerClient();
        client.id = session.getId();
        CodeFrayServer.clients.add(client);
    }

    @Override
    public void messageReceived(IoSession session, Object message) {
        Packet p = (Packet) message;

        if (p instanceof PacketHelloServer) {
            CodeFrayServer.ServerClient sc = null;
            for (CodeFrayServer.ServerClient client : CodeFrayServer.clients) {
                if (client.id == session.getId()) {
                    sc = client;
                    break;
                }
            }

            sc.username = ((PacketHelloServer) p).name;

            PacketGameInfo info = new PacketGameInfo();
            info.blueControllerName = game.getController(Team.BLUE).name;
            info.redControllerName = game.getController(Team.RED).name;
            info.redName = game.getController(Team.RED).devId;
            info.blueName = game.getController(Team.BLUE).devId;
            info.gameStarted = game.isRunning();

            session.write(info);
        }
    }

    @Override
    public void sessionClosed(IoSession session) {
        System.out.println("[SERVER] Client disconnected!");
    }
}