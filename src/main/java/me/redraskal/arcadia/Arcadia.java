package me.redraskal.arcadia;

import me.redraskal.arcadia.game.DeadEndGame;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class Arcadia extends JavaPlugin {

    private ArcadiaAPI api;

    public void onEnable() {
        this.api = new ArcadiaAPI(this);
        //TODO: Actually make stuff work properly

        // Register Default Games (togglable later in the config)
        this.getAPI().getGameRegistry().registerGame(DeadEndGame.class);

        final File mapFolder = new File(this.getDataFolder().getPath() + "/maps/");
        mapFolder.mkdirs();
        this.getAPI().getMapRegistry().loadMaps(mapFolder);
    }

    /**
     * Returns the fun API for this madness.
     * @return
     */
    public ArcadiaAPI getAPI() {
        return this.api;
    }
}