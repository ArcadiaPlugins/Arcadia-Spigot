package me.redraskal.arcadia.registry;

import com.google.common.base.Preconditions;
import me.redraskal.arcadia.Arcadia;
import me.redraskal.arcadia.api.map.GameMap;
import org.bukkit.World;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class MapRegistry {

    private List<GameMap> registeredMaps = new ArrayList<>();
    private World currentWorld;

    /**
     * Adds the specified GameMap to the MapRegistry.
     * @param gameMap
     * @return
     */
    public boolean registerMap(GameMap gameMap) {
        Preconditions.checkNotNull(gameMap, "Map cannot be null");
        if(registeredMaps.contains(gameMap)) return false;
        Arcadia.getPlugin(Arcadia.class).getLogger().info("[MapRegistry] " + gameMap.getName() + " has been registered.");
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
        Arcadia.getPlugin(Arcadia.class).getLogger().info("[MapRegistry] " + gameMap.getName() + " has been unregistered.");
        registeredMaps.remove(gameMap);
        return true;
    }

    /**
     * Attempts to load a GameMap from the specified directory.
     * @param directory
     * @return
     */
    public boolean loadMap(File directory) {
        Arcadia.getPlugin(Arcadia.class).getLogger().info("[MapRegistry] Attempting to load " + directory.getPath() + "...");
        File mapSettingsFile = new File(directory, "settings.properties");
        if(!mapSettingsFile.exists()) return false;
        Properties properties = new Properties();
        try {
            FileInputStream inputStream = new FileInputStream(mapSettingsFile);
            properties.load(inputStream);
            inputStream.close();
            Preconditions.checkNotNull(properties.getProperty("name"), "Map name is not set");
            GameMap gameMap = new GameMap(properties.getProperty("name"), directory);
            for(Object property : properties.keySet()) gameMap.modifySetting((String) property, properties.get(property));
            return this.registerMap(gameMap);
        } catch (Exception e) {
            Arcadia.getPlugin(Arcadia.class).getLogger().info("[MapRegistry] An error has occured while loading (" + directory.getPath() + "):");
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Attempts to load every map in the specified root directory.
     * @param rootDirectory
     */
    public void loadMaps(File rootDirectory) {
        Arcadia.getPlugin(Arcadia.class).getLogger().info("[MapRegistry] Attempting to load maps...");
        for(File file : rootDirectory.listFiles()) {
            if(!file.isDirectory()) continue;
            loadMap(file);
        }
    }

    /**
     * Returns the current game world.
     * @return
     */
    public World getCurrentWorld() {
        return this.currentWorld;
    }

    //TODO: Loading/Unloading world system
}