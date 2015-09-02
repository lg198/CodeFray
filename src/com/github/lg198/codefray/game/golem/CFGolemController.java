package com.github.lg198.codefray.game.golem;

import com.github.lg198.codefray.api.golem.Golem;
import com.github.lg198.codefray.api.golem.GolemController;

public class CFGolemController {

    private GolemController controller;

    public String id, name, version, devId;

    public CFGolemController(GolemController c, String i, String n, String v, String di) {
        controller = c;
        id = i;
        name = n;
        version = v;
        devId = di;
    }


    public void onRound(Golem g) {
        controller.onRound(g);
    }
}
