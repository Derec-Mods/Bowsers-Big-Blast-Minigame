package io.github.derec4.bowsersBigBlast.player;

import org.bukkit.entity.Player;

import java.util.UUID;

public class GamePlayer {
    private Player player;
    private boolean eliminated;
    private UUID id;

    public GamePlayer (Player player, UUID id) {
        this.player = player;
        this.eliminated = false;
        this.id = id;
    }

    public void setEliminated (boolean e) {
        this.eliminated = e;
    }

}

