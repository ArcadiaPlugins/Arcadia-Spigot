package me.redraskal.arcadia.runnable;

import me.redraskal.arcadia.Arcadia;
import me.redraskal.arcadia.ArcadiaAPI;
import me.redraskal.arcadia.api.game.GameState;
import me.redraskal.arcadia.api.game.event.GameTickEvent;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

public class GameTickRunnable extends BukkitRunnable {

    private final ArcadiaAPI api;
    private int ticks = 0;
    private int ticksInSecond = 0;

    public GameTickRunnable() {
        this.api = Arcadia.getPlugin(Arcadia.class).getAPI();
        this.runTaskTimer(Arcadia.getPlugin(Arcadia.class), 0, 1L);
    }

    @Override
    public void run() {
        if(api.getGameManager().getGameState() != GameState.INGAME) {
            this.cancel();
            return;
        }
        Bukkit.getServer().getPluginManager().callEvent(new GameTickEvent(ticks, ticksInSecond));
        ticks++;
        if(ticksInSecond >= 20) {
            ticksInSecond = 0;
        } else {
            ticksInSecond++;
        }
    }
}