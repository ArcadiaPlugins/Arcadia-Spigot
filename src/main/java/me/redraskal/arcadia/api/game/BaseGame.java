package me.redraskal.arcadia.api.game;

import com.google.common.base.Preconditions;
import me.redraskal.arcadia.Arcadia;
import me.redraskal.arcadia.ArcadiaAPI;
import me.redraskal.arcadia.api.map.GameMap;
import me.redraskal.arcadia.api.scoreboard.Sidebar;
import me.redraskal.arcadia.api.scoreboard.SidebarSettings;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseGame implements Listener {

    private final ArcadiaAPI api;

    private final String name;
    private final SidebarSettings sidebarSettings;
    private Sidebar sidebar;
    private GameMap gameMap;
    private final String[] description;
    private String[] requiredSettings;

    /**
     * The base game layout.
     * @param name
     * @param requiredSettings
     * @param description
     */
    public BaseGame(String name, String[] requiredSettings, SidebarSettings sidebarSettings, GameMap gameMap, String... description) {
        Preconditions.checkNotNull(name, "Game name cannot be null");
        this.name = name;
        if(requiredSettings != null) {
            this.requiredSettings = requiredSettings;
        } else {
            this.requiredSettings = new String[]{};
        }
        if(description != null) {
            this.description = description;
        } else {
            this.description = new String[]{};
        }
        List<String> defaultRequiredSettings = new ArrayList<String>();
        for(String setting : this.requiredSettings) defaultRequiredSettings.add(setting);
        defaultRequiredSettings.add("spectatorLocation");
        defaultRequiredSettings.add("mapBoundsA");
        defaultRequiredSettings.add("mapBoundsB");
        this.requiredSettings = defaultRequiredSettings
            .toArray(new String[defaultRequiredSettings.size()]);
        this.sidebarSettings = sidebarSettings;
        try {
            this.sidebar = sidebarSettings.getClazz().newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.api = Arcadia.getPlugin(Arcadia.class).getAPI();
        this.gameMap = gameMap;
    }

    /**
     * Returns the fun API :D
     * @return
     */
    public ArcadiaAPI getAPI() {
        return this.api;
    }

    /**
     * Returns the game name.
     * @return
     */
    public String getName() {
        return this.name;
    }

    /**
     * Returns all the required settings for a map.
     * @return
     */
    public String[] getRequiredSettings() {
        return this.requiredSettings;
    }

    /**
     * Returns the game description (line-by-line).
     * @return
     */
    public String[] getDescription() {
        return this.description;
    }

    /**
     * Returns the current GameMap.
     * @return
     */
    public GameMap getGameMap() {
        return this.gameMap;
    }

    /**
     * Returns the Sidebar instance.
     * @return
     */
    public Sidebar getSidebar() {
        return this.sidebar;
    }

    /**
     * This event is called when a map is loaded in.
     * (Right before the countdown begins)
     */
    public abstract void onPreStart();

    /**
     * This event is called after the game countdown
     * has ended.
     */
    public abstract void onGameStart();

    /**
     * This event is called once the game has ended.
     * (Due to a winner, or the maximum time reached)
     */
    public abstract void onGameEnd();
}