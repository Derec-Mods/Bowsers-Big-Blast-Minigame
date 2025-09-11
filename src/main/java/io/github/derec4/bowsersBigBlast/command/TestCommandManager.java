package io.github.derec4.bowsersBigBlast.command;

import io.github.derec4.bowsersBigBlast.event.MinigameWinEvent;
import io.github.derec4.bowsersBigBlast.plunger.Detonator;
import io.github.derec4.bowsersBigBlast.plunger.DetonatorLocation;
import io.github.derec4.bowsersBigBlast.util.BlockUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.Attachable;
import org.bukkit.block.data.BlockData;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class TestCommandManager implements TabExecutor {
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.isOp()) {
            sender.sendMessage("You must be OP to use this command.");
            return true;
        }

        if (!command.getName().equalsIgnoreCase("bbtest")) {
            return false;
        }

        if (args.length == 0) {
            sender.sendMessage("Usage: /bbtest");
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
            case "testdetonator":
                if (sender instanceof Player player) {
                    org.bukkit.Location baseLoc = player.getLocation().add(0, 0, 2); // 2 blocks ahead
                    org.bukkit.World world = baseLoc.getWorld();
                    if (world == null) {
                        sender.sendMessage("World not found.");
                        break;
                    }

                    Material chosenWool = BlockUtils.WOOLS.get((int) (Math.random() * BlockUtils.WOOLS.size()));
                    Material chosenButton = BlockUtils.BUTTONS.get((int) (Math.random() * BlockUtils.BUTTONS.size()));
                    Boolean placed = BlockUtils.placeDetonatorBlock(baseLoc, chosenWool, chosenButton);

                    // Register as Detonator
                    DetonatorLocation detLoc = new DetonatorLocation(
                            chosenButton.getWorld().getName(),
                            chosenButton.getX(),
                            chosenButton.getY(),
                            chosenButton.getZ()
                    );
                    Detonator detonator = Detonator.getDetonator(detLoc, buttonBlock);
                    sender.sendMessage("Spawned at " + baseLoc.getBlockX() + "," + baseLoc.getBlockY() + "," + baseLoc.getBlockZ());
                    sender.sendMessage("Detonator isBomb: " + detonator.isBomb());
                } else {
                    sender.sendMessage("Only players can test detonator.");
                }
                break;
            default:
                sender.sendMessage("Unknown test command. Usage: /bbtest [thing u want to debug]");
                break;
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (!command.getName().equalsIgnoreCase("bbtest")) {
            return Collections.emptyList();
        }

        List<String> subcommands = Arrays.asList("testCelebration", "revealDetonator", "testDetonator");

        if (args.length == 1) {
            String partial = args[0].toLowerCase();
            List<String> matches = new ArrayList<>();
            for (String sub : subcommands) {
                if (sub.toLowerCase().startsWith(partial)) {
                    matches.add(sub);
                }
            }
            return matches;
        }
        return Collections.emptyList();
    }
}
