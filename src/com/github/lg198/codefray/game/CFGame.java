
package com.github.lg198.codefray.game;

import com.github.lg198.codefray.api.game.Direction;
import com.github.lg198.codefray.api.game.Game;
import com.github.lg198.codefray.api.game.Team;
import com.github.lg198.codefray.api.math.Point;


public class CFGame implements Game {
    
    private long round = 1;

    @Override
    public boolean isHoldingFlag(Team t) {
        
    }

    @Override
    public Point getFlagLocation(Team t) {
        
    }

    @Override
    public Direction getFlagDirection(Team t) {
        
    }

    @Override
    public long getRound() {
        return round;
    }
    
    
}
