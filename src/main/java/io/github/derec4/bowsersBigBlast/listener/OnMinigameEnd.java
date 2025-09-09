package io.github.derec4.bowsersBigBlast.listener;

import io.github.derec4.bowsersBigBlast.event.MinigameWinEvent;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.Random;

public class OnMinigameEnd implements Listener {

    private static final FireworkEffect.Type[] TYPES = {
            FireworkEffect.Type.BALL, FireworkEffect.Type.BALL_LARGE, FireworkEffect.Type.CREEPER,
            FireworkEffect.Type.STAR, FireworkEffect.Type.BURST
    };

    private static final Color[] COLORS = {
            Color.WHITE, Color.SILVER, Color.GRAY, Color.BLACK, Color.RED, Color.MAROON,
            Color.YELLOW, Color.OLIVE, Color.LIME, Color.GREEN, Color.AQUA, Color.TEAL,
            Color.BLUE, Color.NAVY, Color.FUCHSIA, Color.PURPLE, Color.ORANGE
    };

    /**
     * Plays a celebration effect for a winning player, based on the PartyGames plugin logic.
     *
     * @param winner The player to play the celebration for.
     * @param plugin Your main plugin instance, required for the scheduler.
     */
    public static void playWinCelebration(Player winner, JavaPlugin plugin) {
        Random random = new Random();

        new BukkitRunnable() {
            int runs = 5;

            @Override
            public void run() {
                if (runs == 0) {
                    this.cancel();
                    return;
                }

                winner.getWorld().playSound(winner.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 1, 0.2F);
                Location fireworkLocation = winner.getLocation().clone().add(random.nextInt(5), 0, random.nextInt(5));
                Firework fw = (Firework) winner.getWorld().spawnEntity(fireworkLocation, EntityType.FIREWORK);
                FireworkMeta meta = fw.getFireworkMeta();

                meta.addEffect(FireworkEffect.builder()
                        .with(TYPES[random.nextInt(TYPES.length)])
                        .withColor(COLORS[random.nextInt(COLORS.length)])
                        .withColor(COLORS[random.nextInt(COLORS.length)])
                        .withColor(COLORS[random.nextInt(COLORS.length)])
                        .flicker(false)
                        .trail(true)
                        .build());

                meta.setPower(0);
                fw.setFireworkMeta(meta);

                runs--;
            }
        }.runTaskTimer(plugin, 0L, 20L);
    }

    @EventHandler
    public void onMinigameWin(MinigameWinEvent event) {
        Player winner = event.getWinner();
        JavaPlugin plugin = JavaPlugin.getProvidingPlugin(getClass());
        playWinCelebration(winner, plugin);
    }
}