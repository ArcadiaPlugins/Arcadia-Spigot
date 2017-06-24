package me.redraskal.arcadia.api.game.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class GameTickEvent extends Event {

    private static HandlerList handlers = new HandlerList();

    private final int totalTicks;
    private final int ticksInSecond;

    public GameTickEvent(int totalTicks, int ticksInSecond) {
        this.totalTicks = totalTicks;
        this.ticksInSecond = ticksInSecond;
    }

    public int getTotalTicks() {
        return this.totalTicks;
    }

    public int getTicksInSecond() {
        return this.ticksInSecond;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}