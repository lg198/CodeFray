package com.github.lg198.codefray.util;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TextArea;

import java.io.PrintWriter;
import java.io.StringWriter;

public class ErrorAlert {

    public static Alert createAlert(String title, String header, String content, Throwable t) {
        Alert a = new Alert(AlertType.ERROR);

        a.setTitle(title);
        if (header != null) a.setHeaderText(header);
        if (content != null) a.setContentText(content);

        StringWriter writer = new StringWriter();
        PrintWriter pw = new PrintWriter(writer);

        t.printStackTrace(pw);
        pw.close();

        String econtents = writer.toString();

        TextArea area = new TextArea(econtents);
        Stylizer.set(area, "-fx-text-fill", "red");
        area.setEditable(false);

        a.getDialogPane().setExpandableContent(area);

        return a;

    }
}
