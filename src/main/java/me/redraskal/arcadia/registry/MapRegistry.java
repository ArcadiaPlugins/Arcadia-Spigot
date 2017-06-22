package me.redraskal.arcadia.registry;

import com.google.common.base.Preconditions;
import me.redraskal.arcadia.Arcadia;
import me.redraskal.arcadia.Cuboid;
import me.redraskal.arcadia.FileUtils;
import me.redraskal.arcadia.Utils;
import me.redraskal.arcadia.api.game.BaseGame;
import me.redraskal.arcadia.api.map.GameMap;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class MapRegistry {

    private List<GameMap> registeredMaps = new ArrayList<>();
    private World currentWorld;
    private Cuboid mapBounds;
    public World oldWorld;

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
            boolean result = this.registerMap(gameMap);
            if(result) {
                if(properties.getProperty("games") != null && !properties.getProperty("games").isEmpty()) {
                    List<String> temp = new ArrayList<String>();
                    for(String allowedGame : properties.getProperty("games").split(",")) temp.add(allowedGame);
                    for(Class<? extends BaseGame> game
                            : Arcadia.getPlugin(Arcadia.class).getAPI().getGameRegistry().getRegisteredGames()) {
                        if(temp.contains(game.getName())) {
                            Arcadia.getPlugin(Arcadia.class).getAPI().getGameRegistry().applyMap(gameMap, game);
                        }
                    }
                }
            }
            return result;
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

    /**
     * Returns the current map boundaries as a Cuboid.
     * @return
     */
    public Cuboid getMapBounds() {
        return this.mapBounds;
    }

    public World loadWorld(GameMap gameMap) {
        String worldName = "game";
        if(this.currentWorld != null) {
            this.oldWorld = this.currentWorld;
            if(!this.currentWorld.getName().equalsIgnoreCase("game2")) {
                worldName = "game2";
            }
        }
        Arcadia.getPlugin(Arcadia.class).getLogger().info("[MapRegistry] [/] Copying map from " + gameMap.getMapDirectory().getPath() + "...");
        FileUtils.copyDirectory(gameMap.getMapDirectory().getAbsoluteFile(),
                new File(Bukkit.getWorldContainer().getPath() + "/" + worldName + "/"));
        Arcadia.getPlugin(Arcadia.class).getLogger().info("[MapRegistry] [/] Loading " + worldName + "...");
        this.currentWorld = Bukkit.getServer().createWorld(new WorldCreator(worldName));
        this.currentWorld.setAutoSave(false);
        currentWorld.setGameRuleValue("doFireTick", "false");
        currentWorld.setGameRuleValue("doMobSpawning", "false");
        currentWorld.setGameRuleValue("randomTickSpeed", "0");
        currentWorld.setGameRuleValue("doDaylightCycle", "false");
        for(Entity entity : currentWorld.getEntities()) {
            if(!(entity instanceof Player)) {
                entity.remove();
            }
        }
        this.mapBounds = new Cuboid(Utils.parseLocation((String) gameMap.fetchSetting("mapBoundsA")),
            Utils.parseLocation((String) gameMap.fetchSetting("mapBoundsB")));
        return this.currentWorld;
    }

    public boolean unloadWorld(World world) {
        if(world == null) return false;
        Arcadia.getPlugin(Arcadia.class).getLogger().info("[MapRegistry] [/] Unloading " + world.getName() + "...");
        world.setAutoSave(false);
        final File worldDirectory = world.getWorldFolder();
        Utils.fullyUnloadWorld(world);
        FileUtils.deleteDirectory(worldDirectory);
        return true;
    }
}