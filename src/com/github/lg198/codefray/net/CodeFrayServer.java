package com.github.lg198.codefray.net;

import com.github.lg198.codefray.game.CFGame;
import com.github.lg198.codefray.game.GameEndReason;
import com.github.lg198.codefray.net.protocol.CFProtocolFactory;
import com.github.lg198.codefray.net.protocol.packet.Packet;
import com.github.lg198.codefray.net.protocol.packet.PacketGameEnd;
import com.github.lg198.codefray.util.Stylizer;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.serialization.ObjectSerializationCodecFactory;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

public class CodeFrayServer {

    public static List<ServerClient> clients = new ArrayList<>();

    public static final int PORT = 43444;

    private static NioSocketAcceptor acceptor;
    private static CFGame game;

    public static boolean running = false;

    public static void start(CFGame g) throws IOException {
        acceptor = new NioSocketAcceptor();
        game = g;

        acceptor.getFilterChain().addLast("codec", new ProtocolCodecFilter(new CFProtocolFactory()));

        acceptor.setHandler(new CFServerHandler(game));

        running = true;

        acceptor.bind(new InetSocketAddress(PORT));
    }

    public static void stop(GameEndReason r) throws IOException {
        PacketGameEnd end = new PacketGameEnd();
        r.filPacket(end);
        acceptor.broadcast(end).forEach(writeFuture -> writeFuture.awaitUninterruptibly());
        acceptor.dispose(true);
        running = false;
    }

    public static void broadcast(Packet p) throws IOException {
        acceptor.broadcast(p);
    }

    public static void safeBroadcast(Packet p) {
        try {
            broadcast(p);
        } catch (IOException e) {
            game.getGui().bpanel.addLine(Stylizer.text(
                    "Error: can't send " + p.getClass().getSimpleName().replace("Packet", "") + " because " + e.getMessage(),
                    "-fx-fill", "red"));
        }
    }

    static class ServerClient {
        public String username;
        public long id;
    }

    public static void shutdown() {
        if (acceptor != null) {
            acceptor.dispose();
        }
    }

}
