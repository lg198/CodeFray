package com.github.lg198.codefray.jfx;

import com.github.lg198.codefray.util.Stylizer;
import com.sun.corba.se.spi.ior.Writeable;
import com.sun.xml.internal.bind.v2.runtime.output.SAXOutput;
import javafx.beans.binding.Bindings;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.*;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.awt.*;

public class StartGui {

    public static final Image LOCAL_IMG = new Image(ResourceManager.getIcon("personal.png"));
    public static final Image EYE_IMG = new Image(ResourceManager.getIcon("eye.png"));
    public static final Image UPLOAD_IMG = new Image(ResourceManager.getIcon("uploads.png"));

    private HBox localBox, eyeBox, uploadBox;
    private Stage stage;


    public void launch() {
        stage = new Stage();
        stage.initStyle(StageStyle.UNDECORATED);
        Scene sc = new Scene(build());

        stage.setScene(sc);
        stage.setTitle("Launch CodeFray");
        stage.show();
    }

    public VBox build() {
        VBox box = new VBox();
        box.setPadding(new Insets(25));
        box.setSpacing(10);

        localBox = buildSelectionBox(LOCAL_IMG, "Run Local Game", "Run a CodeFray game on your current computer.");
        eyeBox = buildSelectionBox(EYE_IMG, "View Remote Game", "View a CodeFray game that is currently being broadcasted live.");
        uploadBox = buildSelectionBox(UPLOAD_IMG, "Host Remote Game", "Host a CodeFray game so that others can view it live.");

        EventHandler<MouseEvent> handler = (MouseEvent e) -> {
            if (e.getSource() == localBox) {
                stage.close();
                CodeFrayApplication.startLocalGame();
            } else if (e.getSource() == eyeBox) {
                System.out.println("eye");
            } else {
                stage.close();
                CodeFrayApplication.startBroadcastedGame();
            }
        };

        localBox.setOnMouseClicked(handler);
        eyeBox.setOnMouseClicked(handler);
        uploadBox.setOnMouseClicked(handler);

        Label title = new Label("Welcome to CodeFray");
        Stylizer.set(title, "-fx-font-size", "30px");
        Stylizer.set(title, "-fx-font-weight", "bold");
        title.setAlignment(Pos.CENTER);
        title.setPadding(new Insets(0, 0, 5, 0));
        HBox titleBox = new HBox(title);
        titleBox.setAlignment(Pos.CENTER_LEFT);
        box.getChildren().add(titleBox);

        box.getChildren().add(localBox);
        box.getChildren().add(eyeBox);
        box.getChildren().add(uploadBox);

        HBox exitBox = new HBox();
        Label exitLabel = new Label("Quit");
        Stylizer.set(exitLabel, "-fx-font-size", "16px");
        Stylizer.set(exitLabel, "-fx-font-weight", "bold");
        exitLabel.setCursor(Cursor.HAND);
        exitLabel.setPadding(new Insets(12));
        exitBox.setAlignment(Pos.CENTER_RIGHT);
        HBox.setHgrow(exitBox, Priority.ALWAYS);
        exitLabel.hoverProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                Stylizer.set(exitLabel, "-fx-background-color", "derive(lightblue, 50%)");
            } else {
                Stylizer.remove(exitLabel, "-fx-background-color");
            }
        });
        exitLabel.pressedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                Stylizer.set(exitLabel, "-fx-background-color", "derive(lightblue, 35%)");
            } else {
                if (exitLabel.hoverProperty().get()) {
                    Stylizer.set(exitLabel, "-fx-background-color", "derive(lightblue, 50%)");
                } else {
                    Stylizer.remove(exitLabel, "-fx-background-color");
                }
            }
        });
        exitLabel.setOnMouseClicked(event -> stage.close());
        exitBox.getChildren().add(exitLabel);

        box.getChildren().add(exitBox);

        return box;
    }

    private HBox buildSelectionBox(Image i, String title, String details) {
        final HBox localBox = new HBox();
        localBox.setPadding(new Insets(10));
        localBox.setSpacing(10);
        localBox.setAlignment(Pos.TOP_LEFT);
        localBox.setCursor(Cursor.HAND);
        //Stylizer.set(localBox, "-fx-border-color", "lightblue");
        //Stylizer.set(localBox, "-fx-border-size", "2px");

        ImageView localView = new ImageView(i);
        localView.setSmooth(true);
        localView.setFitHeight(80);
        localView.setFitWidth(80);
        localBox.getChildren().add(localView);

        VBox detailsBox = new VBox();
        detailsBox.setAlignment(Pos.TOP_LEFT);
        detailsBox.setSpacing(2);

        Label localTitle = new Label(title);
        Stylizer.set(localTitle, "-fx-font-size", "20px");
        Stylizer.set(localTitle, "-fx-font-weight", "bold");
        detailsBox.getChildren().add(localTitle);

        Label detailsLabel = new Label(details);
        Stylizer.set(detailsLabel, "-fx-font-size", "14px");
        detailsBox.getChildren().add(detailsLabel);

        localBox.getChildren().add(detailsBox);

        localBox.setOnMouseEntered((MouseEvent e) -> {
            Stylizer.set(localBox, "-fx-background-color", "derive(lightblue, 50%)");
        });
        localBox.setOnMouseExited((MouseEvent e) -> {
            Stylizer.remove(localBox, "-fx-background-color");
        });
        localBox.setOnMousePressed((MouseEvent e) -> {
            Stylizer.set(localBox, "-fx-background-color", "derive(lightblue, 35%)");
        });
        localBox.setOnMouseReleased((MouseEvent e) -> {
            Stylizer.set(localBox, "-fx-background-color", "derive(lightblue, 50%)");
        });

        return localBox;
    }
}
