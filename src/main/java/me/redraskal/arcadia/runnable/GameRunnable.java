package me.redraskal.arcadia.runnable;

import me.redraskal.arcadia.Arcadia;
import me.redraskal.arcadia.ArcadiaAPI;
import me.redraskal.arcadia.api.game.GameState;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class GameRunnable extends BukkitRunnable {

    private final ArcadiaAPI api;
    private int minutes;
    private int seconds;

    public GameRunnable() {
        this.api = Arcadia.getPlugin(Arcadia.class).getAPI();
        this.minutes = api.getGameManager().getCurrentGame().getSidebarSettings().getGameMinutes();
        this.seconds = api.getGameManager().getCurrentGame().getSidebarSettings().getGameSeconds();
        new GameTickRunnable();
        this.runTaskTimer(Arcadia.getPlugin(Arcadia.class), 0, 20L);
    }

    @Override
    public void run() {
        if(api.getGameManager().getGameState() != GameState.INGAME) {
            this.cancel();
            return;
        }
        if(seconds <= 1 && minutes <= 0) {
            this.cancel();
            api.getGameManager().getCurrentGame().endGame();
            api.getGameManager().endGame();
            new GameSwitchRunnable();
            return;
        }
        if(seconds <= 0) {
            minutes--;
            seconds = 59;
        } else {
            seconds--;
        }
        if(minutes == 0 && seconds <= 3 && seconds > 0) {
            for(Player player : Bukkit.getOnlinePlayers()) {
                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BASS, 1f, 1f);
            }
        }
        api.getGameManager().getCurrentGame().getSidebar().updateDisplayName(minutes, seconds);
    }
}