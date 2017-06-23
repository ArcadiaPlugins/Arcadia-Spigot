package me.redraskal.arcadia.api.scoreboard;

import me.redraskal.arcadia.Arcadia;
import me.redraskal.arcadia.ArcadiaAPI;
import me.redraskal.arcadia.Utils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Objective;

import java.util.*;

public enum WinMethod {

    LAST_PLAYER_STANDING(0),
    HIGHEST_SCORE(1);

    private final int id;

    private WinMethod(int id) {
        this.id = id;
    }

    public int getID() {
        return this.id;
    }

    public Player calculateWinner(int place) {
        ArcadiaAPI api = Arcadia.getPlugin(Arcadia.class).getAPI();
        if(this.id == 0) {
            List<Player> deathOrder = api.getGameManager().getCurrentGame().getDeathOrder();
            Iterator<Player> iterator = deathOrder.iterator();
            while(iterator.hasNext()) {
                Player next = iterator.next();
                if(api.getGameManager().getCurrentGame().spectatorCache.contains(next)) {
                    iterator.remove();
                }
            }
            if(deathOrder.size() > (deathOrder.size()-place) && (deathOrder.size()-place) > -1) {
                return deathOrder.get((deathOrder.size()-place));
            } else {
                return null;
            }
        }
        if(this.id == 1) {
            Objective sidebar = api.getGameManager().getCurrentGame().getSidebar().getSidebar();
            Map<String, Integer> scores = new HashMap<>();
            Bukkit.getOnlinePlayers().forEach(player -> {
                if(sidebar.getScore(player.getName()) != null) {
                    if(!api.getGameManager().getCurrentGame().spectatorCache.contains(player)) {
                        scores.put(player.getUniqueId().toString(), sidebar.getScore(player.getName()).getScore());
                    }
                }
            });
            List<Map.Entry<String, Integer>> sorted = Utils.entriesSortedByValues(scores);
            if(sorted.size() > (place-1) && (place-1) > -1) {
                return Bukkit.getPlayer(UUID.fromString(sorted.get((place-1)).getKey()));
            } else {
                return null;
            }
        }
        return null;
    }
}