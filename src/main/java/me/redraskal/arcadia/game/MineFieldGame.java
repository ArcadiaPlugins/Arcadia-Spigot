package me.redraskal.arcadia.game;

import me.redraskal.arcadia.Cuboid;
import me.redraskal.arcadia.Utils;
import me.redraskal.arcadia.api.game.BaseGame;
import me.redraskal.arcadia.api.map.GameMap;
import me.redraskal.arcadia.api.scoreboard.SidebarSettings;
import me.redraskal.arcadia.api.scoreboard.WinMethod;
import me.redraskal.arcadia.api.scoreboard.defaults.RelativeDistanceSidebar;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.Iterator;
import java.util.Random;

public class MineFieldGame extends BaseGame {

    private Material winBlock;
    private Cuboid glass;

    public MineFieldGame(GameMap gameMap) {
        super("Minefield", new String[]{"startPosition", "minefieldBoundsA", "minefieldBoundsB", "glassBoundsA", "glassBoundsB", "targetPosition", "targetTowards", "winBlock"},
                new SidebarSettings(RelativeDistanceSidebar.class,
                WinMethod.HIGHEST_SCORE, 1, 30), gameMap,
                "Race through the minefield to the finish!");
    }

    @Override
    public void onPreStart() {
        Location spawnLocation = Utils.parseLocation((String) this.getGameMap().fetchSetting("startPosition"));
        for(Player player : Bukkit.getOnlinePlayers()) {
            if(!this.getAPI().getGameManager().isAlive(player)) continue;
            player.teleport(spawnLocation);
            player.setGameMode(GameMode.ADVENTURE);
        }
        ((RelativeDistanceSidebar) this.getSidebar()).setTarget(
            Utils.parseLocation((String) this.getGameMap().fetchSetting("targetPosition")),
            (String) this.getGameMap().fetchSetting("targetTowards"));
        this.winBlock = Material.getMaterial((String) this.getGameMap().fetchSetting("winBlock"));
        Cuboid minefield = new Cuboid(Utils.parseLocation((String) this.getGameMap().fetchSetting("minefieldBoundsA")),
                Utils.parseLocation((String) this.getGameMap().fetchSetting("minefieldBoundsB")));
        Iterator<Block> blocks = minefield.iterator();
        Random random = new Random();
        blocks.forEachRemaining(block -> {
            Block next = blocks.next();
            if(next.getRelative(BlockFace.UP).getType() == Material.AIR) {
                if(random.nextFloat() < 0.6F) {
                    next.setType(Material.STONE_PLATE);
                }
            }
        });
        this.glass = new Cuboid(Utils.parseLocation((String) this.getGameMap().fetchSetting("glassBoundsA")),
                Utils.parseLocation((String) this.getGameMap().fetchSetting("glassBoundsB")));
        Iterator<Block> glassBlocks = glass.iterator();
        while(glassBlocks.hasNext()) {
            glassBlocks.next().setType(Material.GLASS);
        }
    }

    @Override
    public void onGameStart() {
        Iterator<Block> glassBlocks = glass.iterator();
        while(glassBlocks.hasNext()) {
            glassBlocks.next().setType(Material.AIR);
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if(this.getAPI().getGameManager().isAlive(event.getPlayer())) {
            if(event.getTo().getBlock().getType() == Material.STONE_PLATE) {
                event.getTo().getBlock().setType(Material.AIR);
                event.getTo().getWorld().spigot().playEffect(event.getTo(), Effect.FLAME,
                        0, 0, 1, 1, 1, 0, 5, 15);
                event.getTo().getWorld().spigot().playEffect(event.getTo(), Effect.EXPLOSION,
                    0, 0, 1, 1, 1, 0, 6, 15);
                event.getTo().getWorld().playSound(event.getTo(), Sound.ENTITY_GENERIC_EXPLODE, 5f, 1f);
                event.getPlayer().teleport(Utils.parseLocation((String) this.getGameMap().fetchSetting("startPosition")));
                return;
            }
            if(event.getTo().getBlock().getRelative(BlockFace.DOWN).getType() == winBlock) {
                this.endGame();
            }
        }
    }

    @Override
    public void onGameEnd() {}
}