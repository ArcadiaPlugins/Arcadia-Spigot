package me.redraskal.arcadia.runnable;

import me.redraskal.arcadia.Arcadia;
import me.redraskal.arcadia.ArcadiaAPI;
import me.redraskal.arcadia.Utils;
import me.redraskal.arcadia.api.game.GameState;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.boss.BarColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class GameSwitchRunnable extends BukkitRunnable {

    private final ArcadiaAPI api;
    private int seconds = 6;

    public GameSwitchRunnable() {
        this.api = Arcadia.getPlugin(Arcadia.class).getAPI();
        api.getGameManager().getMainBossBar().setColor(BarColor.YELLOW);
        this.runTaskTimer(Arcadia.getPlugin(Arcadia.class), 0, 20L);
    }

    @Override
    public void run() {
        if(api.getGameManager().getGameState() != GameState.FINISHED) {
            this.cancel();
            return;
        }
        if(seconds <= 0) {
            this.cancel();
            api.getGameManager().nullifyGame();
            Arcadia.getPlugin(Arcadia.class).nextGameInRotation(false);
            return;
        } else {
            seconds--;
            if(seconds <= 3) {
                if(seconds > 0) {
                    for(Player player : Bukkit.getOnlinePlayers()) {
                        player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1f, 0.9f);
                    }
                } else {
                    for(Player player : Bukkit.getOnlinePlayers()) {
                        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_PLING, 1f, 1.1f);
                    }
                }
            }
            if(seconds > 0) {
                api.getGameManager().getMainBossBar().setTitle(ChatColor.translateAlternateColorCodes('&', "&6&lSwitching Game In: &c&l" + Utils.formatTimeFancy(0, seconds)));
                api.getGameManager().getMainBossBar().setProgress(1D-(double)seconds/6D);
            } else {
                api.getGameManager().getMainBossBar().setTitle(ChatColor.translateAlternateColorCodes('&', "&6&lLoading game..."));
                api.getGameManager().getMainBossBar().setProgress(1D);
            }
        }
        api.getGameManager().getCurrentGame().getSidebar().updateDisplayName(0, seconds);
    }
}