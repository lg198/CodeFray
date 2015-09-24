package com.github.lg198.codefray.view;

public class ViewTile {

    public int type;
    public int team;

    public ViewTile(int type) {
        this.type = type;
    }

    public ViewTile(int type, int team) {
        this(type);
        this.team = team;
    }
}
