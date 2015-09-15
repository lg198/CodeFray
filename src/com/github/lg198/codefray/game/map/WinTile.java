package com.github.lg198.codefray.game.map;

import com.github.lg198.codefray.api.game.Team;
import com.github.lg198.codefray.api.game.TileType;
import com.github.lg198.codefray.game.CFGame;
import com.github.lg198.codefray.game.GameEndReason;
import com.github.lg198.codefray.game.golem.CFGolem;

public class WinTile extends MapTile implements GolemHabitat {

    private Team team;

    public WinTile(Team t) {
        super(TileType.WIN);
        team = t;
    }

    public Team getTeam() {
        return team;
    }

    @Override
    public boolean onGolemEnter(CFGolem g) {
        if (g.getTeam() != team || !g.isHoldingFlag()) {
            return false;
        }

        ((CFGame) g.getGame()).stop(new GameEndReason.Win(g.getTeam(), GameEndReason.Win.Reason.FLAG));
        return true;
    }

    @Override
    public boolean canGolemEnter(CFGolem g) {
        return g.getTeam() == team && g.isHoldingFlag();
    }
}
