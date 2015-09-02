package com.github.lg198.codefray.game.map;

import com.github.lg198.codefray.api.game.Team;
import com.github.lg198.codefray.api.game.TileType;
import com.github.lg198.codefray.api.golem.GolemType;

public class GolemSpawnTile extends MapTile {

    public GolemType golemType;
    public Team team;

    public GolemSpawnTile(GolemType gt, Team t) {
        super(TileType.EMPTY);
        golemType = gt;
        team = t;
    }
}
