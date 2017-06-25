package me.redraskal.arcadia.game;

import me.redraskal.arcadia.Cuboid;
import me.redraskal.arcadia.Utils;
import me.redraskal.arcadia.api.game.BaseGame;
import me.redraskal.arcadia.api.map.GameMap;
import me.redraskal.arcadia.api.scoreboard.SidebarSettings;
import me.redraskal.arcadia.api.scoreboard.WinMethod;
import me.redraskal.arcadia.api.scoreboard.defaults.RelativeDistanceSidebar;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.Iterator;

public class RedLightGreenLightGame extends BaseGame {

    private Location startPosition;
    private Location targetPosition;
    private String towards;
    private Cuboid glass;

    public RedLightGreenLightGame(GameMap gameMap) {
        super("Red Light, Green Light", new String[]{"startPosition", "targetPosition", "targetTowards", "glassBoundsA", "glassBoundsB"},
                new SidebarSettings(RelativeDistanceSidebar.class,
                    WinMethod.HIGHEST_SCORE, 1, 30), gameMap,
                "Run when the light is green. Stop when the light is red.");
    }

    @Override
    public void onPreStart() {
        this.startPosition = Utils.parseLocation((String) this.getGameMap().fetchSetting("startPosition"));
        for(Player player : Bukkit.getOnlinePlayers()) {
            if(!this.getAPI().getGameManager().isAlive(player)) continue;
            player.teleport(startPosition);
            player.setGameMode(GameMode.ADVENTURE);
        }
        this.targetPosition = Utils.parseLocation((String) this.getGameMap().fetchSetting("targetPosition"));
        this.towards = (String) this.getGameMap().fetchSetting("targetTowards");
        ((RelativeDistanceSidebar) this.getSidebar()).setTarget(targetPosition, towards);
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
        //TODO: Fun runnable system
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if(this.getAPI().getGameManager().isAlive(event.getPlayer())) {
            if(this.getSidebar().getSidebar().getScore(event.getPlayer().getName()).getScore() >= -1) {
                this.endGame();
            }
        }
    }

    @Override
    public void onGameEnd() {}
}