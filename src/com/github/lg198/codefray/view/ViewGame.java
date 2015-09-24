package com.github.lg198.codefray.view;

import com.github.lg198.codefray.api.game.Team;
import com.github.lg198.codefray.api.math.Point;
import com.github.lg198.codefray.game.GameBoardProvider;
import com.github.lg198.codefray.game.GameEndReason;
import com.github.lg198.codefray.game.map.MapTile;
import com.github.lg198.codefray.net.protocol.packet.*;
import com.github.lg198.codefray.util.SingleCollector;
import com.github.lg198.codefray.view.jfx.ViewGui;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
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

    public ViewGui gui;

    public ViewGame(ViewProfile vp) {
        profile = vp;
        gui = new ViewGui(this);
        vp.game = this;
    }

    public void recGameInfo(PacketGameInfo info) {
        blueName = info.blueName;
        redName = info.redName;
        redControllerName = info.redControllerName;
        blueControllerName = info.blueControllerName;
        running = info.gameStarted;
        if (running) {
            start();
        } else {
            updateGui();
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
            Scene scene = new Scene(gui.build());
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("CodeFray Game Viewer");
            stage.show();
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
        stop(reason);
    }

    public void recGamePause(PacketGamePause pause) {
        paused = pause.paused;
        if (paused) {
            gui.summary.statusPause();
        } else {
            gui.summary.statusStart();
        }
    }

    public void recGolemMove(PacketGolemMove move) {
        ViewGolem g = null;
        for (ViewGolem gol : golemList) {
            if (gol.id == move.id) {
                g = gol;
                break;
            }
        }

        golems[g.x][g.y] = null;
        golems[move.x][move.y] = g;
        g.x = move.x;
        g.y = move.y;
    }

    public void recGolemDie(PacketGolemDie die) {
        golemList = golemList.stream().filter(g -> g.id != die.id).collect(Collectors.toList());
    }

    public void recRoundUpdate(PacketRoundUpdate update) {
        gui.summary.round.setText(update.round + "");
    }

    public void recGolemUpdate(PacketGolemUpdate update) {
        //do nothing for now
    }

    private void start() {
        gui.summary.statusStart();
    }

    private void stop(GameEndReason reason) {

    }

    private void updateGui() {
        gui.board.update();
    }

    @Override
    public boolean isPaused() {
        return paused;
    }

    @Override
    public int golemIdAt(Point p) {
        return golems[p.getX()][p.getY()].id;
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
        return tiles[p.getX()][p.getY()].type;
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
}
