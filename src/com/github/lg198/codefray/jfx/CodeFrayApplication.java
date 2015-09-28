package com.github.lg198.codefray.jfx;

import com.github.lg198.codefray.api.game.Team;
import com.github.lg198.codefray.controllers.PackagedControllers;
import com.github.lg198.codefray.game.CFGame;
import com.github.lg198.codefray.game.GameStatistics;
import com.github.lg198.codefray.game.golem.CFGolemController;
import com.github.lg198.codefray.game.map.CFMap;
import com.github.lg198.codefray.load.ControllerLoader;
import com.github.lg198.codefray.load.MapLoader;
import com.github.lg198.codefray.net.CodeFrayClient;
import com.github.lg198.codefray.net.CodeFrayServer;
import com.github.lg198.codefray.updater.CodeFrayUpdater;
import com.github.lg198.codefray.util.ErrorAlert;
import com.github.lg198.codefray.view.ViewGame;
import com.github.lg198.codefray.view.jfx.UsernameGui;
import com.github.lg198.codefray.view.jfx.ViewGui;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import javax.swing.text.View;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class CodeFrayApplication extends Application {

    public static boolean ignoreUpdates = false;
    public static volatile boolean shutdown = false;

    public static void main(String[] args) {
        Platform.setImplicitExit(true);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if (!shutdown) {
                CodeFrayClient.shutdown();
                CodeFrayServer.shutdown();
                Platform.exit();
            }
        }));
        try {
            PackagedControllers.init();
            if (args.length > 0) {
                if (args[0].equals("-i")) {
                    ignoreUpdates = true;
                }
            }
            launch(args);
        } catch (Exception e) {
            ErrorAlert.createAlert("Error", "CodeFray has crashed", "CodeFray has crashed due to a " + e.getClass().getSimpleName(), e).showAndWait();
            System.exit(1);
        }
    }

    private static Stage primaryStage;

    @Override
    public void start(Stage stage) {
        primaryStage = stage;

        if (!ignoreUpdates) {
            try {
                CodeFrayUpdater.checkForUpdate();
            } catch (Exception e) {
                ErrorAlert.createAlert("Error", "Failed to update CodeFray",
                                       "An attempt to update CodeFray failed due to an error!", e);
            }
        } else {
            CodeFrayUpdater.VERSION = "Your Mom";
        }

        new StartGui().launch();
    }

    @Override
    public void stop() {
        shutdown = true;
        System.out.println("STOPPING CODEFRAY...");
        CodeFrayClient.shutdown();
        CodeFrayServer.shutdown();
    }

    private static void startGame(Stage stage, CFGolemController red, CFGolemController blue, File mapFile, boolean broadcasted) {
        CFMap testMap = MapLoader.loadMap(mapFile);
        Map<Team, CFGolemController> cmap = new HashMap<>();
        cmap.put(Team.RED, red);
        cmap.put(Team.BLUE, blue);
        CFGame testGame;
        try {
            testGame = new CFGame(testMap, cmap, broadcasted);
        } catch (RuntimeException e) {
            Platform.runLater(() -> new StartGui().launch());
            return;
        }
        Parent box = testGame.getGui().build();
        Scene sc = new Scene(box, 750, 520);
        stage.setScene(sc);
        stage.show();

        primaryStage.setMaximized(true);
        primaryStage.setTitle("CodeFray: " + testGame.getController(Team.RED).name + " vs " + testGame.getController(Team.RED).name);
    }

    public static void switchToGame(CFGolemController red, CFGolemController blue, String mapString, boolean broadcasted) {
        primaryStage.hide();
        startGame(primaryStage, red, blue, new File(mapString), broadcasted);
    }

    public static void switchToResult(GameStatistics stats) {
        GameResultGui gui = new GameResultGui(stats);
        Scene sc = new Scene(gui.build());
        primaryStage.setScene(sc);
        primaryStage.setMaximized(false);
        primaryStage.setTitle("Result");
        primaryStage.sizeToScene();
        primaryStage.centerOnScreen();
        primaryStage.show();
    }

    public static void startLocalGame() {
        ControllerLoader l = new ControllerLoader();
        LoadTeamsGui gui = new LoadTeamsGui(l, false);
        Scene scene = new Scene(gui.build());
        primaryStage.setScene(scene);
        primaryStage.setTitle("Load Game");
        primaryStage.show();
    }

    public static void startBroadcastedGame() {
        ControllerLoader l = new ControllerLoader();
        LoadTeamsGui gui = new LoadTeamsGui(l, true);
        Scene scene = new Scene(gui.build());
        primaryStage.setScene(scene);
        primaryStage.setTitle("Load Game");
        primaryStage.show();
    }

    public static void startViewGame() {
        UsernameGui gui = new UsernameGui();
        Scene scene = new Scene(gui.build());
        primaryStage.setScene(scene);
        primaryStage.setTitle("View Game");
        primaryStage.show();
    }

    public static void startViewGui(ViewGame g) {
        primaryStage.hide();
        Scene scene = new Scene(g.gui.build());
        primaryStage.setScene(scene);
        primaryStage.setTitle("CodeFray Game Viewer");
        primaryStage.setMaximized(true);
        primaryStage.show();
    }


}
