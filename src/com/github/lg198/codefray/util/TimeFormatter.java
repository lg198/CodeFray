package com.github.lg198.codefray.util;

public class TimeFormatter {

    public static String format(long seconds) {
        if (seconds < 60) {
            return pluralSeconds(seconds);
        }
        int minutes = (int) (seconds / 60.0);
        return pluralMinutes(minutes) + " and " + pluralSeconds(seconds % 60);
    }

    public static String pluralSeconds(long i) {
        if (i == 1) {
            return i + " second";
        }
        return i + " seconds";
    }

    public static String pluralMinutes(long i) {
        if (i == 1) {
            return i + " minute";
        }
        return i + " minutes";
    }
}
