package me.redraskal.arcadia.listener;

import me.redraskal.arcadia.Arcadia;
import me.redraskal.arcadia.api.game.GameState;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;

public class DamageListener implements Listener {

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if(event.getCause() == EntityDamageEvent.DamageCause.CUSTOM) return;
        if(Arcadia.getPlugin(Arcadia.class).getAPI().getGameManager().getGameState() != GameState.INGAME) {
            event.setCancelled(true);
        } else {
            if(!Arcadia.getPlugin(Arcadia.class).getAPI().getGameManager().getCurrentGame().allowPVP) event.setCancelled(true);
        }
        if(!event.isCancelled() && event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            if((player.getHealth()-event.getDamage()) <= 0) {
                event.setDamage(0);
                if(Arcadia.getPlugin(Arcadia.class).getAPI().getGameManager().isAlive(player)) {
                    Arcadia.getPlugin(Arcadia.class).getAPI().getGameManager().setAlive(player, false);
                }
            }
        }
    }

    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        event.setCancelled(true);
    }
}