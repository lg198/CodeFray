package com.github.lg198.codefray.jfx;

import com.github.lg198.codefray.api.game.Team;
import com.github.lg198.codefray.game.GameEndReason;
import com.github.lg198.codefray.game.GameStatistics;
import com.github.lg198.codefray.util.TimeFormatter;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class GameResultGui {

    private final GameStatistics stats;

    public GameResultGui(GameStatistics gs) {
        stats = gs;
    }

    public VBox build() {
        VBox root = new VBox();
        root.setAlignment(Pos.CENTER);
        root.setSpacing(10);
        root.setPadding(new Insets(15));

        Label title = new Label("Game Result");
        title.setStyle("-fx-font-size: 30px");
        title.setUnderline(true);
        root.getChildren().add(title);
        VBox.setMargin(title, new Insets(0, 0, 5, 0));

        root.getChildren().addAll(
                createWinnerStatBox(stats.reason),
                createStatBox("Rounds:", "" + stats.rounds),
                createStatBox("Length:", TimeFormatter.format(stats.timeInSeconds)),
                createStatBox("Red Golems Left:", "" + stats.redLeft),
                createStatBox("Blue Golems Left:", "" + stats.blueLeft),
                createStatBox("Red Health:", stats.redHealthPercent, Team.RED),
                createStatBox("Blue Health:", stats.blueHealthPercent, Team.BLUE)
        );

        return root;
    }

    private HBox createWinnerStatBox(GameEndReason reason) {
        HBox box = new HBox();
        box.setSpacing(6);
        box.setAlignment(Pos.CENTER);

        if (reason instanceof GameEndReason.Win) {
            Label key = new Label("Winner:");
            Label value = new Label(((GameEndReason.Win)reason).winner.name());
            key.setStyle("-fx-font-size: 20px");
            value.setStyle(key.getStyle());
            box.getChildren().addAll(key, value);
            return box;
        } else if (reason instanceof GameEndReason.Infraction) {
            Label key = new Label(((GameEndReason.Infraction)reason).guilty.name() + " cheated and lost");
            key.setStyle("-fx-font-size: 20px");
            box.getChildren().addAll(key);
            return box;
        } else {
            Label key = new Label("Winner:");
            Label value = new Label("None");
            key.setStyle("-fx-font-size: 20px");
            value.setStyle(key.getStyle());
            box.getChildren().addAll(key, value);
            return box;
        }
    }

    private HBox createStatBox(String name, String value) {
        HBox box = new HBox();
        box.setSpacing(6);
        box.setAlignment(Pos.CENTER);
        box.getChildren().addAll(new Label(name), new Label(value));

        return box;
    }

    private HBox createStatBox(String name, double perc, Team team) {
        HBox box = new HBox();
        box.setAlignment(Pos.CENTER);
        box.setSpacing(6);

        ProgressBar pb = new ProgressBar(perc);
        if (team == Team.RED) {
            pb.setStyle("-fx-accent: red");
        } else {
            pb.setStyle("-fx-accent: blue");
        }

        box.getChildren().addAll(new Label(name), pb);

        return box;
    }
}
