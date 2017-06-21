package me.redraskal.arcadia.api.map;

import com.google.common.base.Preconditions;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class GameMap {

    private final String name;
    private final File mapDirectory;
    private final Map<String, Object> settings = new HashMap<>();

    /**
     * Initializes a new GameMap instance used in the game system.
     * @param name
     * @param mapDirectory
     */
    public GameMap(String name, File mapDirectory) {
        Preconditions.checkNotNull(name, "Map name cannot be null");
        Preconditions.checkNotNull(mapDirectory, "Map directory cannot be null");
        this.name = name;
        this.mapDirectory = mapDirectory;
    }

    /**
     * Returns the map name.
     * @return
     */
    public String getName() {
        return this.name;
    }

    /**
     * Returns the map directory.
     * @return
     */
    public File getMapDirectory() {
        return this.mapDirectory;
    }

    /**
     * Returns the specified map setting if it exists.
     * @param key
     * @return
     */
    public Object fetchSetting(String key) {
        if(!settings.containsKey(key)) return null;
        return settings.get(key);
    }

    /**
     * Returns true if the specified map setting exists.
     * @param key
     * @return
     */
    public boolean doesSettingExist(String key) {
        return settings.containsKey(key);
    }

    /**
     * Modifies a setting as long as the Object is not null.
     * @param key
     * @param value
     */
    public void modifySetting(String key, Object value) {
        Preconditions.checkNotNull(value, "Setting cannot be null");
        settings.put(key, value);
    }
}