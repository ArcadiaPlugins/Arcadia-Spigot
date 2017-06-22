package me.redraskal.arcadia.listener;

import me.redraskal.arcadia.Arcadia;
import me.redraskal.arcadia.Utils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class ConnectionListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        event.setJoinMessage(null);
        Utils.resetPlayer(event.getPlayer());
        Arcadia.getPlugin(Arcadia.class).getAPI()
            .getGameManager().setAlive(event.getPlayer(), false);
        if(Arcadia.getPlugin(Arcadia.class).getAPI().getGameManager().getCurrentGame() != null) {
            event.getPlayer().setScoreboard(Arcadia.getPlugin(Arcadia.class).getAPI()
                .getGameManager().getCurrentGame().getSidebar().getSidebar().getScoreboard());
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        event.setQuitMessage(null);
    }
}