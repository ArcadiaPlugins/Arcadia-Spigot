package me.redraskal.arcadia.api.game;

import com.google.common.base.Preconditions;
import org.bukkit.event.Listener;

public abstract class BaseGame implements Listener {

    private final String name;
    private final String[] description;
    private final String[] requiredSettings;

    /**
     * The base game layout.
     * @param name
     * @param requiredSettings
     * @param description
     */
    public BaseGame(String name, String[] requiredSettings, String... description) {
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