package io.github.derec4.bowsersBigBlast.listener;

import io.github.derec4.bowsersBigBlast.game.DetonatorManager;
import io.github.derec4.bowsersBigBlast.game.GameManager;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import io.github.derec4.bowsersBigBlast.plunger.Detonator;

import io.github.derec4.bowsersBigBlast.player.GamePlayer;
import java.util.UUID;

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

        Detonator detonator = DetonatorManager.getInstance().getDetonatorByBlock(clicked);

        if (detonator == null) {
            return;
        }

        Bukkit.getLogger().info("DEBUG: Detonator lookup result: " + (detonator != null ? detonator.toString() : "null"));
        Bukkit.getLogger().info("DEBUG: Clicked block: " + (clicked != null ? clicked.getType() + " at " + clicked.getLocation() : "null"));

        // reset countdown meaning they interacted
        GameManager.getInstance().cancelCountdown();

        // Find the GamePlayer for the interacting Bukkit player
        final GamePlayer gamePlayer;
        UUID playerUuid = event.getPlayer().getUniqueId();
        GamePlayer foundPlayer = null;

        for (GamePlayer gp : GameManager.getInstance().getCurrentPlayers()) {
            if (gp.getId().equals(playerUuid)) {
                foundPlayer = gp;
                break;
            }
        }

        gamePlayer = foundPlayer;

        if (gamePlayer != null) {
            Bukkit.getLogger().info("Is detonator: " + detonator.isBomb());
            if (detonator.isBomb()) {
                DetonatorManager.getInstance().handleUnluckyChoice(event.getPlayer(), gamePlayer);
            } else {
                event.getPlayer().sendTitle("§aSafe", "", 10, 40, 10);
                GameManager.getInstance().onPlayerSafe();
            }
        } else {
            Bukkit.getLogger().warning("Detonator or GamePlayer is null");
        }
    }
}
