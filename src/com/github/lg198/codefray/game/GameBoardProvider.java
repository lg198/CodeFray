package com.github.lg198.codefray.game;

import com.github.lg198.codefray.api.game.Team;
import com.github.lg198.codefray.api.math.Point;
import com.github.lg198.codefray.game.golem.CFGolem;
import com.github.lg198.codefray.game.map.MapTile;

public interface GameBoardProvider {

    boolean isPaused();

    int golemIdAt(Point p);

    void selectGolem(int id);

    void deselectGolem();

    int getMapWidth();

    int getMapHeight();

    int getMapTileAt(Point p);

    Team getMapTileTeam(Point p);

    int golemType(int id);

    Team golemTeam(int id);
}
