package me.redraskal.arcadia.listener;

import me.redraskal.arcadia.Arcadia;
import me.redraskal.arcadia.ArcadiaAPI;
import me.redraskal.arcadia.Utils;
import me.redraskal.arcadia.api.game.GameState;
import me.redraskal.arcadia.runnable.GameSwitchRunnable;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class ConnectionListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        event.setJoinMessage(null);
        ArcadiaAPI api = Arcadia.getPlugin(Arcadia.class).getAPI();
        Utils.resetPlayer(event.getPlayer());
        api.getGameManager().setAlive(event.getPlayer(), false);
        api.getGameManager().setSpectating(event.getPlayer(), false);
        if(api.getGameManager().getCurrentGame() != null) {
            event.getPlayer().setScoreboard(api.getGameManager().getCurrentGame().getSidebar().getSidebar().getScoreboard());
            event.getPlayer().sendMessage(ChatColor.GREEN + "=== " + ChatColor.BOLD + api.getGameManager().getCurrentGame().getName() + ChatColor.GREEN + " ===");
            for(String line : api.getGameManager().getCurrentGame().getDescription()) event.getPlayer().sendMessage(ChatColor.GOLD + line);
            event.getPlayer().sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "DON'T LEAVE! " + ChatColor.GRAY + "The next game will start shortly.");
        }
        if(Bukkit.getOnlinePlayers().size() == 1) {
            if(api.getGameManager().getGameState() == GameState.STARTING) {
                api.getGameManager().endGame();
                new GameSwitchRunnable();
            }
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        event.setQuitMessage(null);
        ArcadiaAPI api = Arcadia.getPlugin(Arcadia.class).getAPI();
        if(api.getGameManager().isAlive(event.getPlayer())) api.getGameManager().setAlive(event.getPlayer(), false);
        if(api.getGameManager().isSpectating(event.getPlayer())) api.getGameManager().setSpectating(event.getPlayer(), false);
    }
}