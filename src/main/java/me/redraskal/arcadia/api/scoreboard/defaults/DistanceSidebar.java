package me.redraskal.arcadia.api.scoreboard.defaults;

import me.redraskal.arcadia.Arcadia;
import me.redraskal.arcadia.ArcadiaAPI;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;

public class DistanceSidebar extends ScoreSidebar {

    private Location target;
    public int spectatorValue = -1000;

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if(target == null) return;
        if(!event.getTo().getWorld().getName().equalsIgnoreCase(event.getFrom().getWorld().getName())) return;
        if(event.getTo().getBlockX() == event.getFrom().getBlockX()
            && event.getTo().getBlockZ() == event.getFrom().getBlockZ()) return;
        updateTarget(event.getPlayer());
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        this.setScore(event.getPlayer(), spectatorValue);
    }

    private void updateTarget(Player player) {
        if(target == null) return;
        if(!target.getWorld().getName().equalsIgnoreCase(player.getWorld().getName())) return;
        if(Arcadia.getPlugin(Arcadia.class).getAPI().getGameManager().isAlive(player)) {
            this.setScore(player, -Double.valueOf(player.getLocation().distance(target)).intValue());
        } else {
            this.setScore(player, spectatorValue);
        }
    }

    /**
     * Sets the target modifying a player's score.
     * @param location
     */
    public void setTarget(Location location) {
        this.target = location;
        ArcadiaAPI api = Arcadia.getPlugin(Arcadia.class).getAPI();
        for(Player player : Bukkit.getOnlinePlayers()) {
            if(api.getGameManager().isAlive(player)) updateTarget(player);
        }
    }
}