package com.github.lg198.codefray.net;

import com.github.lg198.codefray.net.protocol.CFProtocolFactory;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

import java.io.IOException;
import java.net.InetSocketAddress;

public class CodeFrayClient {

    private static NioSocketConnector connector;

    public static void start(String address) throws IOException {
        connector = new NioSocketConnector();

        connector.getFilterChain().addLast("codec", new ProtocolCodecFilter(new CFProtocolFactory()));

        connector.setHandler(new CFClientHandler());

        connector.connect(new InetSocketAddress(address, CodeFrayServer.PORT));
    }
}
