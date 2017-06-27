package me.redraskal.arcadia.listener;

import me.redraskal.arcadia.Arcadia;
import me.redraskal.arcadia.ArcadiaAPI;
import me.redraskal.arcadia.api.game.GameState;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityToggleGlideEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.material.MaterialData;

public class WorldListener implements Listener {

    @EventHandler
    public void onWeatherChange(WeatherChangeEvent event) {
        if(!event.toWeatherState()) event.setCancelled(true);
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        ArcadiaAPI api = Arcadia.getPlugin(Arcadia.class).getAPI();
        if(!api.getGameManager().isAlive(event.getPlayer())) event.setCancelled(true);
        if(api.getGameManager().getCurrentGame() != null) {
            MaterialData materialData = new MaterialData(event.getBlock().getType(), event.getBlock().getData());
            if(!api.getGameManager().getCurrentGame().breakableBlocks.contains(materialData)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onVehicleExit(VehicleExitEvent event) {
        if(event.getExited().getType() != EntityType.PLAYER) return;
        ArcadiaAPI api = Arcadia.getPlugin(Arcadia.class).getAPI();
        if(!api.getGameManager().isAlive((Player) event.getExited())) return;
        if(api.getGameManager().getGameState() != GameState.FINISHED) {
            if(event.getVehicle().hasMetadata("allow-exit")) return;
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onToggleGlide(EntityToggleGlideEvent event) {
        event.setCancelled(true);
    }
}