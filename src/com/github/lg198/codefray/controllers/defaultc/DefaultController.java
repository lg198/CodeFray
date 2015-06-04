package com.github.lg198.codefray.controllers.defaultc;

import com.github.lg198.codefray.api.golem.ControllerDef;
import com.github.lg198.codefray.api.golem.Golem;
import com.github.lg198.codefray.api.golem.GolemController;

@ControllerDef(
        id = "com.github.lg198.Default",
        name = "CodeFray v1 Included Controller - Default",
        version = "1.0",
        devId = "dfc61082ebcc29a8eee96284e2a26e42")
public class DefaultController implements GolemController {

    @Override
    public void onRound(Golem g) {

    }

}
