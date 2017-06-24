package me.redraskal.arcadia.game;

import me.redraskal.arcadia.Cuboid;
import me.redraskal.arcadia.Utils;
import me.redraskal.arcadia.api.game.BaseGame;
import me.redraskal.arcadia.api.map.GameMap;
import me.redraskal.arcadia.api.scoreboard.SidebarSettings;
import me.redraskal.arcadia.api.scoreboard.WinMethod;
import me.redraskal.arcadia.api.scoreboard.defaults.DistanceSidebar;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.Iterator;

public class RainbowJumpGame extends BaseGame {

    private Material winBlock;
    private Location spawnLocation;

    public RainbowJumpGame(GameMap gameMap) {
        super("Rainbow Jump", new String[]{"startPosition", "floorLevel", "glassBoundsA", "glassBoundsB", "targetPosition", "winBlock"},
                new SidebarSettings(DistanceSidebar.class,
                        WinMethod.LAST_PLAYER_STANDING, 1, 0), gameMap,
                "Jump to the finish!");
        this.killOnMapExit = false;
    }

    @Override
    public void onPreStart() {
        this.spawnLocation = Utils.parseLocation((String) this.getGameMap().fetchSetting("startPosition"));
        for(Player player : Bukkit.getOnlinePlayers()) {
            if(!this.getAPI().getGameManager().isAlive(player)) continue;
            player.teleport(spawnLocation);
            player.setGameMode(GameMode.ADVENTURE);
        }
        ((DistanceSidebar) this.getSidebar()).setTarget(Utils.parseLocation((String) this.getGameMap().fetchSetting("targetPosition")));
        this.winBlock = Material.getMaterial((String) this.getGameMap().fetchSetting("winBlock"));
    }

    @Override
    public void onGameStart() {
        Cuboid glass = new Cuboid(Utils.parseLocation((String) this.getGameMap().fetchSetting("glassBoundsA")),
                Utils.parseLocation((String) this.getGameMap().fetchSetting("glassBoundsB")));
        Iterator<Block> blocks = glass.iterator();
        while(blocks.hasNext()) {
            blocks.next().setType(Material.AIR);
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if(this.getAPI().getGameManager().isAlive(event.getPlayer())) {
            if(event.getTo().getBlock().getRelative(BlockFace.DOWN).getType() == winBlock) {
                this.endGame();
            }
            if(event.getTo().getY() <= Integer.valueOf((String) this.getGameMap().fetchSetting("floorLevel"))) {
                event.setTo(spawnLocation);
            }
        }
    }

    @Override
    public void onGameEnd() {}
}