package me.redraskal.arcadia.runnable;

import me.redraskal.arcadia.Arcadia;
import me.redraskal.arcadia.ArcadiaAPI;
import me.redraskal.arcadia.api.game.GameState;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class GameStartRunnable extends BukkitRunnable {

    private final ArcadiaAPI api;
    private int countdown = 11;

    public GameStartRunnable() {
        this.api = Arcadia.getPlugin(Arcadia.class).getAPI();
        this.runTaskTimer(Arcadia.getPlugin(Arcadia.class), 0, 20L);
    }

    @Override
    public void run() {
        if(countdown <= 1) {
            this.cancel();
            for(Player player : Bukkit.getOnlinePlayers()) {
                player.sendTitle(ChatColor.GREEN + "* GO *",
                        ChatColor.GREEN + "✪ " + api.getGameManager().getCurrentGame().getName() + " ✪",
                        0, 20, 20);
            }
            this.api.getGameManager().setGameState(GameState.INGAME);
            this.api.getGameManager().getCurrentGame().onGameStart();
            new GameRunnable();
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
            this.api.getGameManager().getCurrentGame().getSidebar().updateDisplayName(0, countdown);
        }
    }
}