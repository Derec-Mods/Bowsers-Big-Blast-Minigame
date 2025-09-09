package io.github.derec4.bowsersBigBlast.command;

import io.github.derec4.bowsersBigBlast.event.MinigameWinEvent;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TestCommandManager {
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.isOp()) {
            sender.sendMessage("You must be OP to use this command.");
            return true;
        }

        if (!command.getName().equalsIgnoreCase("bbtest")) {
            return false;
        }

        if (args.length == 0) {
            sender.sendMessage("Usage: /bbtest <testCelebration|revealDetonator>");
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "testcelebration":
                if (sender instanceof Player player) {
                    MinigameWinEvent event = new MinigameWinEvent(player);
                    Bukkit.getPluginManager().callEvent(event);
                    sender.sendMessage("Test: MinigameWinEvent fired for you.");
                } else {
                    sender.sendMessage("Only players can test celebration.");
                }
                break;
            case "revealdetonator":
                // TODO: Implement revealDetonator logic
                sender.sendMessage("Test: revealDetonator not implemented yet.");
                break;
            default:
                sender.sendMessage("Unknown test command. Usage: /bbtest [thing u want to debug]");
                break;
        }
        return true;
    }
}

