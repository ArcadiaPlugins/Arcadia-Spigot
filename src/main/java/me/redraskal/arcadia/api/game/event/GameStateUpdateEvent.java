package me.redraskal.arcadia.api.game.event;

import me.redraskal.arcadia.api.game.GameState;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class GameStateUpdateEvent extends Event {

    private static HandlerList handlers = new HandlerList();

    private final GameState gameState;

    public GameStateUpdateEvent(GameState gameState) {
        this.gameState = gameState;
    }

    public GameState getGameState() {
        return this.gameState;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
