package io.github.derec4.bowsersBigBlast.command;

import io.github.derec4.bowsersBigBlast.BowsersBigBlast;
import io.github.derec4.bowsersBigBlast.game.GameState;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.Arrays;
import java.util.List;

public class CommandManager implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("bowsergame")) {
            if (args.length == 1 && args[0].equalsIgnoreCase("stop")) {
                GameState.getInstance().reset();
                sender.sendMessage("Bowser's Big Blast game stopped.");
                return true;
            } else if (args.length == 1) {
                try {
                    int numPlayers = Integer.parseInt(args[0]);
                    if (numPlayers < GameState.getInstance().getMinPlayers() || numPlayers > GameState.getInstance().getMaxPlayers()) {
                        sender.sendMessage("Player count must be between 4 and 6.");
                        return true;
                    }
                    GameState.getInstance().setMaxPlayers(numPlayers);
                    GameState.getInstance().setGameRunning(true);
                    BowsersBigBlast.initializeGamePlayers();
                    sender.sendMessage("Bowser's Big Blast game started with " + numPlayers + " players.");
                    GameState.getInstance().startGame();
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
        if (command.getName().equalsIgnoreCase("bowsergame") && args.length == 1) {
            return Arrays.asList("4", "5", "6", "stop");
        }
        return List.of();
    }
}
