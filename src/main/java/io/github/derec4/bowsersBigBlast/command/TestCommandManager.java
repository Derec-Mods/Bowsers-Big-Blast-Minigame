package io.github.derec4.bowsersBigBlast.command;

import io.github.derec4.bowsersBigBlast.event.MinigameWinEvent;
import io.github.derec4.bowsersBigBlast.plunger.Detonator;
import io.github.derec4.bowsersBigBlast.plunger.DetonatorLocation;
import io.github.derec4.bowsersBigBlast.util.BlockUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
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
            case "testdetonator":
                if (sender instanceof Player player) {
                    org.bukkit.Location baseLoc = player.getLocation().add(0, 0, 2); // 2 blocks ahead
                    org.bukkit.World world = baseLoc.getWorld();
                    if (world == null) {
                        sender.sendMessage("World not found.");
                        break;
                    }

                    Material chosenWool = BlockUtils.WOOLS.get((int)(Math.random() * BlockUtils.WOOLS.size()));
                    Material chosenButton = BlockUtils.BUTTONS.get((int)(Math.random() * BlockUtils.BUTTONS.size()));
                    Block woolBlock = world.getBlockAt(baseLoc);
                    Block buttonBlock = world.getBlockAt(baseLoc.clone().add(0, 1, 0));
                    woolBlock.setType(chosenWool);
                    buttonBlock.setType(chosenButton);

                    // Register as Detonator
                    DetonatorLocation detLoc = new DetonatorLocation(
                        buttonBlock.getWorld().getName(),
                        buttonBlock.getX(),
                        buttonBlock.getY(),
                        buttonBlock.getZ()
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
}
