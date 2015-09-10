package com.github.lg198.codefray.net;

import com.github.lg198.codefray.game.CFGame;
import com.github.lg198.codefray.net.protocol.CFProtocolFactory;
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

    public static void start(CFGame g) throws IOException {
        acceptor = new NioSocketAcceptor();
        game = g;

        acceptor.getFilterChain().addLast("codec", new ProtocolCodecFilter(new CFProtocolFactory()));

        acceptor.setHandler(new CFServerHandler(game));

        acceptor.bind(new InetSocketAddress(PORT));
    }

    public static void stop() throws IOException {
        acceptor.dispose(true);
    }

    static class ServerClient {
        public String username;
        public long id;
    }

}
