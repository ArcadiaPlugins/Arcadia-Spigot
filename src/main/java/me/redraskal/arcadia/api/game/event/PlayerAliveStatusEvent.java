package me.redraskal.arcadia.api.game.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerAliveStatusEvent extends Event {

    private static HandlerList handlers = new HandlerList();

    private final Player player;
    private final boolean alive;
    private final boolean spectating;

    public PlayerAliveStatusEvent(Player player, boolean alive, boolean spectating) {
        this.player = player;
        this.alive = alive;
        this.spectating = spectating;
    }

    public Player getPlayer() {
        return this.player;
    }

    public boolean isAlive() {
        return this.alive;
    }

    public boolean isSpectating() {
        return this.spectating;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}