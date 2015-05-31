package com.github.lg198.codefray.jfx;

import com.github.lg198.codefray.api.game.Team;
import com.github.lg198.codefray.api.golem.Golem;
import com.github.lg198.codefray.api.golem.GolemController;
import com.github.lg198.codefray.api.math.Direction;
import com.github.lg198.codefray.game.CFGame;
import com.github.lg198.codefray.game.GameStatistics;
import com.github.lg198.codefray.game.map.CFMap;
import com.github.lg198.codefray.game.map.gen.CFMapGenerator;
import com.github.lg198.codefray.load.Loader;
import javafx.application.Application;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class CodeFrayApplication extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    //TEST: C:\Users\Layne\IdeaProjects\CodeFray\out\artifacts\CodeFrayController_jar\CodeFrayController.jar

    private static Stage primaryStage;

    @Override
    public void start(Stage stage) {
        primaryStage = stage;

        Loader l = new Loader();
        LoadTeamsGui gui = new LoadTeamsGui(l);
        Scene sc = new Scene(gui.build());
        stage.setScene(sc);
        stage.show();
    }

    private static void startGame(Stage stage, GolemController red, GolemController blue) {
        CFMap testMap = CFMapGenerator.generate(40, 40);
        Map<Team, GolemController> cmap = new HashMap<Team, GolemController>();
        cmap.put(Team.RED, red);
        cmap.put(Team.BLUE, blue);
        CFGame testGame = new CFGame(testMap, cmap);
        Parent box = testGame.getGui().build();
        Scene sc = new Scene(box, 750, 520);
        stage.setScene(sc);
        stage.show();
        testGame.start();
        testGame.getGui().update();
    }

    public static void switchToGame(GolemController red, GolemController blue) {
        primaryStage.hide();
        startGame(primaryStage, red, blue);
    }

    public static void switchToResult(GameStatistics stats, File logFile) {
        primaryStage.hide();
        GameResultGui gui = new GameResultGui(stats);
        Scene sc = new Scene(gui.build());
        primaryStage.setScene(sc);
        primaryStage.show();
    }
}
