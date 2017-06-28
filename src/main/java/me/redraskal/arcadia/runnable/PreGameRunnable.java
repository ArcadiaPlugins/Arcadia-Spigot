package me.redraskal.arcadia.runnable;

import me.redraskal.arcadia.Arcadia;
import me.redraskal.arcadia.ArcadiaAPI;
import me.redraskal.arcadia.Utils;
import me.redraskal.arcadia.api.game.event.GameLoadEvent;
import me.redraskal.arcadia.api.scoreboard.defaults.ScoreSidebar;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

public class PreGameRunnable {

    public PreGameRunnable() {
        final ArcadiaAPI api = Arcadia.getPlugin(Arcadia.class).getAPI();
        for(Player player : Bukkit.getOnlinePlayers()) {
            Utils.resetPlayer(player);
            if(api.getGameManager().isSpectating(player)) {
                player.teleport(Utils.parseLocation((String) api.getGameManager().getCurrentGame().getGameMap().fetchSetting("spectatorLocation")));
                player.setGameMode(GameMode.SPECTATOR);
            } else {
                player.setGameMode(GameMode.ADVENTURE);
                api.getGameManager().setAlive(player, true);
            }
        }
        api.getGameManager().getCurrentGame().onPreStart();
        if(api.getGameManager().getCurrentGame().getSidebar() instanceof ScoreSidebar) {
            ScoreSidebar scoreSidebar = (ScoreSidebar) api.getGameManager().getCurrentGame().getSidebar();
            if(scoreSidebar.fixScoreboard) {
                for(Player player : Bukkit.getOnlinePlayers()) {
                    if(!api.getGameManager().isAlive(player)) continue;
                    scoreSidebar.setScore(player, 0);
                }
            }
        }
        new GameStartRunnable();
        Bukkit.getOnlinePlayers().forEach(player -> {
            if(!api.getGameManager().isAlive(player)) api.getGameManager().getCurrentGame().spectatorCache.add(player);
        });
        Bukkit.getServer().getPluginManager().callEvent(new GameLoadEvent());
    }
}