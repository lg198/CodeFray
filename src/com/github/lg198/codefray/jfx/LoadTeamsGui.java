package com.github.lg198.codefray.jfx;

import com.github.lg198.codefray.controllers.PackagedControllers;
import com.github.lg198.codefray.game.golem.CFGolemController;
import com.github.lg198.codefray.load.LoadException;
import com.github.lg198.codefray.load.ControllerLoader;
import com.github.lg198.codefray.util.Stylizer;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.File;

public class LoadTeamsGui {

    private ControllerLoader loader;
    private GridPane grid = new GridPane();

    private TextField redField = new TextField(), blueField = new TextField();
    private Button redBrowseButton = new Button(), blueBrowseButton = new Button();
    private boolean redPreloaded = false, bluePreloaded = false;
    private String redPreloadedName = "", bluePreloadedName = "";
    private HBox redBox, blueBox;
    private Button submit = new Button("Start Game");
    private Label warningLabel = new Label();

    private TextField mapField = new TextField();
    private Button mapBrowseButton = new Button();

    public LoadTeamsGui(ControllerLoader l) {
        loader = l;

        loadButtonImages();
    }

    private void loadButtonImages() {
        Image bi = new Image(ResourceManager.getIcon("loader_browse.png"), 16, 16, true, true);
        redBrowseButton.setGraphic(new ImageView(bi));
        blueBrowseButton.setGraphic(new ImageView(bi));
        mapBrowseButton.setGraphic(new ImageView(bi));
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

        redBox = new HBox();
        redBox.setAlignment(Pos.CENTER);
        redBox.setSpacing(4);
        redBox.getChildren().addAll(redField, redBrowseButton);
        HBox.setHgrow(redField, Priority.ALWAYS);
        redField.setPromptText("Path to red team jarfile");

        grid.add(redBox, 1, 0);
        GridPane.setHgrow(redBox, Priority.ALWAYS);

        grid.add(new Label("Blue Team:"), 0, 1);

        blueBox = new HBox();
        blueBox.setAlignment(Pos.CENTER);
        blueBox.setSpacing(4);
        blueBox.getChildren().addAll(blueField, blueBrowseButton);
        HBox.setHgrow(blueField, Priority.ALWAYS);
        blueField.setPromptText("Path to blue team jarfile");

        grid.add(blueBox, 1, 1);
        GridPane.setHgrow(blueBox, Priority.ALWAYS);

        grid.add(new Label("Map File:"), 0, 2);

        mapBrowseButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                FileChooser chooser = new FileChooser();
                chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CodeFray Map File", "*.cfmap"));
                File f = chooser.showOpenDialog(null);
                if (f == null) {
                    return;
                }
                mapField.setText(f.getAbsolutePath());
            }
        });

        HBox mapBox = new HBox();
        mapBox.setAlignment(Pos.CENTER);
        mapBox.setSpacing(4);
        mapBox.getChildren().addAll(mapField, mapBrowseButton);
        HBox.setHgrow(mapField, Priority.ALWAYS);
        mapField.setPromptText("Path to map");
        grid.add(mapBox, 1, 2);
        GridPane.setHgrow(mapBox, Priority.ALWAYS);

        createContextMenu(redBrowseButton, redField);
        createContextMenu(blueBrowseButton, blueField);

        HBox bottom = new HBox();
        bottom.setAlignment(Pos.CENTER_RIGHT);
        bottom.setSpacing(10);
        bottom.getChildren().addAll(warningLabel, submit);
        warningLabel.setTextFill(Color.RED);
        HBox.setHgrow(warningLabel, Priority.ALWAYS);
        grid.add(bottom, 0, 3, 2, 1);
        GridPane.setMargin(bottom, new Insets(10, 0, 0, 0));

        submit.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                CFGolemController red, blue;
                try {
                    if (redPreloaded) {
                        if (!PackagedControllers.controllerExists(redPreloadedName)) {
                            warningLabel.setText("Red Team Error: The specified controller is not included");
                            return;
                        }
                        red = PackagedControllers.getController(redPreloadedName);
                    } else {
                        red = loader.load(new File(redField.getText()));
                        if (red == null) {
                            warningLabel.setText("Could not load red team!");
                            return;
                        }
                    }
                } catch (LoadException e) {
                    warningLabel.setText("Red Team Error: " + e.getMessage());
                    return;
                }

                try {
                    if (bluePreloaded) {
                        if (!PackagedControllers.controllerExists(bluePreloadedName)) {
                            warningLabel.setText("Blue Team Error: The specified controller is not included");
                            return;
                        }
                        blue = PackagedControllers.getController(bluePreloadedName);
                    } else {
                        blue = loader.load(new File(blueField.getText()));
                        if (blue == null) {
                            warningLabel.setText("Could not load blue team!");
                            return;
                        }
                    }
                } catch (LoadException e) {
                    warningLabel.setText("Blue Team Error: " + e.getMessage());
                    return;
                }

                if (mapField.getText().isEmpty() || !new File(mapField.getText()).exists()) {
                    warningLabel.setText("Map Error: Cannot be found!");
                    return;
                }
               CodeFrayApplication.switchToGame(red, blue, mapField.getText());
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
                if (tf == redField && redPreloaded) {
                    redPreloaded = false;
                    unsetPreloaded(tf);
                } else if (tf == blueField && bluePreloaded) {
                    bluePreloaded = false;
                    unsetPreloaded(tf);
                }
                tf.setText(f.getAbsolutePath());
            }
        });

        MenuItem chooseItem = new MenuItem("Load from Included...");
        chooseItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                showChooseControllerDialog(tf);
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

    public void showChooseControllerDialog(final TextField tf) {
        final Stage stage = new Stage();
        stage.setTitle("Choose a Controller");
        StackPane sp = new StackPane();
        final ListView<String> lv = new ListView<>(FXCollections.observableArrayList(PackagedControllers.getControllerNames()));
        lv.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        lv.setCellFactory(new Callback<ListView<String>, ListCell<String>>() {
            @Override
            public ListCell<String> call(ListView<String> stringListView) {
                ListCell<String> s = new ListCell<String>() {
                    @Override
                    protected void updateItem(String str, boolean empty) {
                        super.updateItem(str, empty);
                        setText(str);
                    }
                };
                s.setAlignment(Pos.CENTER);
                return s;
            }
        });
        lv.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                String controller = lv.getSelectionModel().getSelectedItem();
                if (controller == null) {
                    return;
                }
                tf.setText(controller);
                if (tf == redField) {
                    redPreloaded = true;
                    redPreloadedName = controller;
                    setPreloaded(tf);
                } else {
                    bluePreloaded = true;
                    bluePreloadedName = controller;
                    setPreloaded(tf);
                }
                stage.close();
            }
        });
        sp.getChildren().add(lv);
        Scene sc = new Scene(sp, 300, 200);
        stage.setScene(sc);
        stage.show();
    }

    private void setPreloaded(final TextField tf) {
        double width = tf.getWidth();
        HBox box = tf == blueField ? blueBox : redBox;
        if (box.getChildren().contains(tf)) {
            box.getChildren().remove(tf);
        } else {
            box.getChildren().remove(0);
        }
        Label l = new Label(tf.getText());
        l.setMinWidth(width);
        l.setMaxWidth(width);
        l.setPrefWidth(width);
        Stylizer.set(l, "-fx-background-color", "lightblue");
        Stylizer.set(l, "-fx-text-fill", "white");
        Stylizer.set(l, "-fx-background-radius", "3px");
        Stylizer.set(l, "-fx-padding", "5px");
        Stylizer.set(l, "-fx-font-size", "12px");
        Stylizer.set(l, "-fx-font-weight", "bold");
        HBox.setHgrow(l, Priority.ALWAYS);
        box.getChildren().add(0, l);
    }

    private void unsetPreloaded(final TextField tf) {
        HBox box = tf == blueField ? blueBox : redBox;
        box.getChildren().remove(0);
        box.getChildren().add(0, tf);
        tf.setText("");
    }

}
