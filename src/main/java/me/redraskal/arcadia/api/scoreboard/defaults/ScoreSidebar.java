package me.redraskal.arcadia.api.scoreboard.defaults;

import me.redraskal.arcadia.Arcadia;
import me.redraskal.arcadia.ArcadiaAPI;
import me.redraskal.arcadia.api.scoreboard.Sidebar;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class ScoreSidebar extends Sidebar {

    @Override
    public void onCreation() {
        ArcadiaAPI api = Arcadia.getPlugin(Arcadia.class).getAPI();
        for(Player player : Bukkit.getOnlinePlayers()) {
            if(!api.getGameManager().isAlive(player)) continue;
            this.setScore(player, 0);
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
}