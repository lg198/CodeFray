package com.github.lg198.codefray.game.map;

import com.github.lg198.codefray.api.game.Team;

public class FlagTile extends MapTile {

    private final Team team;

    public FlagTile(Team t) {
        team = t;
    }

    public Team getTeam() {
        return team;
    }
}
