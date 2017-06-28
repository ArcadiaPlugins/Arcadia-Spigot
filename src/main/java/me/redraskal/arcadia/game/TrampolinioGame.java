package me.redraskal.arcadia.game;

import me.redraskal.arcadia.Arcadia;
import me.redraskal.arcadia.Cuboid;
import me.redraskal.arcadia.Utils;
import me.redraskal.arcadia.api.game.BaseGame;
import me.redraskal.arcadia.api.game.GameState;
import me.redraskal.arcadia.api.map.GameMap;
import me.redraskal.arcadia.api.scoreboard.SidebarSettings;
import me.redraskal.arcadia.api.scoreboard.WinMethod;
import me.redraskal.arcadia.api.scoreboard.defaults.ScoreSidebar;
import me.redraskal.arcadia.game.trampolino.PointType;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class TrampolinioGame extends BaseGame {

    private List<Location> spawnLocations = new ArrayList<>();
    private Map<Location, Object[]> currentLocations = new HashMap<>();

    public TrampolinioGame(GameMap gameMap) {
        super(Arcadia.getPlugin(Arcadia.class).getAPI().getTranslationManager().fetchTranslation("game.trampolinio.name").build(),
                new String[]{"startPosition", "pointBoundsA", "pointBoundsB"},
                new SidebarSettings(ScoreSidebar.class,
                        WinMethod.HIGHEST_SCORE, 1, 0), gameMap,
                Arcadia.getPlugin(Arcadia.class).getAPI().getTranslationManager().fetchTranslation("game.trampolinio.desc").build());
    }

    @Override
    public void onPreStart() {
        Location spawnLocation = Utils.parseLocation((String) this.getGameMap().fetchSetting("startPosition"));
        for(Player player : Bukkit.getOnlinePlayers()) {
            if(!this.getAPI().getGameManager().isAlive(player)) continue;
            player.teleport(spawnLocation);
            player.setGameMode(GameMode.ADVENTURE);
        }
        Cuboid pointSpawn = new Cuboid(Utils.parseLocation((String) this.getGameMap().fetchSetting("pointBoundsA")),
                Utils.parseLocation((String) this.getGameMap().fetchSetting("pointBoundsB")));
        Iterator<Block> pointSpawns = pointSpawn.iterator();
        while(pointSpawns.hasNext()) {
            this.spawnLocations.add(pointSpawns.next().getLocation());
        }
    }

    @Override
    public void onGameStart() {
        for(int i=0; i<20; i++) {
            this.spawnPoint();
        }
        new BukkitRunnable() {
            public void run() {
                if(getAPI().getGameManager().getGameState() != GameState.INGAME) {
                    this.cancel();
                    return;
                }
                Bukkit.getOnlinePlayers().forEach(player -> {
                    if(getAPI().getGameManager().isAlive(player)) {
                        if(player.isOnGround()) player.setVelocity(new org.bukkit.util.Vector(0, 0.7D+(1D*Math.random()), 0));
                    }
                });
            }
        }.runTaskTimer(this.getAPI().getPlugin(), 0, 10L);
    }

    private void spawnPoint() {
        final Location spawnLocation = spawnLocations.get(new Random().nextInt(spawnLocations.size()));
        for(Map.Entry<Location, Object[]> entry : this.currentLocations.entrySet()) {
            if(spawnLocation.distance(entry.getKey()) >= 1D) continue;
            return;
        }
        PointType pointType = PointType.fetch();
        FallingBlock entity = spawnLocation.getWorld().spawnFallingBlock(spawnLocation, pointType.getMaterialData());
        entity.setGravity(false);
        entity.setDropItem(false);
        entity.setHurtEntities(false);
        entity.setInvulnerable(true);
        entity.setCustomName(pointType.getTranslation());
        entity.setCustomNameVisible(true);
        new BukkitRunnable() {
            public void run() {
                if(entity.isDead()) {
                    this.cancel();
                } else {
                    entity.setTicksLived(1);
                    entity.teleport(spawnLocation);
                }
            }
        }.runTaskTimer(this.getAPI().getPlugin(), 0, 20L);
        this.currentLocations.put(spawnLocation, new Object[]{pointType, entity});
    }

    private void claimPoint(Player player, Location location) {
        if(!this.currentLocations.containsKey(location)) return;
        final PointType pointType = (PointType) this.currentLocations.get(location)[0];
        final FallingBlock entity = (FallingBlock) this.currentLocations.get(location)[1];
        this.currentLocations.remove(location);
        entity.getWorld().playSound(entity.getLocation(), Sound.ENTITY_CHICKEN_EGG, 1f, 1f);
        player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1.1f);
        entity.remove();
        ScoreSidebar scoreSidebar = ((ScoreSidebar) this.getSidebar());
        scoreSidebar.setScore(player, (scoreSidebar.getScore(player) + pointType.getPoints()));
        if(pointType == PointType.SUPER_BOOST) {
            player.setVelocity(new org.bukkit.util.Vector(0, 1.7D, 0));
        }
        this.spawnPoint();
    }

    @EventHandler
    public void onFallingBlockChange(EntityChangeBlockEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if(!this.getAPI().getGameManager().isAlive(event.getPlayer())) return;
        for(Map.Entry<Location, Object[]> entry : this.currentLocations.entrySet()) {
            if(event.getTo().distance(entry.getKey()) >= 1D) continue;
            this.claimPoint(event.getPlayer(), entry.getKey());
            return;
        }
    }

    @Override
    public void onGameEnd() {
        currentLocations.values().forEach(object -> {
            ((FallingBlock) object[1]).remove();
        });
    }
}