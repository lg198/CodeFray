package com.github.lg198.codefray.levelbuilder.jfx;

import com.github.lg198.codefray.levelbuilder.LoadedCell;
import com.github.lg198.codefray.levelbuilder.LoadedGolem;
import com.github.lg198.codefray.levelbuilder.LoadedMap;
import com.github.lg198.codefray.levelbuilder.MapStorage;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.FileOutputStream;

public class LevelBuilderApplication {


    public static Stage pstage;

    public void start(Stage stage) {
        pstage = stage;

        Scene sc = new Scene(new MapSettingsGui().build());
        stage.setScene(sc);
        stage.sizeToScene();
        stage.setTitle("Create Map");
        stage.show();
    }

    public static void launchEditor(LoadedMap map) {
        MapGui gui = new MapGui(50, 6, map);
        AnchorPane sp = new AnchorPane();
        Canvas c = gui.build();
        AnchorPane.setLeftAnchor(c, 0.0);
        sp.getChildren().add(c);
        VBox edit = gui.pane.build();
        AnchorPane.setRightAnchor(edit, 0.0);
        sp.getChildren().add(edit);
        gui.canvas.heightProperty().bind(sp.heightProperty());
        gui.canvas.widthProperty().bind(sp.widthProperty().add(edit.widthProperty().negate()));
        Scene sc = new Scene(sp, 640, 480);
        pstage.setScene(sc);
        pstage.setTitle("CodeFray Map Builder");
        pstage.show();
        gui.render();
    }

    public static void launchEditor(String name, String author, int width, int height) {
        LoadedMap map = new LoadedMap(width, height);
        map.name = name;
        map.author = author;
        launchEditor(map);
    }
}
