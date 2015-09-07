package com.github.lg198.codefray.game;

import com.github.lg198.codefray.api.game.Team;

public class GameEndReason {

    public static final class Win extends GameEndReason { //type 0

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

    public static final class Infraction extends GameEndReason { //type 1

        public static enum Type {
            OUT_OF_ROUND
        }

        public Type type;
        public Team guilty;

        public Infraction(Team g, Type t) {
            guilty = g;
            type = t;
        }
    }

    public static final class Forced extends GameEndReason { //type 2

    }
}
