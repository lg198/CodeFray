package com.github.lg198.codefray.net;

import com.github.lg198.codefray.net.protocol.packet.*;
import com.github.lg198.codefray.util.Stylizer;
import com.github.lg198.codefray.view.ViewProfile;
import javafx.application.Platform;
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
        profile.game.updateThread(() -> Stylizer.text(
                "You have connected to the broadcast.",
                "-fx-fill", "green",
                "-fx-font-weight", "bold"
        ));
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
        } else if (p instanceof PacketGolemDie) {
            profile.game.recGolemDie((PacketGolemDie) p);
        } else if (p instanceof PacketRoundUpdate) {
            profile.game.recRoundUpdate((PacketRoundUpdate) p);
        } else if (p instanceof PacketChatToClient) {
            profile.game.recChat((PacketChatToClient) p);
        }
    }

    @Override
    public void sessionClosed(IoSession session) {
        System.out.println("[CLIENT] Disconnected!");
        Platform.runLater(() -> profile.game.gui.broadcast.addLine(Stylizer.text(
                "You have been disconnected.",
                "-fx-fill", "red"
        )));
        session.getService().dispose();
    }
}
