package io.github.derec4.bowsersBigBlast.game;

import io.github.derec4.bowsersBigBlast.player.GamePlayer;
import io.github.derec4.bowsersBigBlast.event.MinigameWinEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

/**
 * // Game Structure:
 * // 1. Wait for enough players to join (minPlayers).
 * // 2. Start the game when ready.
 * // 3. Repeat rounds until only one player remains:
 * //    a. Spawn detonators (one is randomly unlucky).
 * //    b. For each player in turn:
 * //       i. Player selects a detonator.
 * //       ii. Check if detonator is unlucky.
 * //           - If unlucky: eliminate player, trigger explosion.
 * //           - If safe: continue.
 * //    c. Remove eliminated players from the round.
 * //    d. Prepare for next round (reset detonators, update player list).
 * // 4. Declare the last remaining player as the winner.
 * // 5. Reset game state for next game.
 */
public class GameManager {
    private static GameManager instance;
    private final List<GamePlayer> currentPlayers = new ArrayList<>();
    private boolean isGameRunning = false;
    private int maxPlayers = 6;
    private Location centerLocation;
    private int currentDetonatorCount = 0; // Detonators for current round
    private int round = 1;
    private boolean roundActive = false;
    private int currentPlayerIndex = 0;
    private boolean playerEliminatedThisRound = false;
    private CountdownTimer currentCountdownTimer = null;
    private GamePlayer currentTurnPlayer = null;

    private GameManager() {
    }

    public static GameManager getInstance() {
        if (instance == null) {
            instance = new GameManager();
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

    public List<GamePlayer> getCurrentPlayers() {
        return currentPlayers;
    }

    public void reset() {
        isGameRunning = false;
        currentPlayers.clear();
        maxPlayers = 6;
    }

    public Location getCenterLocation() {
        return centerLocation;
    }

    public void setCenterLocation(Location loc) {
        this.centerLocation = loc;
    }

    public int getCurrentDetonatorCount() {
        return currentDetonatorCount;
    }

    public int getRound() {
        return round;
    }

    public boolean isRoundActive() {
        return roundActive;
    }

    public void setRoundActive(boolean active) {
        this.roundActive = active;
    }

    public void startGame() {
        // Removed minPlayers check
        setGameRunning(true);
        round = 1;
        currentDetonatorCount = currentPlayers.size();
        Bukkit.getLogger().info("Game started with " + currentDetonatorCount + " detonators.");
        System.out.println(currentPlayers);
        startRound();
    }

    public void startRound() {
        startRound(0);
    }

    public void startRound(int startingPlayerIndex) {
        if (centerLocation == null) {
            Bukkit.getLogger().warning("Center location not set. Cannot start round.");
            return;
        }

        DetonatorManager.getInstance().clearDetonators();
        roundActive = true;
        playerEliminatedThisRound = false;

        if (currentDetonatorCount == 0) {
            currentDetonatorCount = currentPlayers.size();
        }


        DetonatorManager.getInstance().spawnDetonators(
                Objects.requireNonNull(Bukkit.getPlayer(currentPlayers.get(0).getId())), currentDetonatorCount
        );

        Bukkit.getLogger().info("Starting round " + round + " with " + currentDetonatorCount + " detonators.");
        currentPlayerIndex = startingPlayerIndex % currentPlayers.size();
        nextPlayerTurn();
    }

    public void nextPlayerTurn() {
        if (currentPlayerIndex >= currentPlayers.size()) {
            endRound();
            return;
        }

        GamePlayer player = currentPlayers.get(currentPlayerIndex);
        currentTurnPlayer = player;
        Player bukkitPlayer = Bukkit.getPlayer(player.getId());

        if (bukkitPlayer != null) {
            // Show turn announcement to all players
            showTurnAnnouncementToAllPlayers(bukkitPlayer);
            Bukkit.getLogger().info("Player turn: " + bukkitPlayer.getName());
            startCountdownForAllPlayers(10, player);
        }
    }

    /**
     * Shows turn announcement to all players in the game
     */
    private void showTurnAnnouncementToAllPlayers(Player currentPlayer) {
        for (GamePlayer gp : currentPlayers) {
            Player p = Bukkit.getPlayer(gp.getId());
            if (p != null) {
                if (p.equals(currentPlayer)) {
                    p.sendTitle("§aYour Turn!", "Choose a detonator", 10, 40, 10);
                } else {
                    p.sendTitle("§e" + currentPlayer.getName() + "'s Turn", "Waiting for their choice...", 10, 40, 10);
                }
            }
        }
    }

    /**
     * Starts a countdown timer using the existing CountdownTimer class
     */
    private void startCountdownForAllPlayers(int seconds, GamePlayer turnPlayer) {
        if (currentCountdownTimer != null) {
            currentCountdownTimer.stopAndHide();
        }


        List<Player> bukkitPlayers = new ArrayList<>();
        for (GamePlayer gp : currentPlayers) {
            Player p = Bukkit.getPlayer(gp.getId());
            if (p != null) {
                bukkitPlayers.add(p);
            }
        }

        // Create and start the countdown timer
        Plugin plugin = Bukkit.getPluginManager().getPlugin("BowsersBigBlast");
        currentCountdownTimer = new CountdownTimer(plugin, bukkitPlayers);
        currentCountdownTimer.start(seconds);

        // Schedule timeout handler
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if (currentCountdownTimer != null) {
                currentCountdownTimer.stopAndHide();
                currentCountdownTimer = null;
                Player bukkitPlayer = Bukkit.getPlayer(turnPlayer.getId());

                if (bukkitPlayer != null) {
                    for (GamePlayer gp : currentPlayers) {
                        Player p = Bukkit.getPlayer(gp.getId());

                        if (p != null) {
                            if (gp.equals(turnPlayer)) {
                                p.sendTitle("§cOut of Time!", "A random choice will be made", 10, 40, 10);
                            } else {
                                p.sendTitle("§c" + turnPlayer.getName() + " Out of Time!", "Random choice being made...", 10, 40, 10);
                            }
                        }
                    }

                    // if time is up randomly select an unlucky option
                    Bukkit.getLogger().info("Player " + bukkitPlayer.getName() + " ran out of time!");
                    DetonatorManager.getInstance().autoSelectDetonatorForPlayer(bukkitPlayer);
                }
            }
        }, (seconds + 1) * 20L); // Add 1 second buffer to ensure countdown finishes
    }

    public void cancelCountdown() {
        if (currentCountdownTimer != null) {
            currentCountdownTimer.stopAndHide();
            currentCountdownTimer = null;
        }
    }

    public void onPlayerEliminated(GamePlayer eliminatedPlayer) {
        int eliminatedIndex = currentPlayers.indexOf(eliminatedPlayer);
        currentPlayers.remove(eliminatedPlayer);
        playerEliminatedThisRound = true;
        Bukkit.getLogger().info("Player eliminated: " + eliminatedPlayer.getName());

        if (currentPlayers.size() == 1) {
            endGame();
        } else {
            // The next player is the one after the eliminated player
            endRound(eliminatedIndex + 1);
        }
    }

    public void onPlayerSafe() {
        if (currentPlayers.size() <= 1) {
            endGame();
            return;
        }

        if (currentTurnPlayer != null) {
            Player bukkitPlayer = Bukkit.getPlayer(currentTurnPlayer.getId());
            if (bukkitPlayer != null) {
                bukkitPlayer.sendTitle("§aSafe", "", 10, 40, 10);
            }
        }

        // Randomly select the next player (excluding the current player)
        int oldIndex = currentPlayerIndex;
        int nextIndex;

        do {
            nextIndex = new Random().nextInt(currentPlayers.size());
        } while (nextIndex == oldIndex && currentPlayers.size() > 1);

        currentPlayerIndex = nextIndex;
        nextPlayerTurn();
    }

    public void endRound() {
        endRound(0);
    }

    public void endRound(int nextPlayerIndex) {
        roundActive = false;
        round++;

        if (playerEliminatedThisRound) {
            currentDetonatorCount = currentPlayers.size();
        }

        // else, keep same detonator count
        Bukkit.getLogger().info("Round ended. Next round: " + round);
        startRound(nextPlayerIndex);
    }

    public void endGame() {
        roundActive = false;
        isGameRunning = false;
        Bukkit.getLogger().info("Game over! Winner: " + currentPlayers.get(0).getName());
        Player winner = Bukkit.getPlayer(currentPlayers.get(0).getId());

        if (winner != null) {
            Bukkit.getPluginManager().callEvent(new MinigameWinEvent(winner));
        }

        reset();
    }
}
