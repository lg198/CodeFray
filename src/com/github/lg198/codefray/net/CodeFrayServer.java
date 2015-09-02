package com.github.lg198.codefray.net;

import com.github.lg198.codefray.net.protocol.CFProtocolFactory;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.serialization.ObjectSerializationCodecFactory;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;

import java.io.IOException;
import java.net.InetSocketAddress;

public class CodeFrayServer {

    public static final int PORT = 43444;

    private NioSocketAcceptor acceptor;

    public void start() throws IOException {
        acceptor = new NioSocketAcceptor();

        acceptor.getFilterChain().addLast("codec", new ProtocolCodecFilter(new CFProtocolFactory()));

        acceptor.setHandler(new CFServerHandler());

        acceptor.bind(new InetSocketAddress(PORT));
    }

    public void stop() throws IOException {
        acceptor.dispose(true);
    }

}
