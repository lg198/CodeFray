package com.github.lg198.codefray.view;

import com.github.lg198.codefray.api.game.Team;
import com.github.lg198.codefray.api.math.Point;
import com.github.lg198.codefray.game.GameBoardProvider;
import com.github.lg198.codefray.game.GameEndReason;
import com.github.lg198.codefray.game.GameStatistics;
import com.github.lg198.codefray.game.golem.CFGolem;
import com.github.lg198.codefray.game.map.MapTile;
import com.github.lg198.codefray.jfx.CodeFrayApplication;
import com.github.lg198.codefray.net.CodeFrayClient;
import com.github.lg198.codefray.net.protocol.packet.*;
import com.github.lg198.codefray.util.SingleCollector;
import com.github.lg198.codefray.util.Stylizer;
import com.github.lg198.codefray.view.jfx.ViewGui;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class ViewGame implements GameBoardProvider {

    public ViewProfile profile;

    private ViewTile[][] tiles;
    private ViewGolem[][] golems;
    private List<ViewGolem> golemList = new ArrayList<>();
    private String mapName, mapAuthor;
    private String redName, blueName, redControllerName, blueControllerName;

    private boolean running = false;
    private boolean paused = false;
    public volatile boolean initialized = false;

    public int totalHealth;
    public double redHealth, blueHealth;

    public ViewGui gui;

    public ViewGame(ViewProfile vp) {
        profile = vp;
        vp.game = this;
    }

    public void recGameInfo(PacketGameInfo info) {
        blueName = info.blueName;
        redName = info.redName;
        redControllerName = info.redControllerName;
        blueControllerName = info.blueControllerName;
        totalHealth = info.totalHealth;
        running = info.gameStarted;
        if (running && initialized) {
            start();
        }
    }

    public void recMapData(PacketMapData data) {
        mapName = data.mapName;
        mapAuthor = data.mapAuthor;
        tiles = new ViewTile[data.mapWidth][data.mapHeight];
        for (int x = 0; x < data.mapWidth; x++) {
            for (int y = 0; y < data.mapHeight; y++) {
                if (data.tiles[x][y].length > 0) {
                    int[] td = data.tiles[x][y];
                    tiles[x][y] = td.length == 1 ? new ViewTile(td[0]) : new ViewTile(td[0], td[1]);
                }
            }
        }
        golems = new ViewGolem[data.mapWidth][data.mapHeight];
        for (int i = 0; i < data.golems.length; i++) {
            int[] golem = data.golems[i];
            golems[golem[0]][golem[1]] = new ViewGolem(golem[0], golem[1], golem[4], golem[2], golem[3]);
            golemList.add(golems[golem[0]][golem[1]]);
        }

        if (!initialized) {
            initialized = true;
            gui = new ViewGui(this);
            Platform.runLater(() -> CodeFrayApplication.startViewGui(ViewGame.this));
            if (running) {
                start();
            }
        }
        updateGui();
    }

    public void recGameEnd(PacketGameEnd end) {
        running = false;
        GameEndReason reason;

        if (end.type == GameEndReason.FORCED_INDEX) {
            reason = new GameEndReason.Forced();
        } else if (end.type == GameEndReason.INFRACTION_INDEX) {
            reason = new GameEndReason.Infraction(Team.values()[end.guilty], GameEndReason.Infraction.Type.values()[end.reason]);
        } else {
            reason = new GameEndReason.Win(Team.values()[end.winner], GameEndReason.Win.Reason.values()[end.reason]);
        }
        stop(end, reason);
    }

    public void recGamePause(PacketGamePause pause) {
        paused = pause.paused;
        if (paused) {
            updateThread(() -> gui.summary.statusPause());
        } else {
            updateThread(() -> gui.summary.statusStart());
        }
    }


    public void recGolemDie(PacketGolemDie die) {
        golemList = golemList.stream().filter(g -> g.id != die.id).collect(Collectors.toList());
    }

    public void recRoundUpdate(PacketRoundUpdate update) {
        updateThread(() -> gui.summary.round.setText(update.round + ""));
    }

    public void recGolemUpdate(PacketGolemUpdate update) {
        ViewGolem go = golemList.stream().filter(g -> g.id == update.id).limit(1).collect(new SingleCollector<>());
        go.health = update.health;

        int health = golemList.stream().filter(g -> g.team == go.team).mapToInt(g -> g.health).sum();
        if (go.team == 0) {
            redHealth = health / (double) totalHealth;
            updateThread(() -> gui.summary.redHealth.setProgress(redHealth));
        } else {
            blueHealth = health / (double) totalHealth;
            updateThread(() -> gui.summary.blueHealth.setProgress(blueHealth));
        }
    }

    public void recChat(PacketChatToClient chat) {
        if (chat.name.equals(profile.username)) {
            return;
        }
        Platform.runLater(() -> addChatMessage(chat.name, chat.message));
    }

    private void start() {
        updateThread(() -> gui.summary.statusStart());
    }

    private void stop(PacketGameEnd end, GameEndReason reason) {
        GameStatistics s = new GameStatistics();
        s.reason = reason;
        s.rounds = end.rounds;
        s.timeInSeconds = end.timeInSeconds;
        s.redLeft = end.redLeft;
        s.blueLeft = end.blueLeft;
        s.redHealthPercent = end.redHealthPercent;
        s.blueHealthPercent = end.blueHealthPercent;

        CodeFrayApplication.switchToResult(s);
    }

    private void updateGui() {
        updateThread(() -> gui.board.update());
    }

    @Override
    public boolean isPaused() {
        return paused;
    }

    @Override
    public int golemIdAt(Point p) {
        ViewGolem g = golems[p.getX()][p.getY()];
        return g != null ? g.id : -1;
    }

    @Override
    public void selectGolem(int id) {
        //TODO: GUI SELECT GOLEM
    }

    @Override
    public void deselectGolem() {
        //TODO: GUI SELECT GOLEM
    }

    @Override
    public int getMapWidth() {
        return tiles.length;
    }

    @Override
    public int getMapHeight() {
        return tiles[0].length;
    }


    @Override
    public int getMapTileAt(Point p) {
        ViewTile t = tiles[p.getX()][p.getY()];
        return t != null ? t.type : 0;
    }

    @Override
    public Team getMapTileTeam(Point p) {
        return Team.values()[tiles[p.getX()][p.getY()].team];
    }

    @Override
    public int golemType(int id) {
        return golemList.stream().filter(g -> g.id == id).collect(new SingleCollector<>()).type;
    }

    @Override
    public Team golemTeam(int id) {
        return Team.values()[golemList.stream().filter(g -> g.id == id).collect(new SingleCollector<>()).team];
    }

    public void updateThread(Runnable run) {
        Platform.runLater(run);
    }

    public void sendChat(final String message) {
        PacketChatToServer packet = new PacketChatToServer();
        packet.message = message;
        CodeFrayClient.sendPacket(packet);
        updateThread(() -> addChatMessage(profile.username, message));
    }

    private void addChatMessage(String uname, String message) {
        gui.broadcast.addLine(Stylizer.text(
                uname + ": ",
                "-fx-fill", profile.username.equals(uname) ? "blue" : "gray",
                "-fx-font-weight", "bold"
        ), Stylizer.text(
                message,
                "-fx-fill", profile.username.equals(uname) ? "blue" : "gray"
        ));
    }
}
