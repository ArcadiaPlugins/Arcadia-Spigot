package me.redraskal.arcadia.listener;

import me.redraskal.arcadia.Arcadia;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;

public class ItemListener implements Listener {

    @EventHandler
    public void onItemDrop(PlayerDropItemEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onItemPickup(PlayerPickupItemEvent event) {
        if(!Arcadia.getPlugin(Arcadia.class).getAPI().getGameManager().isAlive(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onItemUse(PlayerInteractEvent event) {
        if(event.getItem() != null && event.getItem().getType() != Material.AIR) {
            if(event.getAction().toString().contains("RIGHT")) {
                if(event.getItem().getItemMeta() != null
                        && event.getItem().getItemMeta().hasDisplayName()) {
                    if(event.getItem().getItemMeta().getDisplayName().equals(ChatColor.GOLD + "Cosmetics")) {
                        Bukkit.getServer().dispatchCommand(event.getPlayer(), "uc menu main");
                    }
                }
            }
        }
    }
}