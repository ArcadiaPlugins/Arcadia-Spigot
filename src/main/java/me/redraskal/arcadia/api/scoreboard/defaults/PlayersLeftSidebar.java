package me.redraskal.arcadia.api.scoreboard.defaults;

import me.redraskal.arcadia.Arcadia;
import me.redraskal.arcadia.api.game.event.PlayerAliveStatusEvent;
import me.redraskal.arcadia.api.scoreboard.Sidebar;
import org.bukkit.event.EventHandler;

public class PlayersLeftSidebar extends Sidebar {

    @Override
    public void onCreation() {
        this.update();
    }

    /**
     * Updates the Players Left string.
     */
    public void update() {
        this.getSidebar().getScore("Players Left").setScore(
                Arcadia.getPlugin(Arcadia.class).getAPI().getGameManager().getPlayersAlive());
    }

    @EventHandler
    public void onPlayerStatus(PlayerAliveStatusEvent event) {
        this.update();
    }
}