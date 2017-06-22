package me.redraskal.arcadia;

import me.redraskal.arcadia.api.game.GameManager;
import me.redraskal.arcadia.registry.GameRegistry;
import me.redraskal.arcadia.registry.MapRegistry;

public class ArcadiaAPI {

    private final Arcadia plugin;
    private final MapRegistry mapRegistry;
    private final GameRegistry gameRegistry;
    private final GameManager gameManager;

    /**
     * A fun box full of API methods (or something like that).
     * @param plugin
     */
    ArcadiaAPI(Arcadia plugin) {
        this.plugin = plugin;
        this.mapRegistry = new MapRegistry();
        this.gameRegistry = new GameRegistry();
        this.gameManager = new GameManager();
    }

    /**
     * Returns the official Arcadia instance ;o
     * @return
     */
    public Arcadia getPlugin() {
        return this.plugin;
    }

    /**
     * Returns the MapRegistry.
     * @return
     */
    public MapRegistry getMapRegistry() {
        return this.mapRegistry;
    }

    /**
     * Returns the GameRegistry.
     * @return
     */
    public GameRegistry getGameRegistry() {
        return this.gameRegistry;
    }

    /**
     * Returns the GameManager.
     * @return
     */
    public GameManager getGameManager() {
        return this.gameManager;
    }
}