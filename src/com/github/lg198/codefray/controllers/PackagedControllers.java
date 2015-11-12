package com.github.lg198.codefray.controllers;


import com.github.lg198.codefray.api.golem.GolemController;
import com.github.lg198.codefray.game.golem.CFGolemController;
import com.github.lg198.codefray.load.ControllerLoader;

import java.io.IOException;
import java.util.*;

public class PackagedControllers {

    private static Map<String, Class<? extends GolemController>> controllers = new HashMap<>();

    public static void init() {
        controllers.put("default", DefaultController.class);
        controllers.put("aggressive offense", AggressiveOffenseController.class);
    }

    public static boolean controllerExists(String s) {
        return controllers.containsKey(s);
    }

    public static List<String> getControllerNames() {
        return new ArrayList<String>(controllers.keySet());
    }

    public static CFGolemController getController(String s) {
        if (!controllers.containsKey(s)) {
            return null;
        }
        Class<? extends GolemController> c = controllers.get(s);
        try {
            GolemController gc = c.newInstance();
            return ControllerLoader.wrapController(gc);
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }
}
