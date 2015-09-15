package com.github.lg198.codefray.game.map;

import com.github.lg198.codefray.game.golem.CFGolem;

public interface GolemHabitat {

    public boolean onGolemEnter(CFGolem g);
    public boolean canGolemEnter(CFGolem g);
}
