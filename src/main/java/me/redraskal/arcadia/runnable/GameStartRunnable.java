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

public class GameStartRunnable extends BukkitRunnable {

    private final ArcadiaAPI api;
    private int countdown = 11;

    public GameStartRunnable() {
        this.api = Arcadia.getPlugin(Arcadia.class).getAPI();
        api.getTranslationManager().sendTranslation("ui.game-title", api.getGameManager().getCurrentGame().getName());
        api.getTranslationManager().sendTranslation("ui.game-description", api.getGameManager().getCurrentGame().getDescription());
        for(Player player : Bukkit.getOnlinePlayers()) {
            if(api.getGameManager().isSpectating(player)) {
                api.getTranslationManager().sendTranslation("ui.game-spectating", player);
            } else {
                api.getTranslationManager().sendTranslation("ui.game-quit-playing", player);
            }
        }
        api.getGameManager().getMainBossBar().setColor(BarColor.BLUE);
        this.runTaskTimer(Arcadia.getPlugin(Arcadia.class), 0, 20L);
    }

    @Override
    public void run() {
        if(api.getGameManager().getGameState() != GameState.STARTING) {
            this.cancel();
            return;
        }
        if(countdown <= 1) {
            this.cancel();
            Bukkit.getServer().getPluginManager().registerEvents(api.getGameManager().getCurrentGame(), Arcadia.getPlugin(Arcadia.class));
            for(Player player : Bukkit.getOnlinePlayers()) {
                player.sendTitle(ChatColor.GREEN + "* GO *",
                        ChatColor.GREEN + "✪ " + api.getGameManager().getCurrentGame().getName() + " ✪",
                        0, 20, 20);
                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_PLING, 1f, 1.2f);
            }
            this.api.getGameManager().setGameState(GameState.INGAME);
            this.api.getGameManager().getCurrentGame().onGameStart();
            new GameRunnable();
            if(api.getMapRegistry().oldWorld != null) {
                api.getMapRegistry().unloadWorld(api.getMapRegistry().oldWorld);
                api.getMapRegistry().oldWorld = null;
            }
        } else {
            countdown--;
            ChatColor color = ChatColor.RED;
            if(countdown <= 7) color = ChatColor.YELLOW;
            if(countdown <= 3) color = ChatColor.GREEN;
            for(Player player : Bukkit.getOnlinePlayers()) {
                player.sendTitle(color + "* " + countdown + " *",
                    ChatColor.GREEN + "✪ " + api.getGameManager().getCurrentGame().getName() + " ✪",
                         0, 40, 0);
            }
            if(countdown <= 3) {
                for(Player player : Bukkit.getOnlinePlayers()) {
                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_PLING, 1f, 0.7f);
                }
            }
            api.getGameManager().getMainBossBar().setTitle(ChatColor.translateAlternateColorCodes('&', "&6&lStarting Game In: &c&l" + Utils.formatTimeFancy(0, countdown)));
            api.getGameManager().getMainBossBar().setProgress(1D-(double)countdown/10D);
            this.api.getGameManager().getCurrentGame().getSidebar().updateDisplayName(0, countdown);
        }
    }
}