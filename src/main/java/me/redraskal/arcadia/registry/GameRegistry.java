package me.redraskal.arcadia.registry;

import com.google.common.base.Preconditions;
import me.redraskal.arcadia.api.game.BaseGame;

import java.util.ArrayList;
import java.util.List;

public class GameRegistry {

    private List<Class<? extends BaseGame>> registeredGames = new ArrayList<>();

    /**
     * Adds the specified BaseGame to the MapRegistry.
     * @param game
     * @return
     */
    public boolean registerGame(Class<? extends BaseGame> game) {
        Preconditions.checkNotNull(game, "Game cannot be null");
        if(registeredGames.contains(game)) return false;
        registeredGames.add(game);
        return true;
    }

    /**
     * Removes the specified BaseGame from the MapRegistry.
     * @param game
     * @return
     */
    public boolean unregisterGame(Class<? extends BaseGame> game) {
        Preconditions.checkNotNull(game, "Game cannot be null");
        if(!registeredGames.contains(game)) return false;
        registeredGames.remove(game);
        return true;
    }

    //TODO: Apply maps to BaseGame instances
}