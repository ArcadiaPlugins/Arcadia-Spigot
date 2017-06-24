package me.redraskal.arcadia.listener;

import me.redraskal.arcadia.Arcadia;
import me.redraskal.arcadia.ArcadiaAPI;
import me.redraskal.arcadia.Utils;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;

public class SpectatorListener implements Listener {

    @EventHandler
    public void onPlayerToggleFlight(PlayerToggleFlightEvent event) {
        if(!Arcadia.getPlugin(Arcadia.class).getAPI().getGameManager().isAlive(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if(!event.getTo().getWorld().getName().equalsIgnoreCase(event.getFrom().getWorld().getName())) return;
        if(event.getTo().getWorld().getName().equalsIgnoreCase("world")) return;
        ArcadiaAPI api = Arcadia.getPlugin(Arcadia.class).getAPI();
        if(!api.getGameManager().isAlive(event.getPlayer())) {
            double yLevel = Utils.parseLocation((String) api.getGameManager().getCurrentGame()
                .getGameMap().fetchSetting("spectatorLocation")).getY();
            double yDif = Math.abs(event.getTo().getY() - yLevel);
            Location newLocation = event.getTo();
            newLocation.setY(yLevel);
            if(yDif >= 0.5) event.setTo(newLocation);
        }
        if(api.getMapRegistry().getMapBounds() != null) {
            if(!api.getMapRegistry().getMapBounds().contains(event.getTo())) {
                if(api.getGameManager().isAlive(event.getPlayer())
                        && api.getGameManager().getCurrentGame().killOnMapExit) {
                    api.getGameManager().setAlive(event.getPlayer(), false);
                } else {
                    event.setTo(event.getFrom());
                }
            }
        }
    }
}