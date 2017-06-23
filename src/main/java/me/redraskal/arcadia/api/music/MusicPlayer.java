package me.redraskal.arcadia.api.music;

import me.redraskal.arcadia.Arcadia;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class MusicPlayer extends BukkitRunnable {

    private final MusicSequence musicSequence;
    private final Player[] players;
    private int ticks = 0;

    public MusicPlayer(MusicSequence musicSequence, Player... players) {
        this.musicSequence = musicSequence;
        this.players = players;
        this.runTaskTimer(Arcadia.getPlugin(Arcadia.class), 0, 1L);
    }

    @Override
    public void run() {
        if(musicSequence.getLastTick() < ticks) {
            this.cancel();
            return;
        }
        for(MusicNote musicNote : musicSequence.getSounds(ticks)) {
            musicNote.play(players);
        }
        ticks++;
    }
}