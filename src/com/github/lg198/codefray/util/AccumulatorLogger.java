package com.github.lg198.codefray.util;

import javafx.beans.value.ChangeListener;

public class AccumulatorLogger {

    public static interface Listener {
        void changed(String contents);
    }

    private String contents = "";
    private int currentLines = 0;

    private Listener listener = (String s) -> {};

    private int maxLines = 100;

    public String getContents() {
        return contents;
    }

    public int getMaxLines() {
        return maxLines;
    }
    public void setMaxLines(int ml) {
        maxLines = ml;
        if (currentLines > maxLines) {
            trim();
        }
    }

    private void trim() {
        while (currentLines > maxLines) {
            contents = contents.substring(contents.indexOf("\n")+1);
            currentLines--;
        }
        change();
    }

    public void log(String s) {
        contents += s + "\n";
        currentLines++;
        trim();

        change();
    }

    public void setListener(Listener l) {
        listener = l;
    }

    private void change() {
        listener.changed(contents);
    }


}
