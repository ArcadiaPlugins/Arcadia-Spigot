package me.redraskal.arcadia.api.music.defaults;

import me.redraskal.arcadia.api.music.MusicNote;
import me.redraskal.arcadia.api.music.MusicSequence;
import org.bukkit.Sound;

public class EndGameMusic {

    public EndGameMusic() {
        MusicSequence musicSequence = new MusicSequence();
        float add = 0f;
        int ticks = 0;
        for(int i=0; i<15; i++) {
            musicSequence.addSound(new MusicNote(Sound.BLOCK_NOTE_HARP, 1.5f, 0.24444445f+add), ticks);
            ticks++;
            musicSequence.addSound(new MusicNote(Sound.BLOCK_NOTE_HARP, 1.5f, 0.24444445f+add), ticks);
            ticks++;
            add+=0.11111115;
        }
        musicSequence.play();
    }
}