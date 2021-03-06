package me.redraskal.arcadia.api.scoreboard.defaults;

import me.redraskal.arcadia.Arcadia;
import me.redraskal.arcadia.ArcadiaAPI;
import me.redraskal.arcadia.api.game.event.PlayerAliveStatusEvent;
import me.redraskal.arcadia.api.scoreboard.Sidebar;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

public class ScoreSidebar extends Sidebar {

    public boolean removeDeadPlayers = false;
    public boolean fixScoreboard = true;

    @Override
    public void onCreation() {
        ArcadiaAPI api = Arcadia.getPlugin(Arcadia.class).getAPI();
        for(Player player : Bukkit.getOnlinePlayers()) {
            if(!api.getGameManager().isAlive(player)) continue;
            this.setScore(player, 0);
        }
    }

    @EventHandler
    public void onPlayerStatus(PlayerAliveStatusEvent event) {
        if(!event.isAlive() && removeDeadPlayers) {
            this.getSidebar().getScoreboard().resetScores(event.getPlayer().getName());
        }
    }

    /**
     * Updates the specified Player's score.
     * @param player
     * @param score
     */
    public void setScore(Player player, int score) {
        this.getSidebar().getScore(player.getName()).setScore(score);
    }

    /**
     * Returns the specified Player's score.
     * @param player
     * @return
     */
    public int getScore(Player player) {
        if(this.getSidebar().getScore(player) != null) {
            return this.getSidebar().getScore(player).getScore();
        }
        return 0;
    }
}