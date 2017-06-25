package me.redraskal.arcadia;

import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class Freeze implements Listener {

    private final ArmorStand entity;
    private final Player player;

    public Freeze(Player player) {
        this.player = player;
        this.entity = player.getLocation().getWorld().spawn(player.getLocation(),
                ArmorStand.class);
        Arcadia.getPlugin(Arcadia.class).getServer()
            .getPluginManager().registerEvents(this, Arcadia.getPlugin(Arcadia.class));
        entity.setVisible(false);
        entity.setGravity(false);
        entity.setBasePlate(false);
        entity.setSmall(false);
        entity.setCanPickupItems(false);
        new BukkitRunnable() {
            public void run() {
                entity.addPassenger(player);
            }
        }.runTaskLater(Arcadia.getPlugin(Arcadia.class), 1L);
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if(event.getEntity().getUniqueId() == entity.getUniqueId())
            event.setCancelled(true);
        if(event.getEntity().getUniqueId() == player.getUniqueId())
            event.setCancelled(true);
    }

    @EventHandler
    public void onEntityDamaged(EntityDamageByEntityEvent event) {
        if(event.getDamager().getUniqueId() == player.getUniqueId())
            event.setCancelled(true);
    }

    @EventHandler
    public void onEntityManipulate(PlayerArmorStandManipulateEvent event) {
        if(event.getRightClicked().getUniqueId() == entity.getUniqueId())
            event.setCancelled(true);
    }

    @EventHandler
    public void onPluginDisable(PluginDisableEvent event) {
        if(event.getPlugin() == Arcadia.getPlugin(Arcadia.class)) {
            destroy();
        }
    }

    public boolean destroy() {
        if(entity.isDead()) return false;
        entity.remove();
        HandlerList.unregisterAll(this);
        return true;
    }
}