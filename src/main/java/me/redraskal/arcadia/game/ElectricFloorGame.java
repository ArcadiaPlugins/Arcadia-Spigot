package me.redraskal.arcadia.game;

import me.redraskal.arcadia.Arcadia;
import me.redraskal.arcadia.Utils;
import me.redraskal.arcadia.api.game.BaseGame;
import me.redraskal.arcadia.api.game.GameState;
import me.redraskal.arcadia.api.map.GameMap;
import me.redraskal.arcadia.api.scoreboard.SidebarSettings;
import me.redraskal.arcadia.api.scoreboard.WinMethod;
import me.redraskal.arcadia.api.scoreboard.defaults.PlayersLeftSidebar;
import me.redraskal.arcadia.game.electricfloor.FloorOrder;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.material.MaterialData;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class ElectricFloorGame extends BaseGame {

    private FloorOrder floorOrder;
    private List<Block> changePending = new ArrayList<Block>();

    public ElectricFloorGame(GameMap gameMap) {
        super(Arcadia.getPlugin(Arcadia.class).getAPI().getTranslationManager().fetchTranslation("game.electricfloor.name").build(),
                new String[]{"startPosition", "blocks"},
                new SidebarSettings(PlayersLeftSidebar.class,
                WinMethod.LAST_PLAYER_STANDING, 1, 30), gameMap,
                Arcadia.getPlugin(Arcadia.class).getAPI().getTranslationManager().fetchTranslation("game.electricfloor.desc").build());
    }

    @Override
    public void onPreStart() {
        Location spawnLocation = Utils.parseLocation((String) this.getGameMap().fetchSetting("startPosition"));
        for(Player player : Bukkit.getOnlinePlayers()) {
            if(!this.getAPI().getGameManager().isAlive(player)) continue;
            player.teleport(spawnLocation);
            player.setGameMode(GameMode.ADVENTURE);
        }
        List<MaterialData> floorData = new ArrayList<MaterialData>();
        for(String temp : ((String) this.getGameMap().fetchSetting("blocks")).split(",")) {
            floorData.add(Utils.parseMaterialData(temp));
        }
        this.floorOrder = new FloorOrder(floorData);
    }

    @Override
    public void onGameStart() {
        new BukkitRunnable() {
            public void run() {
                if(Arcadia.getPlugin(Arcadia.class).getAPI().getGameManager().getGameState()
                        != GameState.INGAME) {
                    this.cancel();
                    return;
                }
                for(Player player : Bukkit.getOnlinePlayers()) {
                    updateBlock(player, player.getLocation().getBlock().getRelative(BlockFace.DOWN));
                }
            }
        }.runTaskTimer(this.getAPI().getPlugin(), 0, 20L);
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        this.updateBlock(event.getPlayer(), event.getTo().getBlock().getRelative(BlockFace.DOWN));
    }

    private void updateBlock(Player player, Block block) {
        if(this.getAPI().getGameManager().isAlive(player)) {
            if(block.getType() == Material.AIR
                    && block.getRelative(BlockFace.DOWN).getType() != Material.AIR) {
                block = block.getRelative(BlockFace.DOWN);
            }
            MaterialData currentData = floorOrder.currentData(block);
            if(floorOrder.match(block, floorOrder.last())) {
                this.getAPI().getGameManager().setAlive(player, false);
            } else {
                if(floorOrder.contains(block)) {
                    if(!changePending.contains(block)) {
                        final Block finalBlock = block;
                        final MaterialData next = floorOrder.nextData(block);
                        changePending.add(block);
                        new BukkitRunnable() {
                            public void run() {
                                if(changePending.contains(finalBlock)) {
                                    finalBlock.setTypeIdAndData(next.getItemTypeId(), next.getData(), false);
                                    changePending.remove(finalBlock);
                                }
                            }
                        }.runTaskLater(this.getAPI().getPlugin(), 10L);
                    }
                }
            }
        }
    }

    @Override
    public void onGameEnd() {}
}