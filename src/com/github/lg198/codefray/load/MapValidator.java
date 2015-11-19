package com.github.lg198.codefray.load;

import com.github.lg198.codefray.api.game.Team;
import com.github.lg198.codefray.api.math.Point;
import com.github.lg198.codefray.game.map.CFMap;
import com.github.lg198.codefray.game.map.FlagTile;
import com.github.lg198.codefray.game.map.GolemSpawnTile;
import com.github.lg198.codefray.game.map.WinTile;

public class MapValidator {

    public static String validate(CFMap map) {
        //both sides must have equal number of golems
        int redGolems = 0, blueGolems = 0;
        for (int x = 0; x < map.getWidth(); x++) {
            for (int y = 0; y < map.getHeight(); y++) {
                if (map.getTile(new Point(x, y)) instanceof GolemSpawnTile) {
                    if (((GolemSpawnTile) map.getTile(new Point(x, y))).team == Team.RED) redGolems++;
                    else blueGolems++;
                }
            }
        }
        if (redGolems != blueGolems) {
            return "There are an unequal amount of golems per team!";
        }

        //both sides must have a win location and flag
        boolean redWin = false, blueWin = false, redFlag = false, blueFlag = false;
        for (int x = 0; x < map.getWidth(); x++) {
            for (int y = 0; y < map.getHeight(); y++) {
                if (map.getTile(new Point(x, y)) instanceof FlagTile) {
                    if (((FlagTile) map.getTile(new Point(x, y))).getTeam() == Team.RED) redFlag = true;
                    else blueFlag = true;
                }
                if (map.getTile(new Point(x, y)) instanceof WinTile) {
                    if (((WinTile) map.getTile(new Point(x, y))).getTeam() == Team.RED) redWin = true;
                    else blueWin = true;
                }
            }
        }
        if (!redWin) return "Team red has no win location!";
        if (!blueWin) return "Team blue has no win location!";
        if (!redFlag) return "Team red has no flag!";
        if (!blueFlag) return "Team blue has no flag!";

        return null;
    }
}
