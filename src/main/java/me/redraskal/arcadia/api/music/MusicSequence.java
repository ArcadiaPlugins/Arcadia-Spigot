package me.redraskal.arcadia.api.music;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MusicSequence {

    private Map<Integer, List<MusicNote>> sounds = new HashMap<>();

    public MusicNote[] getSounds(int ticks) {
        if(sounds.containsKey(ticks)) return sounds.get(ticks).toArray(new MusicNote[sounds.get(ticks).size()]);
        return new MusicNote[]{};
    }

    public void addSound(MusicNote note, int ticks) {
        List<MusicNote> temp = new ArrayList<>();
        if(sounds.containsKey(ticks)) temp.addAll(sounds.get(ticks));
        temp.add(note);
        sounds.put(ticks, temp);
    }

    public int getLastTick() {
        if(sounds.isEmpty()) return 0;
        return sounds.entrySet().stream()
            .max((entry1, entry2) -> entry1.getKey() > entry2.getKey() ? 1 : -1)
            .get().getKey();
    }

    public MusicPlayer play(Player... players) {
        return new MusicPlayer(this, players);
    }

    public MusicPlayer play() {
        return this.play(Bukkit.getOnlinePlayers().toArray(new Player[Bukkit.getOnlinePlayers().size()]));
    }
}