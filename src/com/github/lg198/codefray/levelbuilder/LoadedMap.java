package com.github.lg198.codefray.levelbuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class LoadedMap {

    public LoadedCell[][] cells;
    public String name, author;

    private List<LoadedGolem> golems = new ArrayList<>();

    public LoadedGolem[][] golemMap;

    public LoadedMap(int w, int h) {
        cells = new LoadedCell[w][h];
        golemMap = new LoadedGolem[w][h];
    }

    public void set(int x, int y, LoadedCell cell) {
        cells[x][y] = cell;
        if (cell != null) {
            cell.x = x;
            cell.y = y;
        }
    }

    public void setMirrored(int x, int y, LoadedCell cell) {
        set(x, y, cell);
        int nx = getWidth() - x - 1, ny = getHeight() - y - 1;
        LoadedCell c2 = new LoadedCell(nx, ny, cell.type, cell.team);
        cells[nx][ny] = c2;
    }

    public int getWidth() {
        return cells.length;
    }

    public int getHeight() {
        return cells[0].length;
    }

    public void addGolem(LoadedGolem g) {
        golems.add(g);
        if (golemMap[g.x][g.y] != null) {
            golems.remove(golemMap[g.x][g.y]);
        }
        golemMap[g.x][g.y] = g;
    }

    public void removeGolem(int x, int y) {
        LoadedGolem g = golemMap[x][y];
        if (g != null) {
            golemMap[x][y] = null;
            golems.remove(g);
        }
    }

    public int golemCount() {
        return golems.size();
    }

    public List<LoadedGolem> getGolems() {
        return Collections.unmodifiableList(golems);
    }
}
