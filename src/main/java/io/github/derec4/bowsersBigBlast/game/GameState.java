package io.github.derec4.bowsersBigBlast.game;

import io.github.derec4.bowsersBigBlast.player.GamePlayer;
import java.util.ArrayList;
import java.util.List;

public class GameState {
    private static GameState instance;
    private boolean isGameRunning = false;
    private int maxPlayers = 6;
    private int minPlayers = 4;
    private List<GamePlayer> currentPlayers = new ArrayList<>();

    private GameState() {}

    public static GameState getInstance() {
        if (instance == null) {
            instance = new GameState();
        }
        return instance;
    }

    public boolean isGameRunning() {
        return isGameRunning;
    }

    public void setGameRunning(boolean running) {
        this.isGameRunning = running;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public void setMaxPlayers(int maxPlayers) {
        this.maxPlayers = maxPlayers;
    }

    public int getMinPlayers() {
        return minPlayers;
    }

    public List<GamePlayer> getCurrentPlayers() {
        return currentPlayers;
    }

    public void reset() {
        isGameRunning = false;
        currentPlayers.clear();
        maxPlayers = 6;
    }
}

