package com.github.lg198.codefray.levelbuilder;

public class LoadedCell {

    public LoadedCell(int x, int y, int type, int team) {
        this.x = x;
        this.y = y;
        this.type = type;
        this.team = team;
    }

    public LoadedCell() {}


    public int x, y, type, team;
}
