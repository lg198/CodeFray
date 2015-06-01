package com.github.lg198.codefray.controllers.defaultc;

import com.github.lg198.codefray.api.golem.Golem;
import com.github.lg198.codefray.api.golem.GolemController;

public class DefaultController implements GolemController {
    @Override
    public void onRound(Golem g) {

    }

    @Override
    public String getIdString() {
        return "CodeFray v1 Included Controllers - Default - v1.0";
    }
}
