package io.github.derec4.bowsersBigBlast.game;

import io.github.derec4.bowsersBigBlast.plunger.Detonator;
import io.github.derec4.bowsersBigBlast.plunger.DetonatorLocation;
import io.github.derec4.bowsersBigBlast.util.BlockUtils;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Button;
import org.bukkit.util.Vector;
import java.util.*;

public class DetonatorManager {
    private final List<Detonator> detonators = new ArrayList<>();

    public List<Detonator> getDetonators() {
        return Collections.unmodifiableList(detonators);
    }

    /**
     * Spawns detonators in front of the player, spaced by 1 block, each with a unique wool and random button.
     * @param player The player to spawn in front of
     * @param playerCount Number of detonators to spawn
     */
    public void spawnDetonators(Player player, int playerCount) {
        World world = player.getWorld();
        Location base = player.getLocation();
        Vector direction = base.getDirection().normalize();
        List<Material> wools = new ArrayList<>(BlockUtils.WOOLS);
        Collections.shuffle(wools);
        List<Material> usedWools = wools.subList(0, Math.min(playerCount, wools.size()));
        Random rand = new Random();

        for (int i = 0; i < playerCount; i++) {
            Material woolType = usedWools.get(i);
            Material buttonType = BlockUtils.BUTTONS.get(rand.nextInt(BlockUtils.BUTTONS.size()));

            // Calculate location: 1 block ahead per detonator, at player's Y
            Location woolLoc = base.clone().add(direction.clone().multiply(i + 2)).getBlock().getLocation();
            woolLoc.setY(base.getBlockY());
            Block woolBlock = world.getBlockAt(woolLoc);
            woolBlock.setType(woolType);

            // Button on top
            Location buttonLoc = woolLoc.clone().add(0, 1, 0);
            Block buttonBlock = world.getBlockAt(buttonLoc);
            buttonBlock.setType(buttonType);
            BlockData data = buttonBlock.getBlockData();
            if (data instanceof Button) {
                ((Button) data).setFace(org.bukkit.block.BlockFace.FLOOR);
                buttonBlock.setBlockData(data);
            }

            DetonatorLocation detLoc = new DetonatorLocation(woolLoc);
            Detonator detonator = new Detonator(detLoc, buttonBlock, true);
            detonators.add(detonator);
        }
    }
}

