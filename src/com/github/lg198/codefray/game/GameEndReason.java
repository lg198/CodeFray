package com.github.lg198.codefray.game;

import com.github.lg198.codefray.api.game.Team;
import com.github.lg198.codefray.net.protocol.packet.PacketGameEnd;

public abstract class GameEndReason {

    public static int WIN_INDEX = 0, INFRACTION_INDEX = 1, FORCED_INDEX = 2;

    public static final class Win extends GameEndReason {
        @Override
        public void filPacket(PacketGameEnd e) {
            e.type = WIN_INDEX;
            e.winner = winner.ordinal();
            e.reason = reason.ordinal();
        }

        public static enum Reason {
            FLAG, DEATH;
        }

        public Team winner;
        public Reason reason;

        public Win(Team t, Reason r) {
            winner = t;
            reason = r;
        }
    }

    public static final class Infraction extends GameEndReason {
        @Override
        public void filPacket(PacketGameEnd e) {
            e.type = INFRACTION_INDEX;
            e.guilty = guilty.ordinal();
            e.reason = type.ordinal();
        }

        public static enum Type {
            OUT_OF_ROUND, EXCEPTION
        }

        public Type type;
        public Team guilty;

        public Infraction(Team g, Type t) {
            guilty = g;
            type = t;
        }
    }

    public static final class Forced extends GameEndReason {
        @Override
        public void filPacket(PacketGameEnd e) {
            e.type = FORCED_INDEX;
        }

    }

    public abstract void filPacket(PacketGameEnd e);
}
