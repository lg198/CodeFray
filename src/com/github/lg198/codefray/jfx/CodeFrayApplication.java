package com.github.lg198.codefray.jfx;

import com.github.lg198.codefray.api.game.Team;
import com.github.lg198.codefray.controllers.PackagedControllers;
import com.github.lg198.codefray.game.CFGame;
import com.github.lg198.codefray.game.GameStatistics;
import com.github.lg198.codefray.game.golem.CFGolemController;
import com.github.lg198.codefray.game.map.CFMap;
import com.github.lg198.codefray.load.ControllerLoader;
import com.github.lg198.codefray.load.MapLoader;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class CodeFrayApplication extends Application {

    public static void main(String[] args) {
        PackagedControllers.init();
        launch(args);
    }

    //TEST: C:\Users\Layne\IdeaProjects\CodeFray\out\artifacts\CodeFrayController_jar\CodeFrayController.jar

    private static Stage primaryStage;

    @Override
    public void start(Stage stage) {
        primaryStage = stage;

        ControllerLoader l = new ControllerLoader();
        LoadTeamsGui gui = new LoadTeamsGui(l);
        Scene sc = new Scene(gui.build());
        stage.setScene(sc);
        stage.show();
        //switchToGame(PackagedControllers.controllers.get("Default"), PackagedControllers.controllers.get("Default"), "C:\\Users\\Layne\\Documents\\code\\CodeFray\\test.cfmap");
    }

    private static void startGame(Stage stage, CFGolemController red, CFGolemController blue, File mapFile) {
        CFMap testMap = MapLoader.loadMap(mapFile);
        Map<Team, CFGolemController> cmap = new HashMap<>();
        cmap.put(Team.RED, red);
        cmap.put(Team.BLUE, blue);
        CFGame testGame = new CFGame(testMap, cmap);
        Parent box = testGame.getGui().build();
        Scene sc = new Scene(box, 750, 520);
        stage.setScene(sc);
        stage.show();
        testGame.start();
        testGame.getGui().update();

        primaryStage.setMaximized(true);
    }

    public static void switchToGame(CFGolemController red, CFGolemController blue, String mapString) {
        primaryStage.hide();
        startGame(primaryStage, red, blue, new File(mapString));
    }

    public static void switchToResult(GameStatistics stats, File logFile) {
        primaryStage.hide();
        GameResultGui gui = new GameResultGui(stats);
        Scene sc = new Scene(gui.build());
        primaryStage.setScene(sc);
        primaryStage.show();
    }
}
