package com.github.lg198.codefray.jfx;

import com.github.lg198.codefray.api.game.Team;
import com.github.lg198.codefray.api.golem.Golem;
import com.github.lg198.codefray.api.golem.GolemController;
import com.github.lg198.codefray.api.math.Direction;
import com.github.lg198.codefray.game.CFGame;
import com.github.lg198.codefray.game.map.CFMap;
import com.github.lg198.codefray.game.map.gen.CFMapGenerator;
import javafx.application.Application;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.util.HashMap;
import java.util.Map;

public class CodeFrayApplication extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        CFMap testMap = CFMapGenerator.generate(40, 40);
        GolemController dummy = new GolemController() {
            @Override
            public void onRound(Golem g) {
                if (g.getTeam()==Team.RED) {
                    //if (g.detectTile(Direction.SOUTH)== TileType.EMPTY) {
                        g.move(Direction.SOUTH);
                    //}
                }
            }
        };
        Map<Team, GolemController> cmap = new HashMap<Team, GolemController>();
        cmap.put(Team.RED, dummy);
        cmap.put(Team.BLUE, dummy);
        CFGame testGame = new CFGame(testMap, cmap);
        Parent box = testGame.getGui().build();
        Scene sc = new Scene(box, 750, 520);
        stage.setScene(sc);
        stage.show();
        testGame.start();
        testGame.getGui().update();
    }
}
