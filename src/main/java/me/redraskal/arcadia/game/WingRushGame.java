package me.redraskal.arcadia.game;

import me.redraskal.arcadia.Arcadia;
import me.redraskal.arcadia.Freeze;
import me.redraskal.arcadia.Utils;
import me.redraskal.arcadia.api.game.BaseGame;
import me.redraskal.arcadia.api.map.GameMap;
import me.redraskal.arcadia.api.scoreboard.SidebarSettings;
import me.redraskal.arcadia.api.scoreboard.WinMethod;
import me.redraskal.arcadia.api.scoreboard.defaults.ScoreSidebar;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class WingRushGame extends BaseGame {

    private Location startLocationCenter;
    private List<Freeze> freezeList = new ArrayList<>();

    public WingRushGame(GameMap gameMap) {
        super(Arcadia.getPlugin(Arcadia.class).getAPI().getTranslationManager().fetchTranslation("game.wingrush.name").build(),
                new String[]{"startPositionCenter"},
                new SidebarSettings(ScoreSidebar.class,
                        WinMethod.HIGHEST_SCORE, 3, 0), gameMap,
                Arcadia.getPlugin(Arcadia.class).getAPI().getTranslationManager().fetchTranslation("game.wingrush.desc").build());
    }

    @Override
    public void onPreStart() {
        this.startLocationCenter = Utils.parseLocation((String) this.getGameMap().fetchSetting("startPositionCenter"));
        for(Player player : Bukkit.getOnlinePlayers()) {
            if(!this.getAPI().getGameManager().isAlive(player)) continue;
            player.teleport(startLocationCenter);
            player.setGameMode(GameMode.ADVENTURE);
            player.getInventory().setChestplate(new ItemStack(Material.ELYTRA, 1));
            player.setGliding(true);
            freezeList.add(new Freeze(player));
        }
    }

    @Override
    public void onGameStart() {
        freezeList.forEach(freeze -> {
            freeze.destroy();
        });
        freezeList.clear();
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if(event.getPlayer().isGliding() && event.getPlayer().getLocation().getPitch() < 0) {
            event.getPlayer().setVelocity(event.getPlayer().getLocation().getDirection().multiply(1D));
        }
    }

    @Override
    public void onGameEnd() {}
}