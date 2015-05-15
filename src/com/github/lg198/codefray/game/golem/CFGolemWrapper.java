
package com.github.lg198.codefray.game.golem;

import com.github.lg198.codefray.api.math.Direction;
import com.github.lg198.codefray.api.game.Game;
import com.github.lg198.codefray.api.game.Team;
import com.github.lg198.codefray.api.golem.Golem;
import com.github.lg198.codefray.api.golem.GolemInfo;
import com.github.lg198.codefray.api.golem.GolemType;
import com.github.lg198.codefray.api.math.Point;
import java.util.List;


public final class CFGolemWrapper implements Golem {
    
    private final long round;
    private final CFGolem golem;
    
    public CFGolemWrapper(long round, CFGolem golem) {
        this.round = round;
        this.golem = golem;
    }
    
    private void check() {
        if (golem.getGame().getRound() != round) {
            //TODO: Cheater!
        }
    }

    @Override
    public Game getGame() {
        check();
        return golem.getGame();
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

    @Override
    public List<GolemInfo> search() {
        check();
        return golem.search();
    }

    @Override
    public Direction getFlagDirection(Team t) {
        check();
        return golem.getFlagDirection(t);
    }

    @Override
    public void move(Direction d) {
        check();
        golem.move(d);
    }

    @Override
    public int getMovesLeft() {
        check();
        return golem.getMovesLeft();
    }

    @Override
    public void shoot(GolemInfo gi) {
        check();
        golem.shoot(gi);
    }

    @Override
    public int getShotsLeft() {
        check();
        return golem.getShotsLeft();
    }

    @Override
    public boolean isWall(Direction d) {
        check();
        return false;
    }

    @Override
    public boolean isGolem(Direction d) {
        check();
        return false;
    }

}
