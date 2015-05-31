package com.github.lg198.codefray.jfx;

public class ResourceManager {

    private static final String RES_BASE = "/com/github/lg198/codefray/res/";

    public static String getIcon(String s) {
        return ResourceManager.class.getResource(RES_BASE + "icon/" + s).toExternalForm();
    }
}
