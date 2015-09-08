
package com.github.lg198.codefray.game;

import com.github.lg198.codefray.api.golem.GolemController;
import com.github.lg198.codefray.api.golem.GolemType;
import com.github.lg198.codefray.api.math.*;
import com.github.lg198.codefray.api.game.Game;
import com.github.lg198.codefray.api.game.Team;
import com.github.lg198.codefray.api.math.Vector;
import com.github.lg198.codefray.game.golem.CFGolem;
import com.github.lg198.codefray.game.golem.CFGolemController;
import com.github.lg198.codefray.game.golem.CFGolemWrapper;
import com.github.lg198.codefray.game.map.*;
import com.github.lg198.codefray.jfx.CodeFrayApplication;
import com.github.lg198.codefray.jfx.MainGui;
import com.github.lg198.codefray.jfx.OptionsPanel;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.util.Duration;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;


public class CFGame implements Game, GameBoardProvider {

    private final CFMap map;
    private final List<CFGolem> golems = new ArrayList<CFGolem>();
    private final Team[] teams;
    private final Map<Team, CFGolemController> controllerMap;
    private final MainGui gui;
    private GameLog log;

    private volatile boolean running = false;
    private volatile boolean paused = false;
    private long round = 1;
    private int clockSpeed = 1333;

    private GameClock clock;

    private int totalHealth;

    public CFGame(CFMap m, Map<Team, CFGolemController> cm) {
        map = m;
        teams = Team.values();
        controllerMap = cm;
        gui = new MainGui(this, true);

        File folder = new File(System.getProperty("user.home"), ".codefray_v1");
        if (!folder.exists()) {
            folder.mkdir();
        }
        log = new GameLog(this, folder);
    }

    public CFGolemController getController(Team t) {
        return controllerMap.get(t);
    }

    public boolean isRunning() {
        return running;
    }

    public boolean isPaused() {
        return paused;
    }

    public CFMap getMap() {
        return map;
    }

    public MainGui getGui() {
        return gui;
    }

    public void start() {
        int id = 0;
        for (GolemSpawnTile t : map.getTilesOfType(GolemSpawnTile.class)) {
            Team team = t.team;
            GolemType type = t.golemType;
            CFGolem golem = new CFGolem(this, type, team, id++);
            golem.setLocation(t.getMapPosition());
            golems.add(golem);
        }

        for (CFGolem g : golems) {
            if (g.getTeam() != Team.RED) {
                continue;
            }
            totalHealth += g.getType().getMaxHealth();
        }

        clock = new GameClock(() -> {
            onRound();
            Platform.runLater(gui::update);
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

    public GameStatistics stop(GameEndReason reason) {
        running = false;
        long time = clock.stop();
        final GameStatistics s = new GameStatistics();
        s.rounds = round;
        int rcount = 0, bcount = 0;
        for (CFGolem g : golems) {
            if (g.getTeam() == Team.RED) {
                rcount++;
            } else {
                bcount++;
            }
        }
        s.redLeft = rcount;
        s.blueLeft = bcount;
        s.redHealthPercent = getPercentHealth(Team.RED);
        s.blueHealthPercent = getPercentHealth(Team.BLUE);
        s.reason = reason;
        s.timeInSeconds = time / 1000;

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                CodeFrayApplication.switchToResult(s, log.getLogFile());
            }
        });
        return s;
    }

    public void onRound() {
        Collections.shuffle(golems);
        for (CFGolem g : golems) {
            g.update();
            controllerMap.get(g.getTeam()).onRound(new CFGolemWrapper(round, g));
            if (!running) { //TO STOP GAME IN MIDDLE OF ROUND
                break;
            }
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
                stop(new GameEndReason.Win(t == Team.RED ? Team.BLUE : Team.RED, GameEndReason.Win.Reason.DEATH));
                return;
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
        int th = 0;
        for (CFGolem g : golems) {
            th += g.getTeam() == t ? g.getHealth() : 0;
        }

        return (double) th / totalHealth;
    }

    public List<CFGolem> searchGolems(CFGolem reference, int radius2) {
        List<CFGolem> found = new ArrayList<>();
        for (CFGolem golem : golems) {
            if (golem.getId() == reference.getId()) {
                continue;
            }
            if (Vector.between(reference.getLocation(), golem.getLocation()).getMagnitudeSquared() > radius2) {
                continue;
            }
            found.add(golem);
        }
        return found;
    }

    public CFGolem golemAt(Point p) {
        for (CFGolem g : golems) {
            if (g.getLocation().equals(p)) {
                return g;
            }
        }
        return null;
    }

    @Override
    public int golemIdAt(Point p) {
        return golemAt(p).getId();
    }

    @Override
    public void selectGolem(int id) {
        CFGolem match = golems.stream().filter(g -> g.getId() == id).limit(1).collect(Collectors.toList()).get(0);
        getGui().panel.golemSelected(golems.get(id));
    }

    @Override
    public void deselectGolem() {
        getGui().panel.removeGolemBox();
    }

    @Override
    public int getMapWidth() {
        return map.getWidth();
    }

    @Override
    public int getMapHeight() {
        return map.getHeight();
    }

    @Override
    public MapTile getMapTileAt(Point p) {
        return map.getTile(p);
    }

    @Override
    public int golemType(int id) {
        CFGolem match = golems.stream().filter(g -> g.getId() == id).limit(1).collect(Collectors.toList()).get(0);
        return match.getType().ordinal();
    }

    @Override
    public Team golemTeam(int id) {
        CFGolem match = golems.stream().filter(g -> g.getId() == id).limit(1).collect(Collectors.toList()).get(0);
        return match.getTeam();
    }

}
