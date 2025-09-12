package io.github.derec4.bowsersBigBlast.player;

import org.bukkit.entity.Player;

public class GamePlayer {
    private Player player;
    private boolean eliminated;

    public GamePlayer (Player player) {
        this.player = player;
        this.eliminated = false;
    }

    public void setEliminated (boolean e) {
        this.eliminated = e;
    }
}

