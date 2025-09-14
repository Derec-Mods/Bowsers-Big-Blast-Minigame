package io.github.derec4.bowsersBigBlast.listener;

import io.github.derec4.bowsersBigBlast.game.DetonatorManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import io.github.derec4.bowsersBigBlast.plunger.Detonator;
import io.github.derec4.bowsersBigBlast.plunger.DetonatorLocation;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

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

        Detonator detonator = DetonatorManager.getInstance().getDetonatorByBlock(clicked);

        // Find the GamePlayer for the interacting Bukkit player
        io.github.derec4.bowsersBigBlast.player.GamePlayer gamePlayer = null;
        java.util.UUID playerUuid = event.getPlayer().getUniqueId();
        for (io.github.derec4.bowsersBigBlast.player.GamePlayer gp : io.github.derec4.bowsersBigBlast.game.GameState.getInstance().getCurrentPlayers()) {
            if (gp.getUuid().equals(playerUuid)) {
                gamePlayer = gp;
                break;
            }
        }

        if (detonator != null && gamePlayer != null) {
            Bukkit.getLogger().info("Is detonator: " + detonator.isBomb());
            if (detonator.isBomb()) {
                event.getPlayer().playSound(event.getPlayer().getLocation(), org.bukkit.Sound.BLOCK_ANVIL_BREAK, 1.0f, 1.0f);
                event.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 40, 100, false, false, false));
                Location tntLoc = event.getPlayer().getLocation().clone().add(0, 10, 0);
                TNTPrimed tnt = tntLoc.getWorld().spawn(tntLoc, org.bukkit.entity.TNTPrimed.class);
                tnt.setFuseTicks(40);
                event.getPlayer().playSound(event.getPlayer().getLocation(), org.bukkit.Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
                // Notify GameState of elimination
                io.github.derec4.bowsersBigBlast.game.GameState.getInstance().onPlayerEliminated(gamePlayer);
            } else {
                // Notify GameState of safe turn
                io.github.derec4.bowsersBigBlast.game.GameState.getInstance().onPlayerSafe();
            }
        } else {
            Bukkit.getLogger().warning("Detonator or GamePlayer is null");
        }
    }

}
