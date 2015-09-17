package com.github.lg198.codefray.controllers;

import com.github.lg198.codefray.api.game.Team;
import com.github.lg198.codefray.api.golem.*;
import com.github.lg198.codefray.api.math.Direction;
import com.github.lg198.codefray.api.math.Point;
import com.github.lg198.codefray.api.math.Vector;

import java.util.List;

import java.util.List;

@ControllerDef(
        id = "com.github.lg198.Default",
        name = "CodeFray v1 Included Controller - Default",
        version = "1.0",
        devId = "dfc61082ebcc29a8eee96284e2a26e42")
public class DefaultController implements GolemController {

<<<<<<< HEAD

    @Override
    public void onRound(Golem g) {
        if (g.getTeam() == Team.RED) {
            List<GolemInfo> search = g.search();
            if (search.isEmpty()) {
                attemptMove(g, g.getFlagDirection(Team.BLUE));
            } else {
                search.stream().filter(gi -> gi.getTeam() == Team.BLUE).limit(g.getShotsLeft()).forEach(gi -> {
                    while (g.getMovesLeft() > 0 && Vector.between(g.getLocation(), gi.getLocation()).getMagnitudeSquared() > 1)
                        g.move(Direction.between(g.getLocation(), gi.getLocation()));
                    g.shoot(gi);
                });
            }
=======
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
>>>>>>> d61b6d3f882fe54b63d01de359a957542844290b
        }
    }

<<<<<<< HEAD
=======
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

>>>>>>> d61b6d3f882fe54b63d01de359a957542844290b
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

<<<<<<< HEAD

}
=======
    private void shootAll(Golem g) {
        g.search().stream().filter(gi -> gi.getTeam() == otherTeam).forEach(gi -> g.shoot(gi));
    }
}
>>>>>>> d61b6d3f882fe54b63d01de359a957542844290b
