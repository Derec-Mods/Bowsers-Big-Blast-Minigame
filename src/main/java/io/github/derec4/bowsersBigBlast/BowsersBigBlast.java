package io.github.derec4.bowsersBigBlast;

import io.github.derec4.bowsersBigBlast.command.TestCommandManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public final class BowsersBigBlast extends JavaPlugin {

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

        // Plugin startup logic
        getCommand("bbtest").setExecutor((CommandSender sender, Command command, String label, String[] args) -> {
            return new TestCommandManager().onCommand(sender, command, label, args);
        });
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
