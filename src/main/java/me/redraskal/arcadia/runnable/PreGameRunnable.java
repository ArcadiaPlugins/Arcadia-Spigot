package me.redraskal.arcadia.runnable;

import me.redraskal.arcadia.Arcadia;
import me.redraskal.arcadia.ArcadiaAPI;
import me.redraskal.arcadia.Utils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

public class PreGameRunnable {

    public PreGameRunnable() {
        final ArcadiaAPI api = Arcadia.getPlugin(Arcadia.class).getAPI();
        for(Player player : Bukkit.getOnlinePlayers()) {
            Utils.resetPlayer(player);
            if(api.getGameManager().isSpectating(player)) {
                player.setGameMode(GameMode.SPECTATOR);
            } else {
                player.setGameMode(GameMode.ADVENTURE);
                api.getGameManager().setAlive(player, true);
            }
        }
    }
}