package io.github.derec4.bowsersBigBlast.game;

import io.github.derec4.bowsersBigBlast.plunger.Detonator;
import io.github.derec4.bowsersBigBlast.plunger.DetonatorLocation;
import io.github.derec4.bowsersBigBlast.util.BlockUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.util.Vector;
import org.bukkit.Sound;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.NamespacedKey;
import io.github.derec4.bowsersBigBlast.player.GamePlayer;
import java.util.*;

public class DetonatorManager {
    private final Map<DetonatorLocation, Detonator> detonatorMap = new HashMap<>();

    private static final NamespacedKey BOWSER_TNT_KEY = new NamespacedKey(
        Bukkit.getPluginManager().getPlugin("BowsersBigBlast"),
        "bowser_tnt"
    );

    private static DetonatorManager instance;
    public static DetonatorManager getInstance() {
        if (instance == null) {
            instance = new DetonatorManager();
        }
        return instance;
    }

    public void spawnDetonatorAt(Location woolLoc, Material woolType, Material buttonType, World world, boolean isUnlucky) {
        BlockUtils.placeDetonatorBlock(woolLoc, woolType, buttonType);
        Location buttonLoc = woolLoc.clone().add(0, 1, 0);
        Block buttonBlock = world.getBlockAt(buttonLoc);
        DetonatorLocation detLoc = new DetonatorLocation(
            buttonBlock.getWorld().getName(),
            buttonBlock.getX(),
            buttonBlock.getY(),
            buttonBlock.getZ()
        );
        Detonator detonator = new Detonator(detLoc, buttonBlock, isUnlucky);
        detonatorMap.put(detLoc, detonator);

        Bukkit.getLogger().info("Detonator ADDED: " + detLoc.getWorld() + " " + detLoc.getX() + "," + detLoc.getY() +
                "," + detLoc.getZ());
    }

    public Detonator getDetonatorByBlock(Block block) {
        if (block == null) return null;

        Location bukkitLoc = block.getLocation();
        DetonatorLocation loc = new DetonatorLocation(
            bukkitLoc.getWorld().getName(),
            block.getX(),
            block.getY(),
            block.getZ()
        );

        Bukkit.getLogger().info("Detonator LOOKUP: " + loc.getWorld() + " " + loc.getX() + "," + loc.getY() + "," + loc.getZ());
        return detonatorMap.get(loc);
    }

    /**
     * Returns the closest cardinal direction (N, E, S, W) as a normalized Vector based on the player's yaw.
     * generated this using AI because i was too lazy to do the math
     */
    public Vector getCardinalDirection(Location loc) {
        float yaw = loc.getYaw();
        yaw = (yaw % 360 + 360) % 360; // Normalize yaw to [0, 360)
        if ((yaw >= 315 || yaw < 45)) {
            // South
            return new Vector(0, 0, 1);
        } else if (yaw >= 45 && yaw < 135) {
            // West
            return new Vector(-1, 0, 0);
        } else if (yaw >= 135 && yaw < 225) {
            // North
            return new Vector(0, 0, -1);
        } else {
            // East
            return new Vector(1, 0, 0);
        }
    }

    /**
     * Spawns detonators in front of the player, spaced by 1 block, each with a unique wool and oak button.
     * @param player The player to spawn in front of
     * @param playerCount Number of detonators to spawn
     */
    public void spawnDetonators(Player player, int playerCount) {
        World world = player.getWorld();
        Location base = player.getLocation();
        Vector direction = getCardinalDirection(base);
        Vector left = direction.clone().crossProduct(new Vector(0, 1, 0)).normalize();

        List<Material> wools = new ArrayList<>(BlockUtils.WOOLS);
        Collections.shuffle(wools);
        List<Material> usedWools = wools.subList(0, Math.min(playerCount, wools.size()));
        Random rand = new Random();

        Location centerLoc = base.clone().add(direction.clone().multiply(2));
        centerLoc.setY(base.getBlockY());

        int half = playerCount / 2;
        int woolIndex = 0;
        int unluckyIndex = rand.nextInt(playerCount); // Pick one unlucky detonator

        if (playerCount % 2 == 1) {
            // Odd: include center
            for (int offset = -half; offset <= half; offset++) {
                Material woolType = usedWools.get(woolIndex);
                Material buttonType = Material.OAK_BUTTON; // Use only oak button
                Location woolLoc = centerLoc.clone().add(left.clone().multiply(offset * 2));
                boolean isUnlucky = (woolIndex == unluckyIndex);

                spawnDetonatorAt(woolLoc, woolType, buttonType, world, isUnlucky);
                woolIndex++;
            }
        } else {
            // Even: skip center (offset 0)
            for (int offset = -half; offset <= half; offset++) {
                if (offset == 0) continue;
                Material woolType = usedWools.get(woolIndex);
                Material buttonType = Material.OAK_BUTTON; // Use only oak button
                Location woolLoc = centerLoc.clone().add(left.clone().multiply(offset * 2));
                boolean isUnlucky = (woolIndex == unluckyIndex);

                spawnDetonatorAt(woolLoc, woolType, buttonType, world, isUnlucky);
                woolIndex++;
            }
        }
    }

    /**
     * Handles the unlucky choice logic for when a player picks the bomb detonator.
     * This includes the "Unlucky!" title, countdown, TNT spawning, and elimination.
     */
    public void handleUnluckyChoice(Player player, GamePlayer gamePlayer) {
        player.sendTitle("§cUnlucky!", "", 10, 40, 10);

        player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_BREAK, 1.0f, 1.0f);
        player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 200, 200, false, false, false));

        // Countdown: 3, 2, 1 (red titles)
        for (int i = 0; i < 3; i++) {
            int count = 3 - i;
            Bukkit.getScheduler().runTaskLater(
                Bukkit.getPluginManager().getPlugin("BowsersBigBlast"),
                () -> player.sendTitle("§c" + count, "", 0, 20, 0),
                i * 20L
            );
        }

        // After countdown, spawn TNT and eliminate player
        Bukkit.getScheduler().runTaskLater(
            Bukkit.getPluginManager().getPlugin("BowsersBigBlast"),
            () -> {
                spawnTNTRain(player.getLocation());
                player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
                GameManager.getInstance().onPlayerEliminated(gamePlayer);
            },
            60L // 3 seconds
        );
    }

    /**
     * Spawns multiple TNT entities in a spread pattern above the player location
     */
    private void spawnTNTRain(Location playerLocation) {
        Random random = new Random();
        int tntCount = 8 + random.nextInt(5);

        for (int i = 0; i < tntCount; i++) {
            // Create random offset positions around the player
            double offsetX = (random.nextDouble() - 0.5) * 6; // -3 to +3 blocks
            double offsetZ = (random.nextDouble() - 0.5) * 6; // -3 to +3 blocks
            double height = 8 + random.nextDouble() * 4; // 8-12 blocks high

            Location tntLocation = playerLocation.clone().add(offsetX, height, offsetZ);
            TNTPrimed tnt = tntLocation.getWorld().spawn(tntLocation, TNTPrimed.class);

            tnt.setFuseTicks(20 + random.nextInt(61));

            // Tag the TNT so it won't break blocks
            tnt.getPersistentDataContainer().set(BOWSER_TNT_KEY, PersistentDataType.BOOLEAN, true);

            Bukkit.getLogger().info("Spawned tagged TNT at: " + tntLocation);
        }
    }

    /**
     * Randomly selects a detonator for the player and simulates pressing it.
     */
    public void autoSelectDetonatorForPlayer(Player player) {
        List<Detonator> available = new ArrayList<>(detonatorMap.values());

        if (available.isEmpty()) {
            Bukkit.getLogger().warning("No detonators available for auto-select.");
            return;
        }

        Random rand = new Random();
        Detonator chosen = available.get(rand.nextInt(available.size()));
        Bukkit.getLogger().info("Auto-selecting detonator for player: " + player.getName() + " -> " + chosen);

        // Find the GamePlayer for this player
        GamePlayer gamePlayer = GameManager.getInstance().getCurrentPlayers().stream()
            .filter(gp -> gp.getId().equals(player.getUniqueId()))
            .findFirst().orElse(null);

        if (chosen.isBomb()) {
            handleUnluckyChoice(player, gamePlayer);
        } else {
            player.sendTitle("§aSafe", "", 10, 40, 10);
            GameManager.getInstance().onPlayerSafe();
        }
    }

    /**
     * Clears all detonators from the map (for new round).
     */
    public void clearDetonators() {
        detonatorMap.clear();
        Bukkit.getLogger().info("Detonator map cleared for new round.");
    }
}
