package me.redraskal.arcadia;

import org.bukkit.plugin.java.JavaPlugin;

public class Arcadia extends JavaPlugin {

    private ArcadiaAPI api;

    public void onEnable() {
        this.api = new ArcadiaAPI(this);
        //TODO: Actually make stuff work
    }

    /**
     * Returns the fun API for this madness.
     * @return
     */
    public ArcadiaAPI getAPI() {
        return this.api;
    }
}