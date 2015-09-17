package com.github.lg198.codefray.controllers;

import com.github.lg198.codefray.api.game.Team;
import com.github.lg198.codefray.api.golem.*;
import com.github.lg198.codefray.api.math.Direction;
import com.github.lg198.codefray.api.math.Point;
import com.github.lg198.codefray.api.math.Vector;

import java.util.List;

@ControllerDef(
        id = "com.github.lg198.Default",
        name = "CodeFray v1 Included Controller - Default",
        version = "1.0",
        devId = "dfc61082ebcc29a8eee96284e2a26e42")
public class DefaultController implements GolemController {


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
        }
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


}
