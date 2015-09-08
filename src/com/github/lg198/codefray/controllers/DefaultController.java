package com.github.lg198.codefray.controllers;

import com.github.lg198.codefray.api.game.Team;
import com.github.lg198.codefray.api.game.TileType;
import com.github.lg198.codefray.api.golem.ControllerDef;
import com.github.lg198.codefray.api.golem.Golem;
import com.github.lg198.codefray.api.golem.GolemController;
import com.github.lg198.codefray.api.math.Direction;

@ControllerDef(
        id = "com.github.lg198.Default",
        name = "CodeFray v1 Included Controller - Default",
        version = "1.0",
        devId = "dfc61082ebcc29a8eee96284e2a26e42")
public class DefaultController implements GolemController {

    public boolean initialized = false;
    public Team thisTeam, otherTeam;

    public Direction cd;

    @Override
    public void onRound(Golem g) {
        if (!initialized) {
            thisTeam = g.getTeam();
            otherTeam = thisTeam == Team.BLUE ? Team.RED : Team.BLUE;
            initialized = true;
            cd = g.getFlagDirection(otherTeam);
            System.out.println("CD: " + cd);
        }

        Direction next = determineNext(g);
        if (next != null) {
            cd = next;
            g.move(next);
        }


    }

    public Direction determineNext(Golem g) {
        Direction d = cd;
        do {
            if (g.detectTile(d) == TileType.EMPTY) {
                return d;
            }
            d = d.clockwise();
        } while (d != cd);
        return null;
    }

}
