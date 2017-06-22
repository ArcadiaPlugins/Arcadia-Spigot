package me.redraskal.arcadia.game;

import me.redraskal.arcadia.Cuboid;
import me.redraskal.arcadia.Utils;
import me.redraskal.arcadia.api.game.BaseGame;
import me.redraskal.arcadia.api.map.GameMap;
import me.redraskal.arcadia.api.scoreboard.SidebarSettings;
import me.redraskal.arcadia.api.scoreboard.defaults.DistanceSidebar;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;

public class MineFieldGame extends BaseGame {

    public MineFieldGame(GameMap gameMap) {
        super("Minefield", new String[]{"startPosition", "minefieldBoundsA", "minefieldBoundsB", "glassBoundsA", "glassBoundsB"}, new SidebarSettings(DistanceSidebar.class, 1, 30), gameMap,
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
        ((DistanceSidebar) this.getSidebar()).setTarget(Utils.parseLocation((String) this.getGameMap().fetchSetting("targetPosition")));
        //TODO
    }

    @Override
    public void onGameStart() {
        Cuboid glass = new Cuboid(Utils.parseLocation((String) this.getGameMap().fetchSetting("glassBoundsA")),
            Utils.parseLocation((String) this.getGameMap().fetchSetting("glassBoundsB")));
        while(glass.iterator().hasNext()) {
            glass.iterator().next().setType(Material.AIR);
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if(this.getAPI().getGameManager().isAlive(event.getPlayer())) {
            if(event.getTo().getBlock().getType() == Material.STONE_PLATE) {
                event.getTo().getBlock().setType(Material.AIR);
                event.getTo().getWorld().playSound(event.getTo(), Sound.ENTITY_GENERIC_EXPLODE, 1f, 1f);
                this.getAPI().getGameManager().setAlive(event.getPlayer(), false);
                return;
            }
            if(event.getTo().getBlock().getType() == Material.DIAMOND_BLOCK) {
                for(Player player : Bukkit.getOnlinePlayers()) {
                    if(this.getAPI().getGameManager().isAlive(player)) this.getAPI().getGameManager().setAlive(player, false);
                }
            }
        }
    }

    @Override
    public void onGameEnd() {}
}