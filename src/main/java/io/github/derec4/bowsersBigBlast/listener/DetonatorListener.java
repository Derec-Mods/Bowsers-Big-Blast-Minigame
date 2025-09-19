package io.github.derec4.bowsersBigBlast.listener;

import io.github.derec4.bowsersBigBlast.game.DetonatorManager;
import io.github.derec4.bowsersBigBlast.game.GameState;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
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

import io.github.derec4.bowsersBigBlast.player.GamePlayer;
import java.util.UUID;

public class DetonatorListener implements Listener {
    @EventHandler
    public void onPlayerInteract (PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        Block clicked = event.getClickedBlock();
        Bukkit.getLogger().info("DEBUG: Clicked block: " + (clicked != null ? clicked.getType() + " at " + clicked.getLocation() : "null"));

        if (clicked == null) {
            return;
        }

        if (!clicked.getType().name().endsWith("_BUTTON")) {
            Bukkit.getLogger().info("DEBUG: Clicked block is not a button");
            return;
        }

        Detonator detonator = DetonatorManager.getInstance().getDetonatorByBlock(clicked);
        Bukkit.getLogger().info("DEBUG: Detonator lookup result: " + (detonator != null ? detonator.toString() : "null"));

        if (detonator == null) {
            return;
        }

        // reset countdown meaning they interacted
        GameState.getInstance().cancelCountdown();

        // Find the GamePlayer for the interacting Bukkit player
        final GamePlayer gamePlayer;
        UUID playerUuid = event.getPlayer().getUniqueId();
        GamePlayer foundPlayer = null;

        for (GamePlayer gp : GameState.getInstance().getCurrentPlayers()) {
            if (gp.getId().equals(playerUuid)) {
                foundPlayer = gp;
                break;
            }
        }

        gamePlayer = foundPlayer;

        if (gamePlayer != null) {
            Bukkit.getLogger().info("Is detonator: " + detonator.isBomb());
            if (detonator.isBomb()) {
                event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.BLOCK_ANVIL_BREAK, 1.0f, 1.0f);
                event.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 60, 100, false, false, false));

                // Countdown: 3, 2, 1 (red titles)
                for (int i = 0; i < 3; i++) {
                    int count = 3 - i;
                    Bukkit.getScheduler().runTaskLater(
                        Bukkit.getPluginManager().getPlugin("BowsersBigBlast"),
                        () -> event.getPlayer().sendTitle("§c" + count, "", 0, 20, 0),
                        i * 20L
                    );
                }

                Bukkit.getScheduler().runTaskLater(
                    Bukkit.getPluginManager().getPlugin("BowsersBigBlast"),
                    () -> {
                        Location tntLoc = event.getPlayer().getLocation().clone().add(0, 10, 0);
                        TNTPrimed tnt = tntLoc.getWorld().spawn(tntLoc, TNTPrimed.class);
                        tnt.setFuseTicks(40);
                        event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
                        GameState.getInstance().onPlayerEliminated(gamePlayer);
                    },
                    60L // 3 seconds
                );
            } else {
                GameState.getInstance().onPlayerSafe();
            }
        } else {
            Bukkit.getLogger().warning("Detonator or GamePlayer is null");
        }
    }

}
