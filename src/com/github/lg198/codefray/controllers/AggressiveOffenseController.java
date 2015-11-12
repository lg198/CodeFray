package com.github.lg198.codefray.controllers;

import com.github.lg198.codefray.api.game.Team;
import com.github.lg198.codefray.api.golem.ControllerDef;
import com.github.lg198.codefray.api.golem.Golem;
import com.github.lg198.codefray.api.golem.GolemController;
import com.github.lg198.codefray.api.golem.GolemType;
import com.github.lg198.codefray.api.math.Direction;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Stream;

@ControllerDef(
        id = "com.github.lg198.AggressiveOffense",
        name = "CodeFray v1 Included - AggressiveOffense",
        version = "1.0",
        devId = "default")
/**
 * This controller features assault and runner golems who rush the other team.
 * The defenders wander randomly, searching for members of the other team.
 */
public class AggressiveOffenseController implements GolemController {

    public static Team thisTeam, thatTeam;
    public static boolean initialized = false;

    public Map<Integer, Queue<Direction>> paths = new HashMap<>();

    @Override
    public void onRound(Golem g) {
        try {
            if (!initialized) {
                initialized = true;
                thisTeam = g.getTeam();
                thatTeam = Team.otherTeam(thisTeam);
            }

            if (g.getType() == GolemType.DEFENDER) {
                onRoundDefender(g);
            } else {
                onRoundOffense(g);
            }
        } catch (Exception e) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            pw.close();
            Logger.getLogger(AggressiveOffenseController.class.getSimpleName()).severe(sw.toString());
        }
    }

    private void onRoundDefender(Golem g) {
        while (g.getMovesLeft() > 0) {
            //move somewhere... SOMEWHERE!
            randomDirectionStream().forEach(d -> {
                if (g.canMove(d)) {
                    
                }
            });
        }
    }

    private void onRoundOffense(Golem g) {

    }

    private Stream<Direction> randomDirectionStream() {
        List l = Arrays.asList(Arrays.copyOf(Direction.values(), Direction.values().length));
        Collections.shuffle(l);
        return l.stream();
    }
}
