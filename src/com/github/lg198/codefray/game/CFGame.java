
package com.github.lg198.codefray.game;

import com.github.lg198.codefray.api.game.Game;
import com.github.lg198.codefray.api.game.Team;
import com.github.lg198.codefray.api.game.TileType;
import com.github.lg198.codefray.api.golem.GolemType;
import com.github.lg198.codefray.api.math.Point;
import com.github.lg198.codefray.api.math.Vector;
import com.github.lg198.codefray.game.GameEndReason.Infraction;
import com.github.lg198.codefray.game.golem.CFGolem;
import com.github.lg198.codefray.game.golem.CFGolemController;
import com.github.lg198.codefray.game.golem.CFGolemWrapper;
import com.github.lg198.codefray.game.map.*;
import com.github.lg198.codefray.jfx.CodeFrayApplication;
import com.github.lg198.codefray.jfx.MainGui;
import com.github.lg198.codefray.net.CodeFrayServer;
import com.github.lg198.codefray.net.protocol.packet.*;
import com.github.lg198.codefray.util.ErrorAlert;
import com.github.lg198.codefray.util.Stylizer;
import javafx.application.Platform;

import java.io.File;
import java.io.IOException;
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

    private boolean broadcasted = false;

    public CFGame(CFMap m, Map<Team, CFGolemController> cm, boolean bc) {
        map = m;
        teams = Team.values();
        controllerMap = cm;
        gui = new MainGui(this, bc);

        File folder = new File(System.getProperty("user.home"), ".codefray_v1");
        if (!folder.exists()) {
            folder.mkdir();
        }
        log = new GameLog(this, folder);

        broadcasted = bc;

        if (bc) {
            try {
                CodeFrayServer.start(this);
                gui.bpanel.addLine(Stylizer.text(
                        "Server started successfully.",
                        "-fx-fill", "green",
                        "-fx-font-weight", "bold"));
            } catch (IOException e) {
                ErrorAlert.createAlert(
                        "Error",
                        "Broadcast Error",
                        "An error has occurred during an attempt to start the broadcast server.",
                        e
                ).showAndWait();
                throw new RuntimeException("Failed game initialization", e);
            }
        }
    }

    public boolean isBroadcasted() {
        return broadcasted;
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
            map.addGolem(golem);
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

        if (broadcasted) {
            PacketGameInfo info = new PacketGameInfo();
            fillPacket(info);
            CodeFrayServer.safeBroadcast(info);
            PacketMapData data = new PacketMapData();
            map.writePacket(data);
            CodeFrayServer.safeBroadcast(data);
        }
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

        if (reason instanceof GameEndReason.Infraction) {
            GameEndReason.Infraction i = (GameEndReason.Infraction) reason;
            System.out.println(i.guilty);
            System.out.println(i.type);
        }

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                CodeFrayApplication.switchToResult(s, log.getLogFile());
            }
        });

        if (broadcasted) {
            try {
                CodeFrayServer.stop(reason);
            } catch (IOException e) {
                ErrorAlert.createAlert(
                        "Error",
                        "Broadcast Error",
                        "An error occurred while attempting to shut down the broadcast server.",
                        e
                ).showAndWait();
            }
        }

        return s;
    }

    public void onRound() {
        if (broadcasted) {
            PacketRoundUpdate pru = new PacketRoundUpdate();
            pru.round = getRound();
            CodeFrayServer.safeBroadcast(pru);
        }
        Collections.shuffle(golems);
        for (CFGolem g : golems) {
            g.update();
            try {
                controllerMap.get(g.getTeam()).onRound(new CFGolemWrapper(round, g));
            } catch (Exception e) {
                new Exception("Infraction exception", e).printStackTrace();
                GameEndReason.Infraction inf = new GameEndReason.Infraction(g.getTeam(), GameEndReason.Infraction.Type.EXCEPTION);
                stop(inf);
                return;
            }
            if (broadcasted) {
                PacketGolemUpdate pgu = new PacketGolemUpdate();
                pgu.id = g.getId();
                pgu.health = g.getHealth();
                CodeFrayServer.safeBroadcast(pgu);
            }
            if (!running) { //TO STOP GAME IN MIDDLE OF ROUND
                break;
            }
        }
        ListIterator<CFGolem> gi = golems.listIterator();
        while (gi.hasNext()) {
            CFGolem g = gi.next();
            if (g.getHealth() <= 0) {
                gi.remove();
                map.removeGolem(g);
                if (broadcasted) {
                    PacketGolemDie pgd = new PacketGolemDie();
                    pgd.id = g.getId();
                    CodeFrayServer.safeBroadcast(pgd);
                }
                if (g.isHoldingFlag()) {
                    map.setTile(g.getLocation(), new FlagTile(g.getHeldFlag()));
                    if (broadcasted) {
                        PacketMapUpdate pmu = new PacketMapUpdate();
                        pmu.x = g.getLocation().getX();
                        pmu.y = g.getLocation().getY();
                        pmu.type = TileType.FLAG.ordinal();
                        pmu.data = new int[]{g.getHeldFlag().ordinal()};
                        CodeFrayServer.safeBroadcast(pmu);
                    }
                }
            }
        }

        for (Team t : teams) {
            if (getPercentHealth(t) <= 0d) {
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
        return map.golemAt(p);
    }

    @Override
    public int golemIdAt(Point p) {
        CFGolem g = golemAt(p);
        return g != null ? g.getId() : -1;
    }

    @Override
    public void selectGolem(int id) {
        CFGolem match = golems.stream().filter(g -> g.getId() == id).limit(1).filter(g -> {
            System.out.println("Found golem for id " + id + " that has id " + g.getId() + " and is on team " + g.getTeam());
            return true;
        }).collect(Collectors.toList()).get(0);
        getGui().panel.golemSelected(match);
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
    public int getMapTileAt(Point p) {
        return map.getTile(p).getTileType().ordinal();
    }

    @Override
    public Team getMapTileTeam(Point p) {
        MapTile mt = map.getTile(p);
        if (mt == null) {
            return null;
        }
        if (mt instanceof FlagTile) {
            return ((FlagTile) mt).getTeam();
        }
        if (mt instanceof WinTile) {
            return ((WinTile) mt).getTeam();
        }
        return null;
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

    @Override
    public TileType getTypeAt(Point p) {
        return map.getTile(p).getTileType();
    }

    public void fillPacket(PacketGameInfo info) {
        info.blueControllerName = getController(Team.BLUE).name;
        info.redControllerName = getController(Team.RED).name;
        info.redName = getController(Team.RED).devId;
        info.blueName = getController(Team.BLUE).devId;
        info.gameStarted = isRunning();
    }

}
