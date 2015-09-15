package com.github.lg198.codefray.game.map;

import com.github.lg198.codefray.api.game.Team;
import com.github.lg198.codefray.api.game.TileType;
import com.github.lg198.codefray.game.CFGame;
import com.github.lg198.codefray.game.golem.CFGolem;

public class FlagTile extends MapTile implements GolemHabitat {

    private final Team team;

    public FlagTile(Team t) {
        super(TileType.FLAG);
        team = t;
    }

    public Team getTeam() {
        return team;
    }

    @Override
    public boolean onGolemEnter(CFGolem g) {
        if (g.getTeam() == team) {
            return false;
        }

        if (g.isHoldingFlag()) {
            return false;
        }

        g.setHasFlag(team);

        ((CFGame) g.getGame()).getMap().setTile(getMapPosition(), null);
        return true;
    }

    @Override
    public boolean canGolemEnter(CFGolem g) {
        return g.getTeam() != team && !g.isHoldingFlag();
    }
}
