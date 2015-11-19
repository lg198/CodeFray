package com.github.lg198.codefray.levelbuilder.jfx;

import com.github.lg198.codefray.levelbuilder.LoadedMap;
import com.github.lg198.codefray.levelbuilder.MapStorage;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class MapSettingsGui {

    public TextField mapWidth = new TextField(), mapHeight = new TextField(), mapName = new TextField(), mapAuthor = new TextField();

    public Button submit = new Button("Create Map");
    public Button open = new Button("Open Map");

    public GridPane build() {
        GridPane gp = new GridPane();
        gp.setAlignment(Pos.CENTER);
        gp.setHgap(5);
        gp.setVgap(10);
        gp.setPadding(new Insets(10));

        gp.add(new Label("Name:"), 0, 0);
        gp.add(mapName, 1, 0);
        GridPane.setHgrow(mapName, Priority.ALWAYS);

        gp.add(new Label("Author:"), 0, 1);
        gp.add(mapAuthor, 1, 1);
        GridPane.setHgrow(mapAuthor, Priority.ALWAYS);

        gp.add(new Label("Dimensions:"), 0, 2);
        HBox dimbox = new HBox();
        dimbox.setSpacing(3);
        dimbox.getChildren().addAll(mapWidth, new Label(", "), mapHeight);
        HBox.setHgrow(mapWidth, Priority.ALWAYS);
        HBox.setHgrow(mapHeight, Priority.ALWAYS);
        gp.add(dimbox, 1, 2);
        GridPane.setHgrow(dimbox, Priority.ALWAYS);

        mapWidth.setPromptText("Width");
        mapHeight.setPromptText("Height");

        gp.add(submit, 1, 3);
        GridPane.setHalignment(submit, HPos.RIGHT);
        GridPane.setMargin(submit, new Insets(5, 0, 0, 0));

        submit.setOnAction(event -> {
            LevelBuilderApplication.launchEditor(
                    mapName.getText(),
                    mapAuthor.getText(),
                    Integer.parseInt(mapWidth.getText()),
                    Integer.parseInt(mapHeight.getText()));
        });

        gp.add(open, 0, 3);
        GridPane.setHalignment(open, HPos.RIGHT);
        GridPane.setMargin(open, new Insets(5, 0, 0, 0));

        open.setOnAction(event -> {
            FileChooser fc = new FileChooser();
            fc.setTitle("Choose Map");
            fc.setSelectedExtensionFilter(new ExtensionFilter("CodeFray Map", ".cfmap"));
            File f = fc.showOpenDialog(LevelBuilderApplication.pstage);
            try {
                LoadedMap map = MapStorage.loadMap(new FileInputStream(f));
                LevelBuilderApplication.launchEditor(map);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        return gp;
    }
}
