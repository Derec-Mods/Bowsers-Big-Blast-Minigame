package io.github.derec4.bowsersBigBlast.listener;

import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.NamespacedKey;
import org.bukkit.Bukkit;

public class TNTExplosionListener implements Listener {

    public static final NamespacedKey BOWSER_TNT_KEY = new NamespacedKey(
        Bukkit.getPluginManager().getPlugin("BowsersBigBlast"),
        "bowser_tnt"
    );

    @EventHandler
    public void onTNTExplode(EntityExplodeEvent event) {
        if (!(event.getEntity() instanceof TNTPrimed)) {
            return;
        }

        TNTPrimed tnt = (TNTPrimed) event.getEntity();

        // Check if this TNT has our custom tag
        if (tnt.getPersistentDataContainer().has(BOWSER_TNT_KEY, PersistentDataType.BOOLEAN)) {
            // Prevent block damage from our custom TNT
            event.blockList().clear();
            Bukkit.getLogger().info("Prevented block damage from Bowser's Big Blast TNT");
        }
    }
}
