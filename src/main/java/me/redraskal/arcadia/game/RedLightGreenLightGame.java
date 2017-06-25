package me.redraskal.arcadia.game;

import me.redraskal.arcadia.Utils;
import me.redraskal.arcadia.api.game.BaseGame;
import me.redraskal.arcadia.api.map.GameMap;
import me.redraskal.arcadia.api.scoreboard.SidebarSettings;
import me.redraskal.arcadia.api.scoreboard.WinMethod;
import me.redraskal.arcadia.api.scoreboard.defaults.RelativeDistanceSidebar;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class RedLightGreenLightGame extends BaseGame {

    private Location startPosition;
    private Location targetPosition;
    private String towards;

    public RedLightGreenLightGame(GameMap gameMap) {
        super("Red Light, Green Light", new String[]{"startPosition", "targetPosition", "targetTowards"},
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
    }

    @Override
    public void onGameStart() {}

    @Override
    public void onGameEnd() {}
}