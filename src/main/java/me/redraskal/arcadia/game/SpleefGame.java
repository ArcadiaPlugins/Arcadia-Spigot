package me.redraskal.arcadia.game;

import me.redraskal.arcadia.Arcadia;
import me.redraskal.arcadia.Utils;
import me.redraskal.arcadia.api.game.BaseGame;
import me.redraskal.arcadia.api.map.GameMap;
import me.redraskal.arcadia.api.scoreboard.SidebarSettings;
import me.redraskal.arcadia.api.scoreboard.WinMethod;
import me.redraskal.arcadia.api.scoreboard.defaults.PlayersLeftSidebar;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

public class SpleefGame extends BaseGame {

    public SpleefGame(GameMap gameMap) {
        super(Arcadia.getPlugin(Arcadia.class).getAPI().getTranslationManager().fetchTranslation("game.spleef.name").build(),
                new String[]{"startPosition", "floorLevel", "platformBoundsA", "platformBoundsB"},
                new SidebarSettings(PlayersLeftSidebar.class,
                    WinMethod.LAST_PLAYER_STANDING, 1, 30), gameMap,
                Arcadia.getPlugin(Arcadia.class).getAPI().getTranslationManager().fetchTranslation("game.spleef.desc").build());
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
        //TODO: Remove floor automatically
    }

    @EventHandler
    public void onSnowballHit(ProjectileHitEvent event) {
        if(event.getEntity() instanceof Snowball) {
            if(event.getHitBlock() != null && event.getHitBlock().getType() == Material.SNOW_BLOCK) {
                event.getHitBlock().breakNaturally(new ItemStack(Material.AIR));
            }
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if(this.getAPI().getGameManager().isAlive(event.getPlayer())) {
            if(event.getBlock().getType() == Material.SNOW_BLOCK) {
                event.getPlayer().getInventory().addItem(new ItemStack(Material.SNOW_BALL, 2));
                event.setDropItems(false);
            }
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