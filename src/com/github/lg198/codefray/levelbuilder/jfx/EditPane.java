package com.github.lg198.codefray.levelbuilder.jfx;

import com.github.lg198.codefray.levelbuilder.MapStorage;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import javafx.scene.shape.StrokeType;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class EditPane {

    public MapGui gui;

    public ToggleGroup group = new ToggleGroup();
    public ToggleButton
            empty = new ToggleButton("Empty"),
            wall = new ToggleButton("Wall"),
            redFlag = new ToggleButton("Red Flag"),
            blueFlag = new ToggleButton("Blue Flag"),
            redRunner = new ToggleButton("Red Runner"),
            redDefender = new ToggleButton("Red Defender"),
            redAssault = new ToggleButton("Red Assault"),
            blueRunner = new ToggleButton("Blue Runner"),
            blueDefender = new ToggleButton("Blue Defender"),
            blueAssault = new ToggleButton("Blue Assault"),
            redWin = new ToggleButton("Red Win"),
            blueWin = new ToggleButton("Blue Win");

    public ToggleButton mirrored = new ToggleButton("Mirror");

    public EditPane(MapGui mg) {
        gui = mg;

        empty.setToggleGroup(group);
        wall.setToggleGroup(group);
        redFlag.setToggleGroup(group);
        blueFlag.setToggleGroup(group);
        redRunner.setToggleGroup(group);
        redDefender.setToggleGroup(group);
        redAssault.setToggleGroup(group);
        blueRunner.setToggleGroup(group);
        blueDefender.setToggleGroup(group);
        blueAssault.setToggleGroup(group);
        redWin.setToggleGroup(group);
        blueWin.setToggleGroup(group);

        group.selectToggle(empty);
    }


    public VBox build() {
        VBox box = new VBox();

        VBox info = new VBox();
        Border b = new Border(
                new BorderStroke(
                        Color.BLACK,
                        new BorderStrokeStyle(StrokeType.CENTERED, StrokeLineJoin.MITER, StrokeLineCap.SQUARE, 5, 1, null),
                        null,
                        new BorderWidths(1)
                )
        );
        info.setBorder(b);
        Label title = new Label("Name: " + gui.map.name);
        Label author = new Label("Author: " + gui.map.author);
        Label dimensions = new Label("Size: (" + gui.map.getWidth() + ", " + gui.map.getHeight() + ")");
        info.getChildren().addAll(title, author, dimensions);
        info.setPadding(new Insets(5));
        box.getChildren().add(info);

        VBox toggleBox = new VBox();
        toggleBox.setAlignment(Pos.TOP_LEFT);
        toggleBox.setSpacing(5);
        toggleBox.getChildren().addAll(
                empty, wall, redFlag, blueFlag, redRunner, redDefender, redAssault,
                blueRunner, blueDefender, blueAssault, redWin, blueWin
        );
        box.getChildren().add(toggleBox);

        mirrored.setAlignment(Pos.CENTER);
        mirrored.setOnAction(event1 -> gui.mirrored = !gui.mirrored);
        box.getChildren().add(mirrored);

        Button save = new Button("Save Map");
        save.setOnAction(event -> {
            FileChooser chooser = new FileChooser();
            chooser.setTitle("Save Map File");
            chooser.setInitialFileName(gui.map.name.toLowerCase() + ".cfmap");
            chooser.setSelectedExtensionFilter(new ExtensionFilter("CodeFray Map", ".cfmap"));
            File f = chooser.showSaveDialog(LevelBuilderApplication.pstage);
            if (f == null) {
                return;
            }

            try {
                MapStorage.writeMap(gui.map, new FileOutputStream(f));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        save.setAlignment(Pos.CENTER);
        box.getChildren().add(save);

        box.setAlignment(Pos.TOP_CENTER);
        box.setSpacing(20);
        box.setPadding(new Insets(10));
        box.setMinWidth(150);
        return box;
    }

}
