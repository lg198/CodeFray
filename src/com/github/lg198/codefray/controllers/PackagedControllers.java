package com.github.lg198.codefray.controllers;


import com.github.lg198.codefray.api.golem.GolemController;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class PackagedControllers {

    public static Map<String, GolemController> controllers = new HashMap<>();

    public static void init() {
        Properties props = new Properties();
        try {
            props.load(PackagedControllers.class.getResourceAsStream("/com/github/lg198/codefray/res/data/controllers.properties"));
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        String[] clist = props.getProperty("controllers").split(",");
        for (String cstring : clist) {
            cstring = cstring.trim();
            try {
                Class gcclass = Class.forName(props.getProperty(cstring + ".class"));
                controllers.put(
                        props.getProperty(cstring + ".name"),
                        (GolemController) gcclass.newInstance()
                );
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }
}
