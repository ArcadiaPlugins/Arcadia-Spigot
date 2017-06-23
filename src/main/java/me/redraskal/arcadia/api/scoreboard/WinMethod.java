package me.redraskal.arcadia.api.scoreboard;

import me.redraskal.arcadia.Arcadia;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Objective;

import java.util.*;

public enum WinMethod {

    LAST_PLAYER_STANDING(0),
    HIGHEST_SCORE(1),
    LOWEST_SCORE(2);

    private final int id;

    private WinMethod(int id) {
        this.id = id;
    }

    public int getID() {
        return this.id;
    }

    public Player calculateWinner(int place) {
        if(this.id == 0) {
            List<Player> deathOrder = Arcadia.getPlugin(Arcadia.class).getAPI()
                .getGameManager().getCurrentGame().getDeathOrder();
            if(deathOrder.size() > (deathOrder.size()-place) && (deathOrder.size()-place) > -1) {
                return deathOrder.get((deathOrder.size()-place));
            } else {
                return null;
            }
        }
        if(this.id == 1 || this.id == 2) {
            Objective sidebar = Arcadia.getPlugin(Arcadia.class).getAPI()
                    .getGameManager().getCurrentGame().getSidebar().getSidebar();
            Map<String, Integer> scores;
            if(this.id == 1) {
                scores = new TreeMap<>(Collections.reverseOrder());
            } else {
                scores = new TreeMap<>();
            }
            Bukkit.getOnlinePlayers().forEach(player -> {
                if(sidebar.getScore(player.getName()) != null) {
                    scores.put(player.getUniqueId().toString(), sidebar.getScore(player.getName()).getScore());
                }
            });
            if(scores.keySet().size() > (place-1) && (place-1) > -1) {
                return Bukkit.getPlayer(UUID.fromString
                   (scores.keySet().toArray(new String[scores.keySet().size() - 1])[(place - 1)]));
            } else {
                return null;
            }
        }
        return null;
    }
}