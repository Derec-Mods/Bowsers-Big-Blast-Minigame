package io.github.derec4.bowsersBigBlast;

import io.github.derec4.bowsersBigBlast.command.CommandManager;
import io.github.derec4.bowsersBigBlast.command.TestCommandManager;
import io.github.derec4.bowsersBigBlast.listener.OnMinigameEnd;
import io.github.derec4.bowsersBigBlast.listener.DetonatorListener;
import io.github.derec4.bowsersBigBlast.listener.TNTExplosionListener;
import io.github.derec4.bowsersBigBlast.game.GameState;
import io.github.derec4.bowsersBigBlast.player.GamePlayer;
import io.github.derec4.bowsersBigBlast.command.ConfigReloadCommand;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public final class BowsersBigBlast extends JavaPlugin {
    // Config variables
    public static boolean fixedSpawn = false;
    public static int spawnX = 0;
    public static int spawnY = 64;
    public static int spawnZ = 0;

    /**
     * Setup: Players stand around a large bomb with several colored plungers attached.
     * Turn Order: Players take turns in a set order.
     * Plunger Selection: On their turn, each player chooses one plunger to press.
     * Random Outcome: Only one plunger will detonate the bomb; the others are safe. The outcome is random and unknown to players.
     * Elimination: If a player presses the detonator plunger, the bomb explodes and that player is eliminated from the game.
     * Next Round: The remaining players continue, with one less plunger (the detonator is reset and the number of plungers matches the number of players).
     * Repeat: Steps 3–6 repeat until only one player remains.
     * Winner: The last remaining player wins the minigame.
     */
    @Override
    public void onEnable() {
        // Load config
        saveDefaultConfig();
        reloadConfigValues();

        // Plugin startup logic
        getCommand("bbtest").setExecutor((CommandSender sender, Command command, String label, String[] args) -> {
            return new TestCommandManager(this).onCommand(sender, command, label, args);
        });

        getServer().getPluginManager().registerEvents(new OnMinigameEnd(), this);
        getServer().getPluginManager().registerEvents(new DetonatorListener(), this);
        getServer().getPluginManager().registerEvents(new TNTExplosionListener(), this);

        getCommand("bowsergame").setExecutor(new CommandManager());
        getCommand("bowsergame").setTabCompleter(new CommandManager());
        getCommand("bowserreload").setExecutor(new ConfigReloadCommand(this));
    }

    private void reloadConfigValues() {
        fixedSpawn = getConfig().getBoolean("fixedspawn", false);
        spawnX = getConfig().getInt("spawn_x", 0);
        spawnY = getConfig().getInt("spawn_y", 64);
        spawnZ = getConfig().getInt("spawn_z", 0);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static void initializeGamePlayers() {
        GameState.getInstance().getCurrentPlayers().clear();
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.getGameMode() == GameMode.SURVIVAL || player.getGameMode() == GameMode.ADVENTURE || player.getGameMode() == GameMode.CREATIVE) {
                GameState.getInstance().getCurrentPlayers().add(new GamePlayer(player, player.getUniqueId(),
                        player.getName()));
            }
        }
    }
}
