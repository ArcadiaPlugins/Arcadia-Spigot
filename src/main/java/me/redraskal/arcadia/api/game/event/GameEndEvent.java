package me.redraskal.arcadia.api.game.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class GameEndEvent extends Event {

    private static HandlerList handlers = new HandlerList();

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}