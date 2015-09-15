package com.github.lg198.codefray.util;

import javafx.scene.Node;
import javafx.scene.text.Text;

import java.util.Arrays;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Stylizer {

    public static void set(Node n, String key, String value) {
        if (n.getStyle().isEmpty()) {
            n.setStyle(key + ": " + value + ";");
            return;
        }
        if (n.getStyle().endsWith(";")) {
            n.setStyle(n.getStyle().substring(0, n.getStyle().length()-1));
        }
        String[] pairs = n.getStyle().split(";");
        boolean found = false;
        for (int i = 0; i < pairs.length; i++) {
            String[] split = pairs[i].split(":\\s*");
            if (split[0].equals(key)) {
                pairs[i] = split[0] + ": " + value;
                found = true;
                break;
            }
        }
        n.setStyle(String.join("; ", pairs));
        if (!found) {
            n.setStyle(n.getStyle() + "; " + key + ": " + value);
        }
    }

    public static void remove(Node n, String key) {
        if (n.getStyle().isEmpty()) return;

        if (n.getStyle().endsWith(";")) {
            n.setStyle(n.getStyle().substring(0, n.getStyle().length()-1));
        }

        n.setStyle(Arrays.stream(n.getStyle().split(";")).filter((String s) -> {
            String[] split = s.split(":\\s+");
            if (split[0].trim().equals(key)) {
                return false;
            }
            return true;
        }).collect(Collectors.joining(";")));
    }

    public static Text text(String text, String... style) {
        if (style.length % 2 != 0) {
            throw new IllegalArgumentException("Style must be even!");
        }

        Text t = new Text(text);
        for (int i = 0; i < style.length; i+=2) {
            set(t, style[i], style[i+1]);
        }

        return t;
    }


}
