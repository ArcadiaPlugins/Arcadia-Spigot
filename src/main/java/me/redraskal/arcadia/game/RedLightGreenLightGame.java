package me.redraskal.arcadia.game;

import me.redraskal.arcadia.Utils;
import me.redraskal.arcadia.api.game.BaseGame;
import me.redraskal.arcadia.api.map.GameMap;
import me.redraskal.arcadia.api.scoreboard.SidebarSettings;
import me.redraskal.arcadia.api.scoreboard.WinMethod;
import me.redraskal.arcadia.api.scoreboard.defaults.PlayersLeftSidebar;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class RedLightGreenLightGame extends BaseGame {

    public RedLightGreenLightGame(GameMap gameMap) {
        super("Red Light, Green Light", new String[]{"startPosition"}, new SidebarSettings(PlayersLeftSidebar.class,
                        WinMethod.LAST_PLAYER_STANDING, 1, 30), gameMap,
                "Run when the light is green. Stop when the light is red.");
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
    public void onGameStart() {}

    @Override
    public void onGameEnd() {}
}