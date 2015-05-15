
package com.github.lg198.codefray.game;

import com.github.lg198.codefray.api.golem.GolemController;
import com.github.lg198.codefray.api.math.Direction;
import com.github.lg198.codefray.api.game.Game;
import com.github.lg198.codefray.api.game.Team;
import com.github.lg198.codefray.api.math.Point;
import com.github.lg198.codefray.game.golem.CFGolem;
import com.github.lg198.codefray.game.golem.CFGolemWrapper;
import com.github.lg198.codefray.game.map.CFMap;
import com.github.lg198.codefray.game.map.FlagTile;

import java.util.*;


public class CFGame implements Game {

    private final CFMap map;
    private final List<CFGolem> golems = new ArrayList<CFGolem>();
    private final Team[] teams;
    private final Map<Team, GolemController> controllerMap;

    private long round = 1;

    public CFGame(CFMap m, Map<Team, GolemController> cm) {
        map = m;
        teams = Team.values();
        controllerMap = cm;
    }

    public CFMap getMap() {
        return map;
    }

    public void start() {
        //TODO: SPAWN GOLEMS FOR EACH TEAM, PLACE FLAGS, THEN START CLOCK
    }

    public void onRound() {
        Collections.shuffle(golems);
        for (CFGolem g : golems) {
            controllerMap.get(g.getTeam()).onRound(new CFGolemWrapper(round, g));
        }
        ListIterator<CFGolem> gi = golems.listIterator();
        while (gi.hasNext()) {
            CFGolem g = gi.next();
            if (g.getHealth() <= 0) {
                gi.remove();
                map.setTile(g.getLocation(), null);
                if (g.isHoldingFlag()) {
                    map.setTile(g.getLocation(), new FlagTile(g.getHeldFlag()));
                }
            }
        }

        //TODO: SEARCH FOR "OUT" TEAMS

        round++;
    }

    @Override
    public boolean isHoldingFlag(Team t) {
        for (CFGolem g : golems) {
            if (g.getTeam() == t && g.isHoldingFlag()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean flagIsHeld(Team t) {
        for (CFGolem g : golems) {
            if (g.isHoldingFlag() && g.getHeldFlag() == t) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Point getFlagLocation(Team t) {
        Point p = map.getFlag(t);
        if (p != null) {
            return p;
        }

        for (CFGolem g : golems) {
            if (g.getHeldFlag() == t) {
                return g.getLocation();
            }
        }
        return null;
    }

    @Override
    public long getRound() {
        return round;
    }

    @Override
    public Team[] getTeams() {
        return teams;
    }


}
