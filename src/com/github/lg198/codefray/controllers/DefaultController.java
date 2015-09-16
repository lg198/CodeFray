package com.github.lg198.codefray.controllers;

import com.github.lg198.codefray.api.game.Team;
import com.github.lg198.codefray.api.golem.ControllerDef;
import com.github.lg198.codefray.api.golem.Golem;
import com.github.lg198.codefray.api.golem.GolemController;
import com.github.lg198.codefray.api.golem.GolemType;
import com.github.lg198.codefray.api.math.Direction;
import com.github.lg198.codefray.api.math.Point;

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
            System.out.println(g.getType());
        }

        if (g.getType() == GolemType.RUNNER) {
            while (g.getMovesLeft() > 0) {
                Direction toFlag = g.getFlagDirection(otherTeam);
                if (toFlag == null) {
                    break;
                }
                if (!g.canMove(toFlag)) {
                    Direction temp = toFlag;
                    boolean moved = false;
                    do {
                        temp = toFlag.clockwise();
                        if (g.canMove(temp)) {
                            moved = true;
                            break;
                        }
                    } while (toFlag != temp);
                    if (!moved) {
                        g.search().forEach(golem -> {
                            if (g.getShotsLeft() > 0) g.shoot(golem);
                        });
                        return;
                    }
                    g.move(temp);
                }
                g.move(toFlag);
            }
            return;
        }

        if (g.getType() == GolemType.DEFENDER) {
            Point defpos = g.getGame().getFlagLocation(thisTeam).translate(0, -1);
            if (!defpos.equals(g.getLocation())) {
                Direction toMove = Direction.between(g.getLocation(), defpos);
                while (g.getMovesLeft() > 0) {
                    if (g.canMove(toMove)) {
                        g.move(toMove);
                        continue;
                    }

                    Direction temp = toMove;
                    do {
                        temp = toMove.clockwise();
                        if (g.canMove(temp)) {
                            g.move(temp);
                            break;
                        }
                    } while (temp != toMove);
                }
            }
            g.search().stream().limit(g.getShotsLeft()).forEach(golem -> g.shoot(golem));
        }
    }

}
