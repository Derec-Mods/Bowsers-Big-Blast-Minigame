package io.github.derec4.bowsersBigBlast.listener;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import io.github.derec4.bowsersBigBlast.plunger.Detonator;
import io.github.derec4.bowsersBigBlast.plunger.DetonatorLocation;

public class DetonatorListener implements Listener {
    @EventHandler
    public void onPlayerInteract (PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        Block clicked = event.getClickedBlock();

        if (clicked == null) {
            return;
        }

        if (!clicked.getType().name().endsWith("_BUTTON")) {
            return;
        }

        Location bukkitLoc = clicked.getLocation();

        DetonatorLocation loc = new DetonatorLocation(
            bukkitLoc.getWorld().getName(),
            bukkitLoc.getX(),
            bukkitLoc.getY(),
            bukkitLoc.getZ()
        );

        Detonator detonator = Detonator.getDetonator(loc, clicked);

        if (detonator != null) {
            System.out.println("Is detonator: " + detonator.isBomb());
        } else {
            System.out.println("Detonator is null");
        }
    }

}
