package io.github.derec4.bowsersBigBlast.player;

import org.bukkit.entity.Player;

import java.util.UUID;

public class GamePlayer {
    private Player player;
    private boolean eliminated;
    private UUID id;
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public GamePlayer (Player player, UUID id, String name) {
        this.player = player;
        this.eliminated = false;
        this.id = id;
        this.name = name;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public boolean isEliminated() {
        return eliminated;
    }

    public void setEliminated (boolean e) {
        this.eliminated = e;
    }

    public UUID getId() {
        return id;
    }

    @Override
    public String toString() {
        return this.name;
    }

}

