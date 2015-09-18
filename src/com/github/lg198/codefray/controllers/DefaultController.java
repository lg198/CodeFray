package com.github.lg198.codefray.controllers;

import com.github.lg198.codefray.api.game.Team;
import com.github.lg198.codefray.api.golem.ControllerDef;
import com.github.lg198.codefray.api.golem.Golem;
import com.github.lg198.codefray.api.golem.GolemController;
import com.github.lg198.codefray.api.golem.GolemType;
import com.github.lg198.codefray.api.math.Direction;

@ControllerDef(
        id = "com.github.lg198.Default",
        name = "CodeFray v1 Included Controller - Default",
        version = "1.0",
        devId = "dfc61082ebcc29a8eee96284e2a26e42")
public class DefaultController implements GolemController {

    public boolean initialized = false;

    public Team thisTeam, otherTeam;

    @Override
    public void onRound(Golem g) {
        if (!initialized) {
            thisTeam = g.getTeam();
            otherTeam = thisTeam == Team.BLUE ? Team.RED : Team.BLUE;
            initialized = true;
        }

        if (g.getType() == GolemType.RUNNER) {
            onRoundRunner(g);

        }
    }
    

    private void onRoundRunner(Golem g) {
        shootAll(g);
        if (g.getLocation().equals(g.getGame().getFlagLocation(otherTeam)) || g.isHoldingFlag()) {
            boolean result;
            do {
                result = attemptMove(g, Direction.between(g.getLocation(), g.getGame().getWinLocation(thisTeam)));
            } while (g.getMovesLeft() > 0 && result);
            return;
        }
        boolean result;
        do {
            result = attemptMove(g, g.getFlagDirection(otherTeam));
        } while (g.getMovesLeft() > 0 && result);
    }

    private boolean attemptMove(Golem g, Direction direct) {
        Direction next = direct;
        do {
            if (g.canMove(next)) {
                g.move(next);
                return true;
            }
            next = next.clockwise();
        } while (direct != next);

        return false;
    }


    private void shootAll(Golem g) {
        g.search().stream().filter(gi -> gi.getTeam() == otherTeam).forEach(gi -> g.shoot(gi));
    }
}