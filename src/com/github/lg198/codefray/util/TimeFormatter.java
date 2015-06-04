package com.github.lg198.codefray.util;

public class TimeFormatter {

    public static String format(long seconds) {
        if (seconds < 60) {
            return seconds + " seconds";
        }
        int minutes = (int) (seconds / 60.0);
        return minutes + " minutes and " + (seconds % 60) + " seconds";
    }
}
