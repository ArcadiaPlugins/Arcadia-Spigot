package me.redraskal.arcadia.listener;

import me.redraskal.arcadia.Arcadia;
import me.redraskal.arcadia.ArcadiaAPI;
import me.redraskal.arcadia.Utils;
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
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        event.setQuitMessage(null);
        ArcadiaAPI api = Arcadia.getPlugin(Arcadia.class).getAPI();
        if(api.getGameManager().isAlive(event.getPlayer())) api.getGameManager().setAlive(event.getPlayer(), false);
    }
}