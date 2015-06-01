package com.github.lg198.codefray.jfx;

import com.github.lg198.codefray.api.golem.GolemController;
import com.github.lg198.codefray.load.LoadException;
import com.github.lg198.codefray.load.Loader;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;

import java.io.File;

public class LoadTeamsGui {

    private Loader loader;
    private GridPane grid = new GridPane();

    private TextField redField = new TextField(), blueField = new TextField();
    private Button redBrowseButton = new Button(), blueBrowseButton = new Button();
    private Button submit = new Button("Start Game");
    private Label warningLabel = new Label();

    public LoadTeamsGui(Loader l) {
        loader = l;

        loadButtonImages();
    }

    private void loadButtonImages() {
        Image bi = new Image(ResourceManager.getIcon("loader_browse.png"), 16, 16, true, true);
        redBrowseButton.setGraphic(new ImageView(bi));
        blueBrowseButton.setGraphic(new ImageView(bi));
    }

    public BorderPane build() {
        BorderPane bp = new BorderPane();
        bp.setCenter(grid);
        bp.setPadding(new Insets(20));
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(15);
        grid.setMinWidth(500);

        grid.add(new Label("Red Team:"), 0, 0);

        HBox redBox = new HBox();
        redBox.setAlignment(Pos.CENTER);
        redBox.setSpacing(4);
        redBox.getChildren().addAll(redField, redBrowseButton);
        HBox.setHgrow(redField, Priority.ALWAYS);
        redField.setPromptText("Path to red team jarfile");

        grid.add(redBox, 1, 0);
        GridPane.setHgrow(redBox, Priority.ALWAYS);

        grid.add(new Label("Blue Team:"), 0, 1);

        HBox blueBox = new HBox();
        blueBox.setAlignment(Pos.CENTER);
        blueBox.setSpacing(4);
        blueBox.getChildren().addAll(blueField, blueBrowseButton);
        HBox.setHgrow(blueField, Priority.ALWAYS);
        blueField.setPromptText("Path to blue team jarfile");

        grid.add(blueBox, 1, 1);
        GridPane.setHgrow(blueBox, Priority.ALWAYS);

        createContextMenu(redBrowseButton, redField);
        createContextMenu(blueBrowseButton, blueField);

        HBox bottom = new HBox();
        bottom.setAlignment(Pos.CENTER_RIGHT);
        bottom.setSpacing(10);
        bottom.getChildren().addAll(warningLabel, submit);
        warningLabel.setTextFill(Color.RED);
        HBox.setHgrow(warningLabel, Priority.ALWAYS);
        grid.add(bottom, 0, 2, 2, 1);
        GridPane.setMargin(bottom, new Insets(10, 0, 0, 0));

        submit.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                GolemController red, blue;
                try {
                    red = loader.load(new File(redField.getText()));
                    if (red == null) {
                        warningLabel.setText("Could not load red team!");
                        return;
                    }
                } catch (LoadException e) {
                    warningLabel.setText("Red Team Error: " + e.getMessage());
                    return;
                }

                try {
                    blue = loader.load(new File(blueField.getText()));
                    if (blue == null) {
                        warningLabel.setText("Could not load blue team!");
                        return;
                    }
                } catch (LoadException e) {
                    warningLabel.setText("Blue Team Error: " + e.getMessage());
                    return;
                }

                CodeFrayApplication.switchToGame(red, blue);
            }
        });

        return bp;
    }

    public void createContextMenu(final Button b, final TextField tf) {
        final ContextMenu menu = new ContextMenu();

        MenuItem fileItem = new MenuItem("Load from File...");
        fileItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                FileChooser chooser = new FileChooser();
                chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JAR", "*.jar"));
                File f = chooser.showOpenDialog(null);
                if (f == null) {
                    return;
                }
                tf.setText(f.getAbsolutePath());
            }
        });

        MenuItem chooseItem = new MenuItem("Load from Included...");
        chooseItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                //TODO: LOAD FROM INCLUDED
            }
        });

        menu.getItems().addAll(fileItem, chooseItem);

        b.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                menu.show(b, Side.RIGHT, 2, 2);
            }
        });
    }

}
