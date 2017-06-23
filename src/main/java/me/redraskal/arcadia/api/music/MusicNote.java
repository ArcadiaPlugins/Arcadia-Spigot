package me.redraskal.arcadia.api.music;

import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class MusicNote {

    private final Sound sound;
    private final float volume;
    private final float pitch;

    public MusicNote(Sound sound, float volume, float pitch) {
        this.sound = sound;
        this.volume = volume;
        this.pitch = pitch;
    }

    public MusicNote(Sound sound) {
        this(sound, 1f, 1f);
    }

    public MusicNote(Sound sound, float volume) {
        this(sound, volume, 1f);
    }

    public Sound getSound() {
        return this.sound;
    }

    public float getVolume() {
        return this.volume;
    }

    public float getPitch() {
        return this.pitch;
    }

    public void play(Player... players) {
        for(Player player : players) {
            player.getWorld().playSound(player.getLocation(), sound, volume, pitch);
        }
    }
}