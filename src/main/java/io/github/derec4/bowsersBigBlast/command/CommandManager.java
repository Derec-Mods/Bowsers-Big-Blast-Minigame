package io.github.derec4.bowsersBigBlast.command;

import io.github.derec4.bowsersBigBlast.BowsersBigBlast;
import io.github.derec4.bowsersBigBlast.game.GameManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CommandManager implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("bowsergame")) {
            if (args.length == 1 && args[0].equalsIgnoreCase("stop")) {
                GameManager.getInstance().reset();
                sender.sendMessage("Bowser's Big Blast game stopped.");
                return true;
            } else if (args.length == 1) {
                try {
                    int numPlayers = Integer.parseInt(args[0]);
                    if (numPlayers > GameManager.getInstance().getMaxPlayers()) {
                        sender.sendMessage("Player count must be less than or equal to " + GameManager.getInstance().getMaxPlayers() + ".");
                        return true;
                    }

                    if (GameManager.getInstance().isGameRunning()) {
                        sender.sendMessage("A game is already running! Use /bowsergame stop to end the current game first.");
                        return true;
                    }

                    GameManager.getInstance().setMaxPlayers(numPlayers);

                    // Set center location from sender if possible
                    if (sender instanceof Player player) {
                        GameManager.getInstance().setCenterLocation(player.getLocation());
                    }

                    BowsersBigBlast.initializeGamePlayers();
                    sender.sendMessage("Bowser's Big Blast game started with " + numPlayers + " players.");
                    GameManager.getInstance().setGameRunning(true);
                    GameManager.getInstance().startGame();
                    return true;
                } catch (NumberFormatException e) {
                    sender.sendMessage("Invalid number of players.");
                    return true;
                }
            } else {
                sender.sendMessage("Usage: /bowsergame [number] or /bowsergame stop");
                return true;
            }
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        if (args.length == 1) {
            completions.addAll(Arrays.asList("[number]", "stop"));
        }
        return completions;
    }
}
