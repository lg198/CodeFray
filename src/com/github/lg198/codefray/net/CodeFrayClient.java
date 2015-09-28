package com.github.lg198.codefray.net;

import com.github.lg198.codefray.net.protocol.CFPacket;
import com.github.lg198.codefray.net.protocol.CFProtocolFactory;
import com.github.lg198.codefray.net.protocol.packet.Packet;
import com.github.lg198.codefray.net.protocol.packet.PacketHelloServer;
import com.github.lg198.codefray.view.ViewProfile;
import org.apache.mina.core.filterchain.IoFilter;
import org.apache.mina.core.filterchain.IoFilterAdapter;
import org.apache.mina.core.filterchain.IoFilterChain;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.future.WriteFuture;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.core.write.WriteRequest;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Arrays;

public class CodeFrayClient {

    private static NioSocketConnector connector;
    public static IoSession session;

    public static void start(String address, ViewProfile profile) throws IOException {
        connector = new NioSocketConnector();

        connector.getFilterChain().addLast("codec", new ProtocolCodecFilter(new CFProtocolFactory()));

        connector.setHandler(new CFClientHandler(profile));
        connector.getSessionConfig().setSendBufferSize(2048);

        ConnectFuture future = connector.connect(new InetSocketAddress(address, CodeFrayServer.PORT));
        future.awaitUninterruptibly(10000);
        Throwable c = future.getException();
        if (c != null) {
            c.printStackTrace();
            throw new IOException("Failed to connect!", c);
        }
        session = future.getSession();

        PacketHelloServer packet = new PacketHelloServer();
        packet.name = profile.username;
        sendPacket(packet);
    }

    public static void sendPacket(Packet p) {
        System.out.println("[CLIENT] SENDING PACKET " + p.getClass().getSimpleName());
        session.write(p);
    }

    public static void shutdown() {
        if (connector != null) {
            connector.dispose();
        }
    }
}
