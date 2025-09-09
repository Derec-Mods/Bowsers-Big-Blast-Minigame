package io.github.derec4.bowsersBigBlast.game;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Countdown timer that shows a bossbar per player and ticks every second.
 */
public class CountdownTimer {
    private final Plugin plugin;
    private final Map<Player, BossBar> bars = new HashMap<>();
    private BukkitRunnable task;
    private int totalSeconds;

    public CountdownTimer(Plugin plugin, Collection<Player> players) {
        this.plugin = plugin;
        totalSeconds = 10;

        for (Player p : players) {
            BossBar bar = Bukkit.createBossBar("Time: " + totalSeconds, BarColor.GREEN, BarStyle.SOLID);
            bar.addPlayer(p);
            bar.setProgress(1.0);
            bars.put(p, bar);
        }
    }

    public void start() {
        start(this.totalSeconds);
    }

    public void start(int seconds) {
        cancel(); // cancel any existing run

        if (seconds <= 0) {
            seconds = 10;
        }
        this.totalSeconds = seconds;
        final int[] remaining = {seconds};

        // initialize bossbar titles/progress/colors
        updateAllBars(remaining[0]);

        task = new BukkitRunnable() {
            @Override
            public void run() {
                remaining[0]--;
                if (remaining[0] < 0) {
                    stopAndHide();
                    return;
                }

                updateAllBars(remaining[0]);

                if (remaining[0] <= 5 && remaining[0] > 0) {
                    for (Player p : bars.keySet()) {
                        if (p.isOnline()) {
                            p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, 1.0f);
                        }
                    }
                }

                if (remaining[0] == 0) {
                    // final tick reached, hide after showing 0
                    // short delay to allow clients to see 0 for a tick
                    // next run will call stopAndHide
                }
            }
        };

        task.runTaskTimer(plugin, 0L, 20L);
    }

    private void updateAllBars(int remaining) {
        double progress = totalSeconds > 0 ? Math.max(0.0, Math.min(1.0, remaining / (double) totalSeconds)) : 0.0;
        BarColor color = determineColor(remaining);

        String title = "Time: " + remaining;
        for (Map.Entry<Player, BossBar> e : bars.entrySet()) {
            BossBar bar = e.getValue();
            bar.setTitle(title);
            bar.setProgress(progress);
            bar.setColor(color);
        }
    }

    private BarColor determineColor(int remaining) {
        if (remaining <= 2) {
            return BarColor.RED;
        }
        if (remaining <= 5) {
            return BarColor.YELLOW;
        }
        return BarColor.GREEN;
    }

    public void cancel() {
        if (task != null) {
            task.cancel();
            task = null;
        }
    }

    public void stopAndHide() {
        cancel();
        for (Map.Entry<Player, BossBar> e : bars.entrySet()) {
            BossBar bar = e.getValue();
            try {
                bar.removeAll();
            } catch (Exception ignored) {
            }
        }
        bars.clear();
    }

    /**
     * Add a player to the timer (creates a bossbar for them if not present)
     */
    public void addPlayer(Player p) {
        if (bars.containsKey(p)) {
            return;
        }
        BossBar bar = Bukkit.createBossBar("Time: " + totalSeconds, BarColor.GREEN, BarStyle.SOLID);
        bar.addPlayer(p);
        bar.setProgress(1.0);
        bars.put(p, bar);
    }

    /**
     * Remove a player from the timer (hides their bossbar)
     */
    public void removePlayer(Player p) {
        BossBar bar = bars.remove(p);
        if (bar != null) {
            try {
                bar.removePlayer(p);
                if (bar.getPlayers().isEmpty()) {
                    bar.removeAll();
                }
            } catch (Exception ignored) {
            }
        }
    }
}