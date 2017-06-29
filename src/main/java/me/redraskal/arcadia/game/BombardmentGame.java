package me.redraskal.arcadia.game;

import me.redraskal.arcadia.Arcadia;
import me.redraskal.arcadia.Utils;
import me.redraskal.arcadia.api.game.BaseGame;
import me.redraskal.arcadia.api.game.event.GameTickEvent;
import me.redraskal.arcadia.api.map.GameMap;
import me.redraskal.arcadia.api.scoreboard.SidebarSettings;
import me.redraskal.arcadia.api.scoreboard.WinMethod;
import me.redraskal.arcadia.api.scoreboard.defaults.PlayersLeftSidebar;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.material.MaterialData;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BombardmentGame extends BaseGame {

    private Location spawnLocation;
    private List<Location> cannonPositions = new ArrayList<>();
    private int totalCannonShots = 0;
    private int cannonShotDelay = 40;
    private double cannonVelocityOffset;

    public BombardmentGame(GameMap gameMap) {
        super(Arcadia.getPlugin(Arcadia.class).getAPI().getTranslationManager().fetchTranslation("game.bombardment.name").build(),
                new String[]{"startPosition", "cannonPositions", "cannonVelocityOffset"},
                new SidebarSettings(PlayersLeftSidebar.class,
                        WinMethod.LAST_PLAYER_STANDING, 1, 20), gameMap,
                Arcadia.getPlugin(Arcadia.class).getAPI().getTranslationManager().fetchTranslation("game.bombardment.desc").build());
    }

    @Override
    public void onPreStart() {
        this.spawnLocation = Utils.parseLocation((String) this.getGameMap().fetchSetting("startPosition"));
        for(Player player : Bukkit.getOnlinePlayers()) {
            if(!this.getAPI().getGameManager().isAlive(player)) continue;
            player.teleport(spawnLocation);
            player.setGameMode(GameMode.ADVENTURE);
        }
        String[] rawLocations = ((String) this.getGameMap().fetchSetting("cannonPositions")).split(";");
        for(String location : rawLocations) {
            cannonPositions.add(Utils.parseLocation(location));
        }
        this.cannonVelocityOffset = Double.valueOf(((String) this.getGameMap().fetchSetting("cannonVelocityOffset")));
    }

    @Override
    public void onGameStart() {}

    @EventHandler
    public void onGameTick(GameTickEvent event) {
        if(totalCannonShots == 80 && cannonShotDelay != 1) {
            this.getAPI().getTranslationManager().sendTranslation("game.bombardment.faster");
            cannonShotDelay = 1;
            cannonVelocityOffset+=0.5;
        }
        if(totalCannonShots == 50 && cannonShotDelay != 3) {
            this.getAPI().getTranslationManager().sendTranslation("game.bombardment.faster");
            cannonShotDelay = 3;
            cannonVelocityOffset+=0.5;
        }
        if(totalCannonShots == 30 && cannonShotDelay != 5) {
            this.getAPI().getTranslationManager().sendTranslation("game.bombardment.faster");
            cannonShotDelay = 5;
            cannonVelocityOffset+=0.5;
        }
        if(totalCannonShots == 15 && cannonShotDelay != 10) {
            this.getAPI().getTranslationManager().sendTranslation("game.bombardment.faster");
            cannonShotDelay = 10;
            cannonVelocityOffset+=0.5;
        }
        if(totalCannonShots == 5 && cannonShotDelay != 20) {
            this.getAPI().getTranslationManager().sendTranslation("game.bombardment.faster");
            cannonShotDelay = 20;
            cannonVelocityOffset+=0.5;
        }
        if(event.getTotalTicks() % cannonShotDelay == 0) {
            this.fireRandomly();
        }
    }

    @EventHandler
    public void onFallingBlockChange(EntityChangeBlockEvent event) {
        final Entity entity = event.getEntity();
        this.playParticles(entity.getLocation());
        entity.getNearbyEntities(2, 2, 2).forEach(nearby -> {
            if(nearby instanceof Player) {
                Player player = (Player) nearby;
                if(getAPI().getGameManager().isAlive(player)) {
                    getAPI().getGameManager().setAlive(player, false);
                }
            }
        });
        entity.remove();
        event.setCancelled(true);
    }

    private void playParticles(Location location) {
        location.getWorld().spigot().playEffect(location, Effect.EXPLOSION_LARGE,
                0, 0, 1, 1, 1, 0, 6, 100);
        location.getWorld().spigot().playEffect(location, Effect.LAVA_POP,
                0, 0, 1, 1, 1, 0, 3, 100);
        location.getWorld().spigot().playEffect(location, Effect.LARGE_SMOKE,
                0, 0, 1, 1, 1, 0, 5, 100);
        location.getWorld().playSound(location, Sound.ENTITY_GENERIC_EXPLODE, 5f, 0.9f);
    }

    private void fireRandomly() {
        final Location cannon = cannonPositions.get(new Random().nextInt(cannonPositions.size()));
        Location target = this.spawnLocation.clone();
        if(this.getAPI().getGameManager().getPlayersAlive() > 0) {
            target = getAPI().getGameManager().getAlivePlayers()
                .get(new Random().nextInt(getAPI().getGameManager().getAlivePlayers().size()))
                    .getLocation().clone().add(0, (cannonVelocityOffset+(cannonVelocityOffset*Math.random())), 0);
        }
        this.fireCannon(cannon, target);
    }

    private void fireCannon(Location cannon, Location target) {
        FallingBlock entity = cannon.getWorld().spawnFallingBlock(cannon, new MaterialData(Material.COAL_BLOCK));
        this.playParticles(entity.getLocation());
        entity.setGravity(true);
        entity.setDropItem(false);
        entity.setHurtEntities(false);
        entity.setInvulnerable(true);
        this.moveToward(entity, target, 2D);
        new BukkitRunnable() {
            public void run() {
                if(entity.isDead()) {
                    this.cancel();
                } else {
                    entity.setTicksLived(1);
                }
            }
        }.runTaskTimer(this.getAPI().getPlugin(), 0, 20L);
        totalCannonShots++;
    }

    private void moveToward(Entity entity, Location to, double speed) {
        Location loc = entity.getLocation();
        double x = loc.getX() - to.getX();
        double y = loc.getY() - to.getY();
        double z = loc.getZ() - to.getZ();
        Vector velocity = new Vector(x, y, z).normalize().multiply(-speed);
        entity.setVelocity(velocity);
    }

    @Override
    public void onGameEnd() {}
}