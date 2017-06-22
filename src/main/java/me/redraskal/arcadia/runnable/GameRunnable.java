package me.redraskal.arcadia.runnable;

import me.redraskal.arcadia.Arcadia;
import me.redraskal.arcadia.ArcadiaAPI;
import me.redraskal.arcadia.api.game.GameState;
import org.bukkit.scheduler.BukkitRunnable;

public class GameRunnable extends BukkitRunnable {

    private final ArcadiaAPI api;
    private int minutes;
    private int seconds;

    public GameRunnable() {
        this.api = Arcadia.getPlugin(Arcadia.class).getAPI();
        this.minutes = api.getGameManager().getCurrentGame().getSidebarSettings().getGameMinutes();
        this.seconds = api.getGameManager().getCurrentGame().getSidebarSettings().getGameSeconds();
        this.runTaskTimer(Arcadia.getPlugin(Arcadia.class), 0, 20L);
    }

    @Override
    public void run() {
        if(api.getGameManager().getGameState() != GameState.INGAME) {
            this.cancel();
            return;
        }
        if(seconds <= 0) {
            if(minutes <= 0) {
                this.cancel();
                api.getGameManager().endGame();
            } else {
                minutes--;
                seconds = 59;
            }
        } else {
            seconds--;
        }
        api.getGameManager().getCurrentGame().getSidebar().updateDisplayName(minutes, seconds);
    }
}