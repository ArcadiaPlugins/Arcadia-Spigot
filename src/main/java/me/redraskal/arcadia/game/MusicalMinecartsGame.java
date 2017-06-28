package me.redraskal.arcadia.game;

import me.redraskal.arcadia.Arcadia;
import me.redraskal.arcadia.Cuboid;
import me.redraskal.arcadia.Utils;
import me.redraskal.arcadia.api.game.BaseGame;
import me.redraskal.arcadia.api.game.GameState;
import me.redraskal.arcadia.api.map.GameMap;
import me.redraskal.arcadia.api.music.defaults.MusicalMinecartsMusic;
import me.redraskal.arcadia.api.scoreboard.SidebarSettings;
import me.redraskal.arcadia.api.scoreboard.WinMethod;
import me.redraskal.arcadia.api.scoreboard.defaults.PlayersLeftSidebar;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class MusicalMinecartsGame extends BaseGame {

    private List<Location> spawnLocations = new ArrayList<>();
    private Random random = new Random();
    private int inMinecarts = 0;
    private boolean enoughMinecarts = true;
    private BukkitTask killRunnable;

    public MusicalMinecartsGame(GameMap gameMap) {
        super(Arcadia.getPlugin(Arcadia.class).getAPI().getTranslationManager().fetchTranslation("game.musicalminecarts.name").build(),
                new String[]{"startPosition", "minecartBoundsA", "minecartBoundsB"},
                new SidebarSettings(PlayersLeftSidebar.class,
                        WinMethod.LAST_PLAYER_STANDING, 1, 30), gameMap,
                Arcadia.getPlugin(Arcadia.class).getAPI().getTranslationManager().fetchTranslation("game.musicalminecarts.desc").build());
    }

    @Override
    public void onPreStart() {
        Location spawnLocation = Utils.parseLocation((String) this.getGameMap().fetchSetting("startPosition"));
        for(Player player : Bukkit.getOnlinePlayers()) {
            if(!this.getAPI().getGameManager().isAlive(player)) continue;
            player.teleport(spawnLocation);
            player.setGameMode(GameMode.ADVENTURE);
        }
        Cuboid minecartSpawn = new Cuboid(Utils.parseLocation((String) this.getGameMap().fetchSetting("minecartBoundsA")),
            Utils.parseLocation((String) this.getGameMap().fetchSetting("minecartBoundsB")));
        Iterator<Block> minecartSpawns = minecartSpawn.iterator();
        while(minecartSpawns.hasNext()) {
            this.spawnLocations.add(minecartSpawns.next().getLocation().clone().add(0, 2.5D, 0));
        }
    }

    @Override
    public void onGameStart() {
        this.nextEvent();
    }

    public void nextEvent() {
        if(getAPI().getGameManager().getGameState() != GameState.INGAME) return;
        if(this.killRunnable != null) this.killRunnable.cancel();
        this.killRunnable = null;
        this.inMinecarts = 0;
        this.killMinecarts();
        new MusicalMinecartsMusic();
        final int totalTicks = 80+random.nextInt(120);
        Bukkit.getOnlinePlayers().forEach(player -> {
            if(getAPI().getGameManager().isAlive(player)) {
                player.setExp(0);
            }
        });
        new BukkitRunnable() {
            int ticks = 0;
            public void run() {
                if(getAPI().getGameManager().getGameState() != GameState.INGAME) {
                    this.cancel();
                    return;
                }
                if(ticks >= totalTicks) {
                    this.cancel();
                    MusicalMinecartsMusic.stopMusic();
                    spawnMinecarts();
                    killRunnable();
                } else {
                    Location location = spawnLocations.get(random.nextInt(spawnLocations.size()));
                    location.getWorld().spigot().playEffect(location, Effect.NOTE, 0, 0, 1, 1, 1, 1, 6, 30);
                }
                ticks++;
            }
        }.runTaskTimer(this.getAPI().getPlugin(), 0, 1L);
    }

    private void killMinecarts() {
        this.getAPI().getMapRegistry().getCurrentWorld().getEntities().forEach(entity -> {
            if(entity instanceof Minecart) {
                entity.setMetadata("allow-exit", new FixedMetadataValue(this.getAPI().getPlugin(), true));
                if(!entity.getPassengers().isEmpty()) entity.eject();
                entity.remove();
            }
        });
    }

    private void spawnMinecarts() {
        int minecarts = this.getAPI().getGameManager().getPlayersAlive();
        if(!enoughMinecarts) minecarts--;
        for(int i=0; i<minecarts; i++) {
            Location location = spawnLocations.get(new Random().nextInt(spawnLocations.size()));
            Minecart minecart = location.getWorld().spawn(location, Minecart.class);
            minecart.getWorld().playSound(minecart.getLocation(), Sound.ENTITY_CHICKEN_EGG, 1f, 1f);
            minecart.setGlowing(true);
        }
        if(enoughMinecarts) enoughMinecarts = false;
    }

    private void killRunnable() {
        Bukkit.getOnlinePlayers().forEach(player -> {
            if(this.getAPI().getGameManager().isAlive(player)) {
                player.setExp(1F);
            }
        });
        this.killRunnable = new BukkitRunnable() {
            int ticks = 0;
            final int totalTicks = 120;
            public void run() {
                if(getAPI().getGameManager().getGameState() != GameState.INGAME) {
                    this.cancel();
                    return;
                }
                if(ticks >= totalTicks) {
                    this.cancel();
                    killRunnable = null;
                    Bukkit.getOnlinePlayers().forEach(player -> {
                        if(getAPI().getGameManager().isAlive(player)) {
                            player.setExp(0F);
                            if(player.getVehicle() == null) getAPI().getGameManager().setAlive(player, false);
                        }
                    });
                    new BukkitRunnable() {
                        public void run() {
                            nextEvent();
                        }
                    }.runTaskLater(Arcadia.getPlugin(Arcadia.class), 20L);
                } else {
                    double percent = (100D-(((double) ticks/(double) totalTicks)*100D));
                    float xp = ((Double.valueOf(percent).floatValue() % 100) / 100);
                    Bukkit.getOnlinePlayers().forEach(player -> {
                        if(getAPI().getGameManager().isAlive(player)) {
                            player.setExp(xp);
                        }
                    });
                }
                ticks++;
            }
        }.runTaskTimer(this.getAPI().getPlugin(), 0, 1L);
    }

    @EventHandler
    public void onMinecartEnter(PlayerInteractEntityEvent event) {
        if(!(event.getRightClicked() instanceof Minecart)) return;
        event.getRightClicked().setGlowing(false);
        event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1f, 1f);
        inMinecarts++;
        if(inMinecarts >= this.getAPI().getGameManager().getPlayersAlive()) {
            new BukkitRunnable() {
                public void run() {
                    if(getAPI().getGameManager().getGameState() != GameState.INGAME) return;
                    Bukkit.getOnlinePlayers().forEach(player -> {
                        if(getAPI().getGameManager().isAlive(player)) {
                            if(player.getVehicle() == null) getAPI().getGameManager().setAlive(player, false);
                        }
                    });
                    nextEvent();
                }
            }.runTaskLater(this.getAPI().getPlugin(), 20L);
        }
    }

    @Override
    public void onGameEnd() {
        MusicalMinecartsMusic.stopMusic();
        this.killMinecarts();
    }
}