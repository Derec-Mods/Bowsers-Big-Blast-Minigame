package io.github.derec4.bowsersBigBlast.command;

import io.github.derec4.bowsersBigBlast.BowsersBigBlast;
import io.github.derec4.bowsersBigBlast.event.MinigameWinEvent;
import io.github.derec4.bowsersBigBlast.plunger.Detonator;
import io.github.derec4.bowsersBigBlast.plunger.DetonatorLocation;
import io.github.derec4.bowsersBigBlast.util.BlockUtils;
import io.github.derec4.bowsersBigBlast.game.DetonatorManager;
import io.github.derec4.bowsersBigBlast.game.CountdownTimer;
import io.github.derec4.bowsersBigBlast.game.GameState;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.Attachable;
import org.bukkit.block.data.BlockData;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class TestCommandManager implements TabExecutor {
    private final DetonatorManager detonatorManager;
    private final Plugin plugin;

    public TestCommandManager(Plugin plugin) {
        this.plugin = plugin;
        this.detonatorManager = new DetonatorManager();
    }

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
                    BlockUtils.placeDetonatorBlock(baseLoc, chosenWool, chosenButton);

                    Location buttonLoc = baseLoc.clone().add(0, 1, 0);
                    Block buttonBlock = world.getBlockAt(buttonLoc);

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
            case "testspawndetonators":
                if (sender instanceof Player player) {
                    int count = 5;
                    if (args.length > 1) {
                        try {
                            count = Math.max(1, Math.min(10, Integer.parseInt(args[1])));
                        } catch (NumberFormatException ignored) {}
                    }
                    detonatorManager.spawnDetonators(player, count);
                    sender.sendMessage("Spawned " + count + " detonators in front of you.");
                } else {
                    sender.sendMessage("Only players can test detonator spawning.");
                }
                break;
            case "testcountdowntimer":
                if (sender instanceof Player player) {
                    int seconds = 10;
                    if (args.length > 1) {
                        try {
                            seconds = Math.max(1, Math.min(60, Integer.parseInt(args[1])));
                        } catch (NumberFormatException ignored) {}
                    }
                    CountdownTimer timer = new CountdownTimer(plugin, Collections.singleton(player));
                    timer.start(seconds);
                    sender.sendMessage("CountdownTimer started for " + seconds + " seconds.");
                } else {
                    sender.sendMessage("Only players can test countdown timer.");
                }
                break;
            case "teststartgame":
                // Allow starting the game with any number of players
                GameState.getInstance().setMaxPlayers(1);
                BowsersBigBlast.initializeGamePlayers();
                int actualPlayers = GameState.getInstance().getCurrentPlayers().size();
                GameState.getInstance().setMaxPlayers(actualPlayers);
                GameState.getInstance().setGameRunning(true);
                sender.sendMessage("Test: Bowser's Big Blast game started with " + actualPlayers + " players (minPlayers set to 1).");
                GameState.getInstance().startGame();
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

        List<String> subcommands = Arrays.asList(
            "testCelebration",
            "revealDetonator",
            "testDetonator",
            "testspawndetonators",
            "testcountdowntimer"
        );

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
        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("testspawndetonators")) {
                List<String> nums = new ArrayList<>();
                for (int i = 1; i <= 10; i++) nums.add(String.valueOf(i));
                return nums;
            }
            if (args[0].equalsIgnoreCase("testcountdowntimer")) {
                List<String> nums = new ArrayList<>();
                for (int i = 1; i <= 60; i++) nums.add(String.valueOf(i));
                return nums;
            }
        }
        return Collections.emptyList();
    }
}
