package me.redraskal.arcadia.api.music.defaults;

import me.redraskal.arcadia.api.music.MusicCallback;
import me.redraskal.arcadia.api.music.MusicNote;
import me.redraskal.arcadia.api.music.MusicPlayer;
import me.redraskal.arcadia.api.music.MusicSequence;
import org.bukkit.Sound;

import java.util.ArrayList;
import java.util.List;

public class MusicalMinecartsMusic {

    private static List<MusicPlayer> musicPlaying = new ArrayList<>();
    public static void stopMusic() {
        musicPlaying.forEach(musicPlayer -> {
            musicPlayer.cancel();
        });
        musicPlaying.clear();
    }

    public MusicalMinecartsMusic() {
        MusicSequence musicSequence = new MusicSequence();
        float add = 0f;
        int ticks = 0;
        for(int i=0; i<8; i++) {
            musicSequence.addSound(new MusicNote(Sound.BLOCK_NOTE_HARP, 1.2f, 0.5f+add), ticks);
            musicSequence.addSound(new MusicNote(Sound.BLOCK_NOTE_FLUTE, 1.5f, 0.7f+add), ticks);
            ticks+=10;
            musicSequence.addSound(new MusicNote(Sound.BLOCK_NOTE_GUITAR, 1.5f, 0.5f), ticks-5);
            add+=0.11111115;
        }
        MusicPlayer musicPlayer = musicSequence.play();
        musicPlaying.add(musicPlayer);
        musicPlayer.setCallback(new MusicCallback() {
            @Override
            public void onFinish() {
                if(musicPlaying.contains(musicPlayer)) musicPlaying.remove(musicPlayer);
                new MusicalMinecartsMusic();
            }
        });
    }
}