package me.redraskal.arcadia.listener;

import me.redraskal.arcadia.Arcadia;
import me.redraskal.arcadia.ArcadiaAPI;
import me.redraskal.arcadia.Utils;
import me.redraskal.arcadia.api.game.GameState;
import me.redraskal.arcadia.api.translation.Translation;
import me.redraskal.arcadia.runnable.GameSwitchRunnable;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class ConnectionListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        ArcadiaAPI api = Arcadia.getPlugin(Arcadia.class).getAPI();
        Translation translation = api.getTranslationManager().fetchTranslation("common.join-message", event.getPlayer());
        if(translation != null) {
            final String message = translation.build(event.getPlayer().getName());
            if(!message.isEmpty()) {
                event.setJoinMessage(message);
            } else {
                event.setJoinMessage(null);
            }
        }
        Utils.resetPlayer(event.getPlayer());
        api.getGameManager().setAlive(event.getPlayer(), false);
        api.getGameManager().setSpectating(event.getPlayer(), false);
        if(api.getGameManager().getCurrentGame() != null) {
            event.getPlayer().setScoreboard(api.getGameManager().getCurrentGame().getSidebar().getSidebar().getScoreboard());
            api.getTranslationManager().sendTranslation("ui.game-title", event.getPlayer(), api.getGameManager().getCurrentGame().getName());
            api.getTranslationManager().sendTranslation("ui.game-description", event.getPlayer(), api.getGameManager().getCurrentGame().getDescription());
            api.getTranslationManager().sendTranslation("ui.dont-leave", event.getPlayer());
            api.getGameManager().getCurrentGame().spectatorCache.add(event.getPlayer());
        }
        if(Bukkit.getOnlinePlayers().size() == 1) {
            if(api.getGameManager().getGameState() == GameState.STARTING) {
                api.getGameManager().endGame();
                new GameSwitchRunnable();
            }
        }
        event.getPlayer().setCollidable(true);
        api.getGameManager().getMainBossBar().addPlayer(event.getPlayer());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        ArcadiaAPI api = Arcadia.getPlugin(Arcadia.class).getAPI();
        Translation translation = api.getTranslationManager().fetchTranslation("common.leave-message", event.getPlayer());
        if(translation != null) {
            final String message = translation.build(event.getPlayer().getName());
            if(!message.isEmpty()) {
                event.setQuitMessage(message);
            } else {
                event.setQuitMessage(null);
            }
        }
        if(api.getGameManager().isAlive(event.getPlayer())) api.getGameManager().setAlive(event.getPlayer(), false);
        if(api.getGameManager().isSpectating(event.getPlayer())) api.getGameManager().setSpectating(event.getPlayer(), false);
    }
}