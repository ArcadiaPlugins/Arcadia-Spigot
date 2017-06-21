package me.redraskal.arcadia.registry;

import com.google.common.base.Preconditions;
import me.redraskal.arcadia.api.map.GameMap;

import java.util.ArrayList;
import java.util.List;

public class MapRegistry {

    private List<GameMap> registeredMaps = new ArrayList<>();

    /**
     * Adds the specified GameMap to the MapRegistry.
     * @param gameMap
     * @return
     */
    public boolean registerMap(GameMap gameMap) {
        Preconditions.checkNotNull(gameMap, "Map cannot be null");
        if(registeredMaps.contains(gameMap)) return false;
        registeredMaps.add(gameMap);
        return true;
    }

    /**
     * Removes the specified GameMap from the MapRegistry.
     * @param gameMap
     * @return
     */
    public boolean unregisterMap(GameMap gameMap) {
        Preconditions.checkNotNull(gameMap, "Map cannot be null");
        if(!registeredMaps.contains(gameMap)) return false;
        registeredMaps.remove(gameMap);
        return true;
    }

    //TODO: Map loader (automatic)
    //TODO: Loading/Unloading world system
}