package com.github.lg198.codefray.game;

import com.github.lg198.codefray.api.game.Team;
import com.github.lg198.codefray.api.math.Direction;
import com.github.lg198.codefray.api.math.Point;
import com.github.lg198.codefray.game.golem.CFGolem;
import com.github.lg198.codefray.game.map.CFMap;

import java.io.*;
import java.util.Date;

public class GameLog {

    private File logFolder;
    private File gameFile;

    private PrintWriter log;

    private CFGame game;

    public GameLog(CFGame g, File folder) {
        game = g;
        logFolder = folder;
    }

    public File getLogFile() {
        return gameFile;
    }

    private String quote(String s) {
        return '"' + s + '"';
    }

    private String loc(Point p) {
        return "[x: " + p.getX() + ", y: " + p.getY() + "]";
    }

    private String info(Object... in) {
        if (in.length % 2 != 0) {
            return "WHOOPSWHOAHWOAHWHATTHEHELLMANWHOHOHOHAHAHHFDFJFJFDJFJSDJFSDJFDJFDLKJFCRAPMANWTF?!";
        }
        String info = "[";
        for (int i = 0; i < in.length; i += 2) {
            info += in[i] + ": " + in[i + 1] + ", ";
        }
        info = info.substring(0, info.length() - 2) + "]";
        return info;
    }

    public void gameStart(CFMap map) {
        Date d = new Date();
        String name = "cfgl_v1_" + d.getHours() + "-" + d.getMinutes() + "-" + d.getSeconds() + ".txt";
        gameFile = new File(logFolder, name);
        try {
            log = new PrintWriter(new FileWriter(gameFile));
            logGameStart(map);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void logGameStart(CFMap map) {
        log.println("=== CodeFray Version 1 [Capture the Flag] Official Game Log ===\n");
        log.println("[Red Team Controller: " + quote(game.getController(Team.RED).getIdString()) + "]");
        log.println("[Blue Team Controller: " + quote(game.getController(Team.BLUE).getIdString()) + "]");
        log.println();
        log.println("> Game started.");
        log.println("> Using map [seed: " + map.getSeed() + "]");
    }

    public void logRoundStart() {
        log.println("> Round " + game.getRound());
    }

    public void logGolemStart(CFGolem g) {
        log.println("\t> Golem " + info("id", g.getId(), "team", g.getTeam(), "location", g.getLocation().toString(), "health", g.getHealth()));
    }

    public void logGolemMove(CFGolem g, Direction d, boolean success) {
        if (success) {
            log.println("\t\t> Moves " + info("direction", d.name()) + " to " + loc(g.getLocation()));
        } else {
            log.println("\t\t> Tried to move " + info("direction", d.name()) + " but failed to do so");
        }
    }

    public void logGolemShoot(CFGolem g1, boolean success) {
        if (success) {
            log.println("\t\t> Shoots " + info("id", g1.getId()) + " at " + loc(g1.getLocation()) +
                    " resulting in " + info("health", g1.getHealth()));
        } else {
            log.println("\t\t> Tried to shoot " + info("id", g1.getId()) + " at " + loc(g1.getLocation()) +
                    " but failed to do so");
        }
    }

}
