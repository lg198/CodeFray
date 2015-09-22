package com.github.lg198.codefray.view;

import com.github.lg198.codefray.api.game.Team;
import com.github.lg198.codefray.api.math.Point;
import com.github.lg198.codefray.game.GameBoardProvider;
import com.github.lg198.codefray.game.GameEndReason;
import com.github.lg198.codefray.game.map.MapTile;
import com.github.lg198.codefray.net.protocol.packet.PacketGameEnd;
import com.github.lg198.codefray.net.protocol.packet.PacketGameInfo;
import com.github.lg198.codefray.net.protocol.packet.PacketGamePause;
import com.github.lg198.codefray.net.protocol.packet.PacketMapData;
import com.github.lg198.codefray.view.jfx.ViewGui;

public class ViewGame implements GameBoardProvider {

    private ViewProfile profile;

    private int[][][] tiles;
    private int[][][] golems;
    private int[][] golemList;
    private String mapName, mapAuthor;
    private String redName, blueName, redControllerName, blueControllerName;

    private boolean running = false;
    private boolean paused = false;

    private ViewGui gui;

    public ViewGame(ViewProfile vp) {
        profile = vp;
        gui = new ViewGui(this);
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
        tiles = data.tiles;
        golems = new int[tiles.length][tiles[0].length][];
        for (int i = 0; i < data.golems.length; i++) {
            int[] golem = data.golems[i];
            golems[golem[0]][golem[1]] = new int[]{golem[2], golem[3]};
            golemList[i] = golem;
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
    }

    private void start() {

    }

    private void stop(GameEndReason reason) {

    }

    private void updateGui() {

    }

    @Override
    public boolean isPaused() {
        return paused;
    }

    @Override
    public int golemIdAt(Point p) {
        return golems[p.getX()][p.getY()][4];
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
        return tiles[p.getX()][p.getY()][2]; //type
    }

    @Override
    public Team getMapTileTeam(Point p) {
        return Team.values()[tiles[p.getX()][p.getY()][3]];
    }

    @Override
    public int golemType(int id) {
        for (int[] golem : golemList) {
            if (golem[4] == id) {
                return golem[2];
            }
        }
        return -1;
    }

    @Override
    public Team golemTeam(int id) {
        for (int[] golem : golemList) {
            if (golem[4] == id) {
                return Team.values()[golem[3]];
            }
        }
        return null;
    }
}
