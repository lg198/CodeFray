
package com.github.lg198.codefray.game;

import com.github.lg198.codefray.api.golem.GolemController;
import com.github.lg198.codefray.api.golem.GolemType;
import com.github.lg198.codefray.api.math.Direction;
import com.github.lg198.codefray.api.game.Game;
import com.github.lg198.codefray.api.game.Team;
import com.github.lg198.codefray.api.math.Point;
import com.github.lg198.codefray.game.golem.CFGolem;
import com.github.lg198.codefray.game.golem.CFGolemWrapper;
import com.github.lg198.codefray.game.map.CFMap;
import com.github.lg198.codefray.game.map.FlagTile;
import com.github.lg198.codefray.game.map.WinTile;
import com.github.lg198.codefray.jfx.MainGui;
import com.github.lg198.codefray.jfx.OptionsPanel;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.util.Duration;

import java.util.*;


public class CFGame implements Game {

    private final CFMap map;
    private final List<CFGolem> golems = new ArrayList<CFGolem>();
    private final Team[] teams;
    private final Map<Team, GolemController> controllerMap;
    private final MainGui gui;

    private volatile boolean running = false;
    private volatile boolean paused = false;
    private long round = 1;
    private int clockSpeed = 1333;

    private GameClock clock;

    public CFGame(CFMap m, Map<Team, GolemController> cm) {
        map = m;
        teams = Team.values();
        controllerMap = cm;
        gui = new MainGui(this);
    }

    public boolean isRunning() {
        return running;
    }
    public boolean isPaused() { return paused;}

    public CFMap getMap() {
        return map;
    }

    public MainGui getGui() {
        return gui;
    }

    public void start() {
        //TODO: SPAWN GOLEMS FOR EACH TEAM, PLACE FLAGS

        CFGolem g1 = new CFGolem(this, GolemType.RUNNER, Team.RED, 0);
        CFGolem g2 = new CFGolem(this, GolemType.RUNNER, Team.BLUE, 1);

        for (int i = 0; i < getMap().getWidth(); i++) {
            if (getMap().getTile(new Point(i, 0)) == null) {
                getMap().setTile(new Point(i, 0), g1);
                break;
            }
        }

        for (int i = getMap().getWidth()-1; i >= 0; i--) {
            if (getMap().getTile(new Point(i, getMap().getHeight()-1)) == null) {
                getMap().setTile(new Point(i, getMap().getHeight()-1), g2);
                break;
            }
        }

        golems.add(g1);
        golems.add(g2);

        clock = new GameClock(new Runnable() {
            @Override
            public void run() {
                onRound();
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        gui.update();
                    }
                });
            }
        });
        running = true;
        clock.start();
    }

    public void setClockSpeed(int s) {
        clockSpeed = s;
        clock.setDelay(clockSpeed);
    }

    public void pause() {
        paused = true;
        clock.pause();
    }

    public void unpause() {
        paused = false;
        clock.unpause();
        gui.panel.removeGolemBox();
    }

    public void stop() {
        running = false;
        clock.stop();
    }

    public void onRound() {
        Collections.shuffle(golems);
        for (CFGolem g : golems) {
            g.update();
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

        for (Team t : teams) {
            if (getPercentHealth(t) == 0d) {
                //TODO: WIN!
            }
        }

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

    @Override
    public Point getWinLocation(Team t) {
        for (WinTile p : map.getTilesOfType(WinTile.class)) {
            if (p.getTeam() == t) {
                return p.getMapPosition();
            }
        }
        return null;
    }

    public double getPercentHealth(Team t) {
        int tmh = 0, th = 0;
        for (CFGolem g : golems) {
            tmh += g.getTeam()==t ? g.getType().getMaxHealth() : 0;
            th += g.getTeam()==t ? g.getHealth() : 0;
        }

        return (double) th/tmh;
    }


}
