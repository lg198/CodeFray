
package com.github.lg198.codefray.game.golem;

import com.github.lg198.codefray.api.game.TileType;
import com.github.lg198.codefray.api.math.Direction;
import com.github.lg198.codefray.api.game.Game;
import com.github.lg198.codefray.api.game.Team;
import com.github.lg198.codefray.api.golem.Golem;
import com.github.lg198.codefray.api.golem.GolemInfo;
import com.github.lg198.codefray.api.golem.GolemType;
import com.github.lg198.codefray.api.math.Point;
import com.github.lg198.codefray.api.math.Vector;
import com.github.lg198.codefray.game.CFGame;
import com.github.lg198.codefray.game.map.MapTile;
import com.github.lg198.codefray.game.map.WallTile;

import java.util.ArrayList;
import java.util.List;

public class CFGolem implements Golem {

    private final CFGame game;
    private final GolemType type;
    private final int id;
    private final Team team;

    private Team flagHeld = null;
    private int health;

    private int shotsLeft, movesLeft;

    private Point location;


    public CFGolem(CFGame g, GolemType t, Team te, int i) {
        game = g;
        type = t;
        id = i;
        team = te;

        health = type.getMaxHealth();
        update();
    }

    public void setHasFlag(Team t) {
        flagHeld = t;
    }

    public void update() {
        shotsLeft = type.getMaxShots();
        movesLeft = type.getMaxMoves();
    }

    public void setHealth(int h) {
        health = h;
    }

    @Override
    public Game getGame() {
        return game;
    }

    @Override
    public GolemType getType() {
        return type;
    }

    @Override
    public int getId() {
        return id;
    }

    public Point getLocation() {
        return location;
    }
    public void setLocation(Point p) {
        location = p;
    }

    @Override
    public Team getTeam() {
        return team;
    }

    @Override
    public Team getHeldFlag() {
        return flagHeld;
    }

    @Override
    public boolean isHoldingFlag() {
        return flagHeld != null;
    }

    @Override
    public int getHealth() {
        return health;
    }

    @Override
    public List<GolemInfo> search() {
        List<GolemInfo> ret = new ArrayList<GolemInfo>();
        for (CFGolem g : game.searchGolems(this, type.getMaxSearchRadiusSquared())) {
            ret.add(new CFGolemInfoWrapper(g, getGame().getRound()));
        }
        return ret;
    }

    @Override
    public Direction getFlagDirection(Team t) {
        int xc = 0, yc = 0;
        Point p = game.getFlagLocation(t);
        return Direction.between(getLocation(), p);
    }

    @Override
    public void move(Direction d) {
        if (movesLeft <= 0) {
            return;
        }
        if (game.getMap().isGolemMoveValid(this, d)) {
            Point old = getLocation();
            setLocation(getLocation().in(d));
            game.getMap().moveGolem(this, old);
            movesLeft--;
        }
    }

    @Override
    public int getMovesLeft() {
        return movesLeft;
    }

    @Override
    public void shoot(GolemInfo gi) {
        if (shotsLeft-- <= 0) {
            return;
        }
        CFGolemInfoWrapper wrapper = (CFGolemInfoWrapper) gi;
        double max = getType().getMaxSearchRadiusSquared();
        double dist = Vector.between(getLocation(), wrapper.getLocation()).getMagnitudeSquared();
        int strength = (int) (((max - dist) / max) * getType().getMaxShotStrength());
        wrapper.getGolem().setHealth(wrapper.getGolem().getHealth() - strength);
    }

    @Override
    public int getShotsLeft() {
        return shotsLeft;
    }

    @Override
    public TileType detectTile(Direction d) {
        Point p = getLocation().in(d);
        if (p.getX() < 0 || p.getX() >= game.getMap().getWidth() ||
                p.getY() < 0 || p.getY() >= game.getMap().getHeight()) {
            return null;
        }
        MapTile mt = game.getMap().getTile(p);
        if (mt == null) {
            return TileType.EMPTY;
        }
        return mt.getTileType();
    }

}
