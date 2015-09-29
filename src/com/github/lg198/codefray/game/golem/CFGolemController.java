package com.github.lg198.codefray.game.golem;

import com.github.lg198.codefray.api.golem.Golem;
import com.github.lg198.codefray.api.golem.GolemController;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.logging.*;

public class CFGolemController {

    private GolemController controller;

    public String id, name, version, devId;

    public Logger logger;
    public File logFile;

    public CFGolemController(GolemController c, String i, String n, String v, String di) {
        controller = c;
        id = i;
        name = n;
        version = v;
        devId = di;

        logger = Logger.getLogger(id);

        logger.addHandler(new Handler() {

            private String format(LogRecord l) {
                return "[" + DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(LocalDateTime.now()) + "] " + l.getLevel().getName().toUpperCase() + ": " + l.getMessage();
            }

            @Override
            public void publish(LogRecord record) {
                try {
                    Files.write(logFile.toPath(), Arrays.asList(format(record)), StandardOpenOption.APPEND, StandardOpenOption.CREATE);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void flush() {

            }

            @Override
            public void close() throws SecurityException {

            }
        });
    }


    public void onRound(Golem g) {
        controller.onRound(g);
    }
}
