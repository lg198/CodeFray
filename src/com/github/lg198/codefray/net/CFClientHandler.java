package com.github.lg198.codefray.net;

import com.github.lg198.codefray.jfx.CodeFrayApplication;
import com.github.lg198.codefray.net.protocol.packet.*;
import com.github.lg198.codefray.view.ViewProfile;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;

public class CFClientHandler extends IoHandlerAdapter {

    private ViewProfile profile;

    public CFClientHandler(ViewProfile p) {
        profile = p;
    }

    @Override
    public void sessionOpened(IoSession session) {
        System.out.println("[CLIENT] Connected to server!");
    }

    @Override
    public void messageReceived(IoSession session, Object message) {
        Packet p = (Packet) message;

        if (p instanceof PacketGameInfo) {
            profile.game.recGameInfo((PacketGameInfo) p);
        } else if (p instanceof PacketMapData) {
            profile.game.recMapData((PacketMapData) p);
        } else if (p instanceof PacketGamePause) {
            profile.game.recGamePause((PacketGamePause) p);
        } else if (p instanceof PacketGameInfo) {
            profile.game.recGameEnd((PacketGameEnd) p);
            session.close(false);
        } else if (p instanceof PacketGolemUpdate) {
            profile.game.recGolemUpdate((PacketGolemUpdate) p);
        } else if (p instanceof PacketGolemMove) {
            profile.game.recGolemMove((PacketGolemMove) p);
        } else if (p instanceof PacketGolemDie) {
            profile.game.recGolemDie((PacketGolemDie) p);
        } else if (p instanceof PacketRoundUpdate) {
            profile.game.recRoundUpdate((PacketRoundUpdate) p);
        }
    }

    @Override
    public void sessionClosed(IoSession session) {
        System.out.println("[CLIENT] Disconnected!");
        Text dtext = new Text("You have been disconnected.");
        dtext.setFill(Color.RED);
        profile.game.gui.broadcast.addLine(dtext);
        session.getService().dispose();
    }
}
