package me.redraskal.arcadia.game;

import me.redraskal.arcadia.Utils;
import me.redraskal.arcadia.api.game.BaseGame;
import me.redraskal.arcadia.api.game.event.GameTickEvent;
import me.redraskal.arcadia.api.map.GameMap;
import me.redraskal.arcadia.api.scoreboard.SidebarSettings;
import me.redraskal.arcadia.api.scoreboard.WinMethod;
import me.redraskal.arcadia.api.scoreboard.defaults.PlayersLeftSidebar;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

public class SpleefGame extends BaseGame {

    public SpleefGame(GameMap gameMap) {
        super("Spleef", new String[]{"startPosition", "floorLevel", "platformBoundsA", "platformBoundsB"},
                new SidebarSettings(PlayersLeftSidebar.class,
                    WinMethod.LAST_PLAYER_STANDING, 1, 30), gameMap,
                "Use your shovel to drop players off the map! Careful, the arena is melting!");
    }

    @Override
    public void onPreStart() {
        Location spawnLocation = Utils.parseLocation((String) this.getGameMap().fetchSetting("startPosition"));
        ItemStack itemStack = new ItemStack(Material.DIAMOND_SPADE, 1);
        for(Player player : Bukkit.getOnlinePlayers()) {
            if(!this.getAPI().getGameManager().isAlive(player)) continue;
            player.teleport(spawnLocation);
            player.setGameMode(GameMode.SURVIVAL);
            player.getInventory().addItem(itemStack);
        }
    }

    @Override
    public void onGameStart() {
        this.breakableBlocks.add(new MaterialData(Material.SNOW_BLOCK));
    }

    @EventHandler
    public void onGameTick(GameTickEvent event) {
        for(Player player : Bukkit.getOnlinePlayers()) {
            if(!this.getAPI().getGameManager().isAlive(player)) continue;
            player.setVelocity(player.getLocation().getDirection().normalize().multiply(0.5D).setY(-1D));
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if(this.getAPI().getGameManager().isAlive(event.getPlayer())) {
            if(event.getTo().getY() <= Integer.valueOf((String) this.getGameMap().fetchSetting("floorLevel"))) {
                this.getAPI().getGameManager().setAlive(event.getPlayer(), false);
            }
        }
    }

    @Override
    public void onGameEnd() {}
}