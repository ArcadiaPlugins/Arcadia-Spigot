package me.redraskal.arcadia.listener;

import me.redraskal.arcadia.Arcadia;
import me.redraskal.arcadia.ArcadiaAPI;
import me.redraskal.arcadia.Utils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class ConnectionListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        event.setJoinMessage(null);
        ArcadiaAPI api = Arcadia.getPlugin(Arcadia.class).getAPI();
        Utils.resetPlayer(event.getPlayer());
        api.getGameManager().setAlive(event.getPlayer(), false);
        if(api.getGameManager().getCurrentGame() != null) {
            event.getPlayer().setScoreboard(api.getGameManager().getCurrentGame().getSidebar().getSidebar().getScoreboard());
            Bukkit.broadcastMessage(ChatColor.GREEN + "=== " + ChatColor.BOLD + api.getGameManager().getCurrentGame().getName() + ChatColor.GREEN + " ===");
            for(String line : api.getGameManager().getCurrentGame().getDescription()) Bukkit.broadcastMessage(ChatColor.GOLD + line);
            Bukkit.broadcastMessage(ChatColor.GREEN + "DON'T LEAVE! " + ChatColor.GRAY + "The next game will start shortly.");
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        event.setQuitMessage(null);
        ArcadiaAPI api = Arcadia.getPlugin(Arcadia.class).getAPI();
        if(api.getGameManager().isAlive(event.getPlayer())) api.getGameManager().setAlive(event.getPlayer(), false);
    }
}