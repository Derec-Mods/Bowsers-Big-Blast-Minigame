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
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Countdown timer that shows a bossbar per player and ticks every second.
 * - Mimics Game.java countdown behavior by sending titles and playing click sounds at thresholds
 */
public class CountdownTimer {
    private final Plugin plugin;
    private final Map<Player, BossBar> bars = new HashMap<>();
    private BukkitRunnable task;
    private int totalSeconds = 10;

    public CountdownTimer(Plugin plugin, Collection<Player> players) {
        this.plugin = plugin;
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
        cancel();
        if (seconds <= 0) seconds = 10;
        this.totalSeconds = seconds;
        final int[] remaining = { seconds };

        updateAllBars(remaining[0]);

        task = new BukkitRunnable() {
            @Override
            public void run() {
                // Decrement first so bossbar shows updated value immediately on schedule tick
                remaining[0]--;

                if (remaining[0] < 0) {
                    stopAndHide();
                    return;
                }

                updateAllBars(remaining[0]);

                if (remaining[0] <= 5 && remaining[0] > 0) {
                    playDingToAll();
                }

                if (remaining[0] == 10) {
                    sendTitleToAll("§a" + remaining[0]); // green
                } else if (remaining[0] <= 5 && remaining[0] > 3) {
                    sendTitleToAll("§6" + remaining[0]); // gold
                } else if (remaining[0] <= 3 && remaining[0] > 0) {
                    sendTitleToAll("§c" + remaining[0]); // red
                    playClickToAll();
                } else if (remaining[0] == 0) {
                    // final tick reached - show 0 briefly (bossbar updated already)
                    sendTitleToAll("§c0");
                }
            }
        };

        task.runTaskTimer(plugin, 0L, 20L);
    }

    private void updateAllBars(int remaining) {
        double progress = totalSeconds > 0 ? Math.max(0.0, Math.min(1.0, remaining / (double) totalSeconds)) : 0.0;
        BarColor color = determineColor(remaining);
        String title = "Time: " + remaining;

        // snapshot players to avoid concurrent modification
        Set<Map.Entry<Player, BossBar>> entries = new HashSet<>(bars.entrySet());
        for (Map.Entry<Player, BossBar> e : entries) {
            BossBar bar = e.getValue();
            try {
                bar.setTitle(title);
                bar.setProgress(progress);
                bar.setColor(color);
            } catch (Exception ignored) { }
        }
    }

    private BarColor determineColor(int remaining) {
        if (remaining <= 2) return BarColor.RED;
        if (remaining <= 5) return BarColor.YELLOW;
        return BarColor.GREEN;
    }

    private void playDingToAll() {
        // use a snapshot to avoid concurrent mod
        Set<Player> players = new HashSet<>(bars.keySet());
        for (Player p : players) {
            if (p != null && p.isOnline()) {
                p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, 1.0f);
            }
        }
    }

    private void playClickToAll() {
        Set<Player> players = new HashSet<>(bars.keySet());
        for (Player p : players) {
            if (p != null && p.isOnline()) {
                p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, 1.0f, 1.0f);
            }
        }
    }

    private void sendTitleToAll(String title) {
        Set<Player> players = new HashSet<>(bars.keySet());
        for (Player p : players) {
            if (p != null && p.isOnline()) {
                // Using Player#sendTitle for simplicity; timing values match Game.java (10,20,10)
                p.sendTitle(title, null, 10, 20, 10);
            }
        }
    }

    public void cancel() {
        if (task != null) {
            task.cancel();
            task = null;
        }
    }

    public void stopAndHide() {
        cancel();
        Set<Map.Entry<Player, BossBar>> entries = new HashSet<>(bars.entrySet());
        for (Map.Entry<Player, BossBar> e : entries) {
            BossBar bar = e.getValue();
            try {
                bar.removeAll();
            } catch (Exception ignored) { }
        }
        bars.clear();
    }

    /**
     * Add a player to the timer (creates a bossbar for them if not present)
     */
    public void addPlayer(Player p) {
        if (bars.containsKey(p)) return;
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
                if (bar.getPlayers().isEmpty()) bar.removeAll();
            } catch (Exception ignored) { }
        }
    }
}