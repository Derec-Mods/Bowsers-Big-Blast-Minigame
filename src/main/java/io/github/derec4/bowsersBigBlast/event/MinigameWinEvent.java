package io.github.derec4.bowsersBigBlast.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class MinigameWinEvent extends Event implements Cancellable {
    private static final HandlerList HANDLER_LIST = new HandlerList();
    private Player winner;
    private boolean cancelled;


    public MinigameWinEvent (Player winner) {
        this.winner = winner;
    }


    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }

    /**
     * Gets the winning player.
     * @return The Player who won.
     */
    public Player getWinner() {
        return this.winner;
    }

    @Override
    public void setCancelled(boolean choice) {
        this.cancelled = choice;
    }

    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }
}
