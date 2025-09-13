package io.github.derec4.bowsersBigBlast.game;

import io.github.derec4.bowsersBigBlast.player.GamePlayer;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Bukkit;

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

    public void startGame() {
        if (currentPlayers.size() < minPlayers) {
            Bukkit.getLogger().warning("Not enough players to start the game.");
            return;
        }
        setGameRunning(true);
        int round = 1;
        int detonators = currentPlayers.size();
        while (currentPlayers.size() > 1 && isGameRunning) {
            Bukkit.getLogger().info("--- Round " + round + " ---");
            Bukkit.getLogger().info("Detonators: " + detonators);
            boolean eliminated = false;
            for (int i = 0; i < currentPlayers.size(); i++) {
                Bukkit.getLogger().info("Player " + (i + 1) + "'s turn.");
                // TODO: Add detonator selection and elimination logic here
                // For now, simulate no elimination
            }
            if (eliminated) {
                detonators = currentPlayers.size();
            } else {
                // No one eliminated, same number of detonators next round
            }
            round++;
        }
        Bukkit.getLogger().info("Game over! Winner: " + (currentPlayers.size() == 1 ? "Player 1" : "None"));
        setGameRunning(false);
    }
}
