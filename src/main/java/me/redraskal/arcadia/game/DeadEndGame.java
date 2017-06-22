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
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class DeadEndGame extends BaseGame {

    public DeadEndGame(GameMap gameMap) {
        super("Dead End", new String[]{"startPosition", "floorLevel"}, new SidebarSettings(PlayersLeftSidebar.class, 1, 30), gameMap,
            "Move quickly! The floor is falling out from under you. Last player standing wins!");
    }

    @Override
    public void onPreStart() {
        Location spawnLocation = Utils.parseLocation((String) this.getGameMap().fetchSetting("startPosition"));
        PotionEffect potionEffect = new PotionEffect(PotionEffectType.JUMP, Integer.MAX_VALUE, 127);
        for(Player player : Bukkit.getOnlinePlayers()) {
            if(!this.getAPI().getGameManager().isAlive(player)) continue;
            player.teleport(spawnLocation);
            player.setGameMode(GameMode.ADVENTURE);
            player.addPotionEffect(potionEffect, true);
        }
    }

    @Override
    public void onGameStart() {
        //TODO
    }

    @Override
    public void onGameEnd() {
        //TODO
    }
}