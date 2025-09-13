package io.github.derec4.bowsersBigBlast.command;

import io.github.derec4.bowsersBigBlast.BowsersBigBlast;
import io.github.derec4.bowsersBigBlast.event.MinigameWinEvent;
import io.github.derec4.bowsersBigBlast.game.CountdownTimer;
import io.github.derec4.bowsersBigBlast.game.DetonatorManager;
import io.github.derec4.bowsersBigBlast.game.GameState;
import io.github.derec4.bowsersBigBlast.plunger.Detonator;
import io.github.derec4.bowsersBigBlast.plunger.DetonatorLocation;
import io.github.derec4.bowsersBigBlast.util.BlockUtils;
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
    private final Plugin plugin;

    public TestCommandManager(Plugin plugin) {
        this.plugin = plugin;
        // Use singleton DetonatorManager for consistency
        // this.detonatorManager = new DetonatorManager(); // Remove this
    }

    @Override
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
                handleTestCelebration(sender);
                break;
            case "revealdetonator":
                handleRevealDetonator(sender);
                break;
            case "testdetonator":
                handleTestDetonator(sender);
                break;
            case "testspawndetonators":
                handleTestSpawnDetonators(sender, args);
                break;
            case "testcountdowntimer":
                handleTestCountdownTimer(sender, args);
                break;
            case "teststartgame":
                handleTestStartGame(sender);
                break;
            default:
                sender.sendMessage("Unknown test command. Usage: /bbtest [thing u want to debug]");
                break;
        }
        return true;
    }

    private void handleTestCelebration(CommandSender sender) {
        if (sender instanceof Player player) {
            MinigameWinEvent event = new MinigameWinEvent(player);
            Bukkit.getPluginManager().callEvent(event);
            sender.sendMessage("Test: MinigameWinEvent fired for you.");
        } else {
            sender.sendMessage("Only players can test celebration.");
        }
    }

    private void handleRevealDetonator(CommandSender sender) {
        // TODO: Implement revealDetonator logic
        sender.sendMessage("Test: revealDetonator not implemented yet.");
    }

    private void handleTestDetonator(CommandSender sender) {
        if (sender instanceof Player player) {
            Location baseLoc = player.getLocation().add(0, 0, 2); // 2 blocks ahead
            org.bukkit.World world = baseLoc.getWorld();
            if (world == null) {
                sender.sendMessage("World not found.");
                return;
            }
            Material chosenWool = BlockUtils.WOOLS.get((int) (Math.random() * BlockUtils.WOOLS.size()));
            Material chosenButton = BlockUtils.BUTTONS.get((int) (Math.random() * BlockUtils.BUTTONS.size()));
            BlockUtils.placeDetonatorBlock(baseLoc, chosenWool, chosenButton);
            Location buttonLoc = baseLoc.clone().add(0, 1, 0);
            Block buttonBlock = world.getBlockAt(buttonLoc);
            DetonatorLocation detLoc = new DetonatorLocation(
                    buttonBlock.getWorld().getName(),
                    buttonBlock.getX(),
                    buttonBlock.getY(),
                    buttonBlock.getZ()
            );
            // Use DetonatorManager singleton for lookup
            Detonator detonator = DetonatorManager.getInstance().getDetonatorByBlock(buttonBlock);
            sender.sendMessage("Spawned at " + baseLoc.getBlockX() + "," + baseLoc.getBlockY() + "," + baseLoc.getBlockZ());
            sender.sendMessage("Detonator isBomb: " + (detonator != null ? detonator.isBomb() : "null"));
        } else {
            sender.sendMessage("Only players can test detonator.");
        }
    }

    private void handleTestSpawnDetonators(CommandSender sender, String[] args) {
        if (sender instanceof Player player) {
            int count = 5;
            if (args.length > 1) {
                try {
                    count = Math.max(1, Math.min(10, Integer.parseInt(args[1])));
                } catch (NumberFormatException ignored) {}
            }
            io.github.derec4.bowsersBigBlast.game.DetonatorManager.getInstance().spawnDetonators(player, count);
            sender.sendMessage("Spawned " + count + " detonators in front of you.");
        } else {
            sender.sendMessage("Only players can test detonator spawning.");
        }
    }

    private void handleTestCountdownTimer(CommandSender sender, String[] args) {
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
    }

    private void handleTestStartGame(CommandSender sender) {
        GameState.getInstance().setMaxPlayers(1);
        BowsersBigBlast.initializeGamePlayers();
        int actualPlayers = GameState.getInstance().getCurrentPlayers().size();
        GameState.getInstance().setMaxPlayers(actualPlayers);
        GameState.getInstance().setGameRunning(true);
        sender.sendMessage("Test: Bowser's Big Blast game started with " + actualPlayers + " players (minPlayers set to 1).");
        GameState.getInstance().startGame();
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
            "testcountdowntimer",
            "teststartgame"
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
