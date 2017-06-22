package me.redraskal.arcadia.runnable;

import me.redraskal.arcadia.Arcadia;
import org.bukkit.scheduler.BukkitRunnable;

public class GameStartRunnable extends BukkitRunnable {

    public GameStartRunnable() {
        this.runTaskTimer(Arcadia.getPlugin(Arcadia.class), 0, 20L);
    }

    @Override
    public void run() {
        //TODO
    }
}