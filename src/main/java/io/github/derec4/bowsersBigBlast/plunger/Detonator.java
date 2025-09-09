package io.github.derec4.bowsersBigBlast.plunger;

import org.bukkit.block.Block;

public class Detonator {
    private DetonatorLocation location;
    private Block block;
    private boolean isDetonator;

    public Detonator(DetonatorLocation location, Block block, boolean isDetonator) {
        this.location = location;
        this.block = block;
        this.isDetonator = isDetonator;
    }

    public DetonatorLocation getLocation() {
        return location;
    }

    public Block getBlock() {
        return block;
    }

    public boolean isDetonator() {
        return isDetonator;
    }

    /**
     * Retrieve a Detonator from a given Location and Block, if valid.
     */
    public static Detonator getDetonator(DetonatorLocation location, Block block) {
        if (block == null || location == null) return null;
        boolean valid = block.getType().name().endsWith("_BUTTON");
        return new Detonator(location, block, valid);
    }
}
