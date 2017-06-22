package me.redraskal.arcadia.game;

import me.redraskal.arcadia.Arcadia;
import me.redraskal.arcadia.Utils;
import me.redraskal.arcadia.api.game.BaseGame;
import me.redraskal.arcadia.api.map.GameMap;
import me.redraskal.arcadia.api.scoreboard.SidebarSettings;
import me.redraskal.arcadia.api.scoreboard.defaults.PlayersLeftSidebar;
import me.redraskal.arcadia.game.electricfloor.FloorOrder;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
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
        super("Electric Floor", new String[]{"startPosition", "blocks"},
                new SidebarSettings(PlayersLeftSidebar.class, 1, 30), gameMap,
                "The platform is being electrified! Be the last player standing to win!");
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
    public void onGameStart() {}

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if(this.getAPI().getGameManager().isAlive(event.getPlayer())) {
            final Block block = event.getTo().getBlock().getRelative(BlockFace.DOWN);
            MaterialData currentData = floorOrder.currentData(block);
            if(floorOrder.match(block, floorOrder.last())) {
                this.getAPI().getGameManager().setAlive(event.getPlayer(), false);
            } else {
                if(floorOrder.contains(block)) {
                    if(!changePending.contains(block)) {
                        final MaterialData next = floorOrder.nextData(block);
                        changePending.add(block);
                        new BukkitRunnable() {
                            public void run() {
                                if(changePending.contains(block)) {
                                    block.setTypeIdAndData(next.getItemTypeId(), next.getData(), false);
                                    changePending.remove(block);
                                }
                            }
                        }.runTaskLater(Arcadia.getPlugin(Arcadia.class), 10L);
                    }
                }
            }
        }
    }

    @Override
    public void onGameEnd() {}
}