package com.github.lg198.codefray.net;

import com.github.lg198.codefray.game.CFGame;
import com.github.lg198.codefray.net.protocol.packet.*;
import com.github.lg198.codefray.util.SingleCollector;
import com.github.lg198.codefray.util.Stylizer;
import javafx.application.Platform;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;

import java.util.ListIterator;

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
        Platform.runLater(() -> game.getGui().bpanel.setViewers(CodeFrayServer.clients.size()));
    }

    @Override
    public void messageReceived(IoSession session, Object message) {
        System.out.println("[SERVER] RECEIVED MESSAGE " + message.getClass().getSimpleName());
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
            final String uname = sc.username;

            Platform.runLater(() -> game.getGui().bpanel.addLine(
                    Stylizer.text(
                            uname,
                            "-fx-font-weight", "bold",
                            "-fx-fill", "green"
                    ),
                    Stylizer.text(
                            " has joined the broadcast.",
                            "-fx-fill", "green"
                    )
            ));

            PacketGameInfo info = new PacketGameInfo();
            game.fillPacket(info);
            session.write(info);

            if (game.isPaused()) {
                PacketGamePause pause = new PacketGamePause();
                pause.paused = true;
                session.write(pause);
            }
            if (game.isRunning()) {
                PacketRoundUpdate update = new PacketRoundUpdate();
                update.round = game.getRound();
                session.write(update);
            }

            PacketMapData data = new PacketMapData();
            game.getMap().writePacket(data);
            session.write(data);
        } else if (p instanceof PacketChatToServer) {
            PacketChatToServer chat = (PacketChatToServer) p;
            CodeFrayServer.ServerClient sc = CodeFrayServer.clients.stream().filter(c -> c.id == session.getId()).limit(1).collect(new SingleCollector<>());
            Platform.runLater(() -> game.getGui().bpanel.addChatMessage(sc.username, chat.message));
            PacketChatToClient client = new PacketChatToClient();
            client.message = chat.message;
            client.name = sc.username;
            CodeFrayServer.safeBroadcast(client);
        }
    }

    @Override
    public void exceptionCaught(IoSession session, Throwable ex) {
        ex.printStackTrace(System.err);
    }

    @Override
    public void sessionClosed(IoSession session) {
        System.out.println("[SERVER] Client disconnected!");
        ListIterator<CodeFrayServer.ServerClient> ci = CodeFrayServer.clients.listIterator();
        while (ci.hasNext()) {
            final CodeFrayServer.ServerClient client = ci.next();
            if (client.id == session.getId()) {
                Platform.runLater(() -> {
                    game.getGui().bpanel.addLine(
                            Stylizer.text(
                                    client.username,
                                    "-fx-font-weight", "bold",
                                    "-fx-fill", "red"
                            ),
                            Stylizer.text(
                                    " has disconnected.",
                                    "-fx-fill", "red"
                            )
                    );
                    game.getGui().bpanel.setViewers(CodeFrayServer.clients.size());
                });
                ci.remove();
                break;
            }
        }

    }
}