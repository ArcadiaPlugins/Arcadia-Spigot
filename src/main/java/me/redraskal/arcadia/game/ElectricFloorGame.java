package me.redraskal.arcadia.game;

import me.redraskal.arcadia.Utils;
import me.redraskal.arcadia.api.game.BaseGame;
import me.redraskal.arcadia.api.map.GameMap;
import me.redraskal.arcadia.api.scoreboard.SidebarSettings;
import me.redraskal.arcadia.api.scoreboard.defaults.PlayersLeftSidebar;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class ElectricFloorGame extends BaseGame {

    public ElectricFloorGame(GameMap gameMap) {
        super("Electric Floor", new String[]{"startPosition"},
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
    }

    @Override
    public void onGameStart() {
        //TODO: Floor Disintegration Runnable
    }

    @Override
    public void onGameEnd() {}
}