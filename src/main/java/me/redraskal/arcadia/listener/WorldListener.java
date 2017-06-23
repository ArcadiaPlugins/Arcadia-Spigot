package me.redraskal.arcadia.listener;

import me.redraskal.arcadia.Arcadia;
import me.redraskal.arcadia.ArcadiaAPI;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
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
        if(api.getGameManager().getCurrentGame() != null) {
            MaterialData materialData = new MaterialData(event.getBlock().getType(), event.getBlock().getData());
            if(!api.getGameManager().getCurrentGame().breakableBlocks.contains(materialData)) {
                event.setCancelled(true);
            }
        }
    }
}