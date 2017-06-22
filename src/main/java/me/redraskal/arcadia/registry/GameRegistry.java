package me.redraskal.arcadia.registry;

import com.google.common.base.Preconditions;
import me.redraskal.arcadia.Arcadia;
import me.redraskal.arcadia.api.game.BaseGame;
import me.redraskal.arcadia.api.map.GameMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameRegistry {

    private List<Class<? extends BaseGame>> registeredGames = new ArrayList<>();
    private Map<GameMap, List<Class<? extends BaseGame>>> registeredMaps = new HashMap<>();

    /**
     * Adds the specified BaseGame to the MapRegistry.
     * @param game
     * @return
     */
    public boolean registerGame(Class<? extends BaseGame> game) {
        Preconditions.checkNotNull(game, "Game cannot be null");
        if(registeredGames.contains(game)) return false;
        Arcadia.getPlugin(Arcadia.class).getLogger().info("[GameRegistry] " + game.getName() + " has been registered.");
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
        Arcadia.getPlugin(Arcadia.class).getLogger().info("[GameRegistry] " + game.getName() + " has been unregistered.");
        registeredGames.remove(game);
        return true;
    }

    /**
     * Applies the specified map to a game.
     * @param gameMap
     * @param registeredGame
     * @return
     */
    public boolean applyMap(GameMap gameMap, Class<? extends BaseGame> registeredGame) {
        Preconditions.checkNotNull(gameMap, "Map cannot be null");
        Preconditions.checkNotNull(registeredGame, "Game cannot be null");
        if(!registeredGames.contains(registeredGame)) return false;
        List<Class<? extends BaseGame>> temp = new ArrayList<>();
        temp.add(registeredGame);
        if(registeredMaps.containsKey(gameMap)) {
            if(registeredMaps.get(gameMap).contains(registeredGame)) return false;
            temp.addAll(registeredMaps.get(gameMap));
        }
        registeredMaps.put(gameMap, temp);
        return true;
    }

    /**
     * Returns the registered games.
     * @return
     */
    public List<Class<? extends BaseGame>> getRegisteredGames() {
        return this.registeredGames;
    }

    /**
     * Returns maps registered to the specified game.
     * @param registeredGame
     * @return
     */
    public List<GameMap> getMaps(Class<? extends BaseGame> registeredGame) {
        Preconditions.checkNotNull(registeredGame, "Game cannot be null");
        List<GameMap> temp = new ArrayList<>();
        for(Map.Entry<GameMap, List<Class<? extends BaseGame>>> entry : this.registeredMaps.entrySet()) {
            if(entry.getValue().contains(registeredGame)) temp.add(entry.getKey());
        }
        return temp;
    }
}