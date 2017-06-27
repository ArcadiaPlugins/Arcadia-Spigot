package me.redraskal.arcadia.api.scoreboard;

import me.redraskal.arcadia.Arcadia;
import me.redraskal.arcadia.ArcadiaAPI;
import me.redraskal.arcadia.Utils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

public abstract class Sidebar implements Listener {

    private final Scoreboard scoreboard;
    private final Objective sidebar;
    private final ArcadiaAPI api;

    /**
     * A fun scoreboard system for Arcadia.
     */
    public Sidebar() {
        this.scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        this.sidebar = scoreboard.registerNewObjective("sidebar", "dummy");
        this.api = Arcadia.getPlugin(Arcadia.class).getAPI();
        sidebar.setDisplaySlot(DisplaySlot.SIDEBAR);
        this.updateDisplayName(0, 10);
        for(Player player : Bukkit.getOnlinePlayers()) player.setScoreboard(scoreboard);
        this.onCreation();
        api.getPlugin().getServer().getPluginManager().registerEvents(this, api.getPlugin());
    }

    public abstract void onCreation();

    /**
     * Returns the sidebar.
     * @return
     */
    public Objective getSidebar() {
        return this.sidebar;
    }

    /**
     * Returns the ArcadiaAPI.
     * @return
     */
    public ArcadiaAPI getAPI() {
        return this.api;
    }

    /**
     * Updates the scoreboard display name.
     * (Called every second & or on certain events)
     */
    public void updateDisplayName(int minutes, int seconds) {
        final String currentRotation = "" + (api.getGameManager().getRotation().getCurrentID()+1);
        final String rotationSize = "" + api.getGameManager().getRotation().getSize();
        final String currentTime = Utils.formatTime(minutes, seconds);
        switch(api.getGameManager().getGameState()) {
            case STARTING: this.sidebar.setDisplayName(api.getTranslationManager()
                    .fetchTranslation("ui.scoreboard.title-starting").build(currentRotation, rotationSize, currentTime)); break;
            case INGAME: this.sidebar.setDisplayName(api.getTranslationManager()
                    .fetchTranslation("ui.scoreboard.title-ingame").build(currentRotation, rotationSize, currentTime)); break;
            case FINISHED: this.sidebar.setDisplayName(api.getTranslationManager()
                    .fetchTranslation("ui.scoreboard.title-finished").build(currentRotation, rotationSize, currentTime)); break;
        }
    }
}