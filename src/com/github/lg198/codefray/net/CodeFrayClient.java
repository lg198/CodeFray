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

public class CodeFrayClient {

    private static NioSocketConnector connector;
    public static IoSession session;

    public static void start(String address, ViewProfile profile) throws IOException {
        connector = new NioSocketConnector();

        connector.getFilterChain().addLast("codec", new ProtocolCodecFilter(new CFProtocolFactory()));
        connector.getFilterChain().addFirst("logger", new IoFilterAdapter() {


            @Override
            public void exceptionCaught(NextFilter nextFilter, IoSession ioSession, Throwable throwable) throws Exception {
                new IOException("Client error", throwable).printStackTrace();
            }


            @Override
            public void messageReceived(NextFilter nextFilter, IoSession ioSession, Object o) throws Exception {
                System.out.println("client received message");
            }

            @Override
            public void messageSent(NextFilter nextFilter, IoSession ioSession, WriteRequest writeRequest) throws Exception {
                System.out.println("Sending message from client to server");
            }

            @Override
            public void filterWrite(NextFilter nextFilter, IoSession session, WriteRequest writeRequest) throws Exception {
                System.out.println("Client Filter write " + writeRequest.getMessage().getClass().getSimpleName());
            }
        });

        connector.setHandler(new CFClientHandler(profile));
        connector.getSessionConfig().setSendBufferSize(2048);

        ConnectFuture future = connector.connect(new InetSocketAddress(address, CodeFrayServer.PORT));
        future.awaitUninterruptibly();
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
        if (!CFPacket.REV_PACKETS.containsKey(p.getClass())) {
            throw new IllegalArgumentException("Class " + p.getClass().getSimpleName() + " is not a registered packet!");
        }

        int id = CFPacket.REV_PACKETS.get(p.getClass());
        CFPacket packet = new CFPacket(id);
        try {
            packet.content = p.write();
            packet.size = packet.content.length;
            WriteFuture future = session.write(packet);
            future.awaitUninterruptibly(5000);
            System.out.println("Is written? " + future.isWritten() + ", is done? " + future.isDone());
            if (future.getException() != null) {
                throw new IOException(future.getException());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void shutdown() {
        if (connector != null) {
            connector.dispose();
        }
    }
}
