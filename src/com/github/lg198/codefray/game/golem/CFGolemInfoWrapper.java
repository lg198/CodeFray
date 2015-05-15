package com.github.lg198.codefray.game.golem;


import com.github.lg198.codefray.api.game.Team;
import com.github.lg198.codefray.api.golem.GolemInfo;
import com.github.lg198.codefray.api.golem.GolemType;
import com.github.lg198.codefray.api.math.Point;

public class CFGolemInfoWrapper implements GolemInfo {

    private final CFGolem golem;
    private final long round;

    public CFGolemInfoWrapper(CFGolem g, long r) {
        golem = g;
        round = r;
    }

    private void check() {
        if (golem.getGame().getRound() != round) {
            //TODO: Cheater!
        }
    }

    public CFGolem getGolem() {
        check();
        return golem;
    }

    @Override
    public GolemType getType() {
        check();
        return golem.getType();
    }

    @Override
    public int getId() {
        check();
        return golem.getId();
    }

    @Override
    public Point getLocation() {
        check();
        return golem.getLocation();
    }

    @Override
    public Team getTeam() {
        check();
        return golem.getTeam();
    }

    @Override
    public Team getHeldFlag() {
        check();
        return golem.getHeldFlag();
    }

    @Override
    public boolean isHoldingFlag() {
        check();
        return golem.isHoldingFlag();
    }

    @Override
    public int getHealth() {
        check();
        return golem.getHealth();
    }
}
