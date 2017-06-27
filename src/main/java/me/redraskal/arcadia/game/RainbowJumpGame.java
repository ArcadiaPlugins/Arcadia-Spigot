package me.redraskal.arcadia.game;

import me.redraskal.arcadia.Arcadia;
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

public class RainbowJumpGame extends BaseGame {

    private Location startPosition;
    private Location targetPosition;
    private String towards;
    private Cuboid glass;

    public RainbowJumpGame(GameMap gameMap) {
        super(Arcadia.getPlugin(Arcadia.class).getAPI().getTranslationManager().fetchTranslation("game.rainbowjump.name").build(),
                new String[]{"startPosition", "floorLevel", "glassBoundsA", "glassBoundsB", "targetPosition", "targetTowards"},
                new SidebarSettings(RelativeDistanceSidebar.class,
                        WinMethod.HIGHEST_SCORE, 1, 0), gameMap,
                Arcadia.getPlugin(Arcadia.class).getAPI().getTranslationManager().fetchTranslation("game.rainbowjump.desc").build());
        this.killOnMapExit = false;
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
        glass = new Cuboid(Utils.parseLocation((String) this.getGameMap().fetchSetting("glassBoundsA")),
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
            if(this.getSidebar().getSidebar().getScore(event.getPlayer().getName()).getScore() >= -1) {
                this.endGame();
            }
            if(event.getTo().getY() <= Integer.valueOf((String) this.getGameMap().fetchSetting("floorLevel"))) {
                event.setTo(startPosition);
            }
        }
    }

    @Override
    public void onGameEnd() {}
}