package io.github.derec4.bowsersBigBlast.util;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.FaceAttachable;
import org.bukkit.block.data.type.Switch;

import java.util.Arrays;
import java.util.List;

public class BlockUtils {
    public static final List<Material> BUTTONS = Arrays.asList(
            Material.STONE_BUTTON,
            Material.OAK_BUTTON,
            Material.BIRCH_BUTTON,
            Material.SPRUCE_BUTTON,
            Material.JUNGLE_BUTTON,
            Material.ACACIA_BUTTON,
            Material.DARK_OAK_BUTTON,
            Material.CRIMSON_BUTTON,
            Material.WARPED_BUTTON
    );

    public static final List<Material> WOOLS = Arrays.asList(
            Material.WHITE_WOOL,
            Material.ORANGE_WOOL,
            Material.MAGENTA_WOOL,
            Material.LIGHT_BLUE_WOOL,
            Material.YELLOW_WOOL,
            Material.LIME_WOOL,
            Material.PINK_WOOL,
            Material.GRAY_WOOL,
            Material.LIGHT_GRAY_WOOL,
            Material.CYAN_WOOL,
            Material.PURPLE_WOOL,
            Material.BLUE_WOOL,
            Material.BROWN_WOOL,
            Material.GREEN_WOOL,
            Material.RED_WOOL,
            Material.BLACK_WOOL
    );

    /**
     * Places a wool block and a button on top at the given location.
     */
    public static boolean placeDetonatorBlock (Location baseLoc, Material wool, Material button) {
        World world = baseLoc.getWorld();
        Block woolBlock = world.getBlockAt(baseLoc);
        woolBlock.setType(wool);

        Location buttonLoc = baseLoc.clone().add(0, 1, 0);
        Block buttonBlock = world.getBlockAt(buttonLoc);
        buttonBlock.setType(button);
        BlockData data = buttonBlock.getBlockData();

        if (data instanceof Switch buttonData) {
            buttonData.setAttachedFace(FaceAttachable.AttachedFace.FLOOR);
            buttonBlock.setBlockData(buttonData);
            return true;
        }
        return false;
    }
}
