package me.redraskal.arcadia.game;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import me.redraskal.arcadia.Arcadia;
import me.redraskal.arcadia.Cuboid;
import me.redraskal.arcadia.Utils;
import me.redraskal.arcadia.api.game.BaseGame;
import me.redraskal.arcadia.api.game.GameState;
import me.redraskal.arcadia.api.game.event.PlayerAliveStatusEvent;
import me.redraskal.arcadia.api.map.GameMap;
import me.redraskal.arcadia.api.scoreboard.SidebarSettings;
import me.redraskal.arcadia.api.scoreboard.WinMethod;
import me.redraskal.arcadia.api.scoreboard.defaults.ScoreSidebar;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Map;

/**
 * @author Hugmanrique
 * @since 23/06/2017
 */
public class HorseRaceGame extends BaseGame {
    private int checkpointSize;

    private Location start;
    private Location spawn;
    private Cuboid startLine;
    private int floorLevel;

    private int[] distances;
    private int totalDistance;

    private List<Location> checkpointLocs;
    private Map<Player, Integer> checkpoints;

    public HorseRaceGame(GameMap gameMap) {
        super(Arcadia.getPlugin(Arcadia.class).getAPI().getTranslationManager().fetchTranslation("game.horserace.name").build(),
                new String[]{"startPosition", "floorLevel", "startLineA", "startLineB", "checkpointLocs", "checkpointRadius"},
                new SidebarSettings(ScoreSidebar.class,
                WinMethod.HIGHEST_SCORE, 1, 30), gameMap,
                Arcadia.getPlugin(Arcadia.class).getAPI().getTranslationManager().fetchTranslation("game.horserace.desc").build());
        this.killOnMapExit = false;
    }

    @Override
    public void onPreStart() {
        startLine = new Cuboid(
                Utils.parseLocation((String) this.getGameMap().fetchSetting("startLineA")),
                Utils.parseLocation((String) this.getGameMap().fetchSetting("startLineB"))
        );
        start = startLine.getCenter();
        startLine.iterator().forEachRemaining(block -> {
            block.setType(Material.GLASS);
        });
        spawn = Utils.parseLocation((String) this.getGameMap().fetchSetting("startPosition"));
        checkpoints = Maps.newHashMap();
        floorLevel = Integer.parseInt((String) this.getGameMap().fetchSetting("floorLevel"));
        for(Player player : Bukkit.getOnlinePlayers()) {
            if(!this.getAPI().getGameManager().isAlive(player)) continue;
            createHorse(player, spawn);
            checkpoints.put(player, -1);
        }
        loadCheckpoints();
    }

    private void loadCheckpoints() {
        checkpointSize = Integer.parseInt((String) this.getGameMap().fetchSetting("checkpointRadius"));

        String[] rawCheckpoints = ((String) this.getGameMap().fetchSetting("checkpointLocs")).split(";");

        checkpointLocs = Lists.newArrayList();
        distances = new int[rawCheckpoints.length];
        totalDistance = 0;

        Location previous = start;

        for (int i = 0; i < distances.length; i++) {
            Location location = Utils.parseLocation(rawCheckpoints[i]);
            checkpointLocs.add(location);

            distances[i] = (int) previous.distance(location);
            totalDistance += distances[i];

            previous = location;
        }
    }

    private void createHorse(Player player, Location spawn) {
        player.teleport(spawn);
        Horse horse = spawn.getWorld().spawn(spawn, Horse.class);
        horse.setJumpStrength(0);

        double speed = this.getAPI().getGameManager().getGameState() == GameState.STARTING ? 0D : 0.7D;

        // TODO Fix this for 1.8
        horse.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(speed);

        Bukkit.getScheduler().runTaskLater(getAPI().getPlugin(), () -> {
            horse.addPassenger(player);
            horse.setTamed(true);
            horse.getInventory().setSaddle(new ItemStack(Material.SADDLE));
            horse.setAdult();
            horse.setOwner(player);
        }, 1L);
    }

    @Override
    public void onGameStart() {
        startLine.iterator().forEachRemaining(block -> {
            block.setType(Material.AIR);
        });
        for(Player player : Bukkit.getOnlinePlayers()) {
            if(!this.getAPI().getGameManager().isAlive(player)) continue;
            if(player.getVehicle() != null) {
                ((Horse) player.getVehicle()).getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(0.7D);
            }
        }
    }

    private int getCheckpoint(Player player) {
        Location location = player.getLocation();

        for (int i = 0; i < checkpointLocs.size(); i++) {
            Location checkpoint = checkpointLocs.get(i);

            if (checkpoint.distanceSquared(location) < checkpointSize) {
                return i;
            }
        }

        return -1;
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        if (!this.getAPI().getGameManager().isAlive(player)) {
            return;
        }

        if(event.getTo().getY() <= floorLevel) {
            if(event.getPlayer().getVehicle() != null) {
                Entity vehicle = event.getPlayer().getVehicle();
                vehicle.eject();
                vehicle.remove();
            }
            this.createHorse(event.getPlayer(), this.spawn);
        }

        int current = checkpoints.get(player);
        int checkpointIndex = getCheckpoint(player);

        int distance;

        if (current == -1) {
            distance = (int) player.getLocation().distance(spawn);
        } else {
            distance = distances[current] + (int) player.getLocation().distance(checkpointLocs.get(current));
        }

        distance = -(totalDistance - distance);

        ((ScoreSidebar) getSidebar()).setScore(player, distance);

        // Not near a checkpoint
        if (checkpointIndex == -1) {
            return;
        }

        if (checkpointIndex < current) {
            this.getAPI().getTranslationManager().sendTranslation("game.horserace.wrong-way", event.getPlayer());
            return;
        }

        if (checkpointIndex > current) {
            checkpoints.put(player, checkpointIndex);
        }

        // Last checkpoint
        if (checkpointIndex == checkpointLocs.size() - 1) {
            double lastDistance = start.distanceSquared(player.getLocation());

            if (lastDistance < checkpointSize) {
                // Player won
                endGame();
            }
        }
    }

    @EventHandler
    public void onSpec(PlayerAliveStatusEvent e) {
        if (e.isAlive()) {
            return;
        }

        Entity entity = e.getPlayer().getVehicle();

        if (entity != null) {
            entity.eject();
            entity.remove();
        }
    }

    @Override
    public void onGameEnd() {}
}
