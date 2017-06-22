package me.redraskal.arcadia.api.scoreboard;

import me.redraskal.arcadia.Arcadia;
import me.redraskal.arcadia.ArcadiaAPI;
import me.redraskal.arcadia.Utils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

public abstract class Sidebar {

    private final Scoreboard scoreboard;
    private final Objective sidebar;

    /**
     * A fun scoreboard system for Arcadia.
     */
    public Sidebar() {
        this.scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        this.sidebar = scoreboard.registerNewObjective("sidebar", "dummy");
        sidebar.setDisplaySlot(DisplaySlot.SIDEBAR);
        this.updateDisplayName(0, 10);
        for(Player player : Bukkit.getOnlinePlayers()) player.setScoreboard(scoreboard);
        this.onCreation();
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
     * Updates the scoreboard display name.
     * (Called every second & or on certain events)
     */
    public void updateDisplayName(int minutes, int seconds) {
        ArcadiaAPI api = Arcadia.getPlugin(Arcadia.class).getAPI();
        final String rotation = "[" + (api.getGameManager().getRotation().getCurrentID()+1)
            + "/" + api.getGameManager().getRotation().getSize() + "]";
        final String currentTime = Utils.formatTime(minutes, seconds);
        switch(api.getGameManager().getGameState()) {
            case STARTING: sidebar.setDisplayName("  " + rotation + " " + ChatColor.BLUE + currentTime + "  "); break;
            case INGAME: sidebar.setDisplayName("  " + rotation + " " + ChatColor.GREEN + currentTime + "  "); break;
            case FINISHED: sidebar.setDisplayName("  " + rotation + " " + ChatColor.GOLD + currentTime + "  "); break;
        }
    }
}