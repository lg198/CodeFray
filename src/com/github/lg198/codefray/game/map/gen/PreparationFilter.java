package com.github.lg198.codefray.game.map.gen;

import com.github.lg198.codefray.api.game.Team;
import com.github.lg198.codefray.game.map.FlagTile;
import com.github.lg198.codefray.game.map.MapTile;
import com.github.lg198.codefray.game.map.WinTile;

import java.util.Map;
import java.util.Random;

public class PreparationFilter extends GenFilter {

    @Override
    public void filter(Random r, MapTile[][] map, int width, int height, Map props) {
        int thirdHeight = (int) height / 3;
        boolean lr = r.nextBoolean();
        int ws = lr ? 0 : (int) ((width * 2d) / 3d);
        int we = lr ? (int) (width/3d) : width;

        int fx = r.nextInt(we - ws - 1) + ws;
        int fy = r.nextInt(thirdHeight);

        int wx = width - fx;

        map[fx][fy] = new FlagTile(Team.RED);
        map[wx][fy] = new WinTile(Team.RED);
    }
}
