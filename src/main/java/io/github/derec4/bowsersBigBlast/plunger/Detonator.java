package io.github.derec4.bowsersBigBlast.plunger;

import org.bukkit.block.Block;

public class Detonator {
    private DetonatorLocation location;
    private Block block;
    private boolean isBomb;

    public Detonator(DetonatorLocation location, Block block, boolean isBomb) {
        this.location = location;
        this.block = block;
        this.isBomb = isBomb;
    }

    public DetonatorLocation getLocation() {
        return location;
    }

    public Block getBlock() {
        return block;
    }

    public boolean isBomb() {
        return isBomb;
    }
}
