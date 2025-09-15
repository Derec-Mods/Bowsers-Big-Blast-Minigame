package io.github.derec4.bowsersBigBlast.game;

import io.github.derec4.bowsersBigBlast.plunger.Detonator;
import io.github.derec4.bowsersBigBlast.plunger.DetonatorLocation;
import io.github.derec4.bowsersBigBlast.util.BlockUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.util.Vector;
import java.util.*;

public class DetonatorManager {
    private final Map<DetonatorLocation, Detonator> detonatorMap = new HashMap<>();

    private static DetonatorManager instance;
    public static DetonatorManager getInstance() {
        if (instance == null) {
            instance = new DetonatorManager();
        }
        return instance;
    }

    public Detonator getDetonatorByBlock(Block block) {
        if (block == null) return null;

        Location bukkitLoc = block.getLocation();
        DetonatorLocation loc = new DetonatorLocation(
            bukkitLoc.getWorld().getName(),
            bukkitLoc.getX(),
            bukkitLoc.getY(),
            bukkitLoc.getZ()
        );

        return detonatorMap.get(loc);
    }

    /**
     * Spawns a single detonator at the given location with button
     */
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
     * Spawns detonators in front of the player, spaced by 1 block, each with a unique wool and random button.
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
                Material buttonType = BlockUtils.BUTTONS.get(rand.nextInt(BlockUtils.BUTTONS.size()));
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
                Material buttonType = BlockUtils.BUTTONS.get(rand.nextInt(BlockUtils.BUTTONS.size()));
                Location woolLoc = centerLoc.clone().add(left.clone().multiply(offset * 2));
                boolean isUnlucky = (woolIndex == unluckyIndex);

                spawnDetonatorAt(woolLoc, woolType, buttonType, world, isUnlucky);
                woolIndex++;
            }
        }
    }
}
