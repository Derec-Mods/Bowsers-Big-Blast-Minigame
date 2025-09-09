package io.github.derec4.bowsersBigBlast;

import io.github.derec4.bowsersBigBlast.command.TestCommandManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public final class BowsersBigBlast extends JavaPlugin {

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
