package me.redraskal.arcadia.api.game;

import com.google.common.base.Preconditions;
import me.redraskal.arcadia.Arcadia;
import me.redraskal.arcadia.ArcadiaAPI;
import me.redraskal.arcadia.api.game.event.PlayerAliveStatusEvent;
import me.redraskal.arcadia.api.map.GameMap;
import me.redraskal.arcadia.api.scoreboard.Sidebar;
import me.redraskal.arcadia.api.scoreboard.SidebarSettings;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.material.MaterialData;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseGame implements Listener {

    private final ArcadiaAPI api;

    private final String name;
    private final SidebarSettings sidebarSettings;
    private Sidebar sidebar;
    private GameMap gameMap;
    private final String description;
    private String[] requiredSettings;
    private List<Player> deathOrder = new ArrayList<Player>();
    public List<Player> spectatorCache = new ArrayList<Player>();

    public boolean allowPVP = false;
    public boolean killOnMapExit = true;
    public List<MaterialData> breakableBlocks = new ArrayList<>();

    /**
     * The base game layout.
     * @param name
     * @param requiredSettings
     * @param description
     */
    public BaseGame(String name, String[] requiredSettings, SidebarSettings sidebarSettings, GameMap gameMap, String description) {
        Preconditions.checkNotNull(name, "Game name cannot be null");
        Preconditions.checkNotNull(name, "Description cannot be null");
        this.name = name;
        if(requiredSettings != null) {
            this.requiredSettings = requiredSettings;
        } else {
            this.requiredSettings = new String[]{};
        }
        this.description = description;
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

    @EventHandler
    public void onPlayerDeath(PlayerAliveStatusEvent event) {
        if(!event.isAlive() && !deathOrder.contains(event.getPlayer())) {
            deathOrder.add(event.getPlayer());
        }
    }

    /**
     * Kills all the players, which ends the game.
     */
    public void endGame() {
        GameManager manager = this.getAPI().getGameManager();
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (manager.isAlive(player)) {
                manager.setAlive(player, false);
            }
        }
    }

    /**
     * Returns the list of players who died in order.
     * @return
     */
    public List<Player> getDeathOrder() {
        return this.deathOrder;
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
    public String getDescription() {
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
     * Returns the SidebarSettings instance.
     * @return
     */
    public SidebarSettings getSidebarSettings() {
        return this.sidebarSettings;
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