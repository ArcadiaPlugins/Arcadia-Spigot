package me.redraskal.arcadia.game;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import me.redraskal.arcadia.Cuboid;
import me.redraskal.arcadia.Utils;
import me.redraskal.arcadia.api.game.BaseGame;
import me.redraskal.arcadia.api.game.GameManager;
import me.redraskal.arcadia.api.map.GameMap;
import me.redraskal.arcadia.api.scoreboard.SidebarSettings;
import me.redraskal.arcadia.api.scoreboard.WinMethod;
import me.redraskal.arcadia.api.scoreboard.defaults.ScoreSidebar;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;
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
    private Cuboid startLine;

    private int[] distances;
    private List<Location> checkpointLocs;

    private Map<Player, Integer> checkpoints;

    public HorseRaceGame(GameMap gameMap) {
        super("Horse Race", new String[]{"start", "startLineA", "startLineB", "checkpointLocs", "checkpointSize"}, new SidebarSettings(ScoreSidebar.class,
                WinMethod.HIGHEST_SCORE, 1, 30), gameMap,
        "Race through the race to the finish!");
    }

    @Override
    public void onPreStart() {
        startLine = new Cuboid(
                Utils.parseLocation((String) this.getGameMap().fetchSetting("startLineA")),
                Utils.parseLocation((String) this.getGameMap().fetchSetting("startLineB"))
        );

        start = startLine.getCenter();
        setStartBlocks(Material.GLASS);

        Location spawn = Utils.parseLocation((String) this.getGameMap().fetchSetting("start"));
        checkpoints = Maps.newHashMap();

        for(Player player : Bukkit.getOnlinePlayers()) {
            if(!this.getAPI().getGameManager().isAlive(player)) continue;

            createHorse(player, spawn);
            checkpoints.put(player, -1);
        }

        loadCheckpoints();
    }

    private void loadCheckpoints() {
        checkpointSize = (Integer) this.getGameMap().fetchSetting("checkpointSize");

        String[] rawCheckpoints = ((String) this.getGameMap().fetchSetting("checkpointLocs")).split(";");

        checkpointLocs = Lists.newArrayList();
        distances = new int[rawCheckpoints.length];

        Location previous = start;

        for (int i = 0; i < distances.length; i++) {
            Location location = Utils.parseLocation(rawCheckpoints[i]);

            checkpointLocs.add(location);


            distances[i] = (int) previous.distanceSquared(location);
            previous = location;
        }
    }

    private void createHorse(Player player, Location spawn) {
        Horse horse = spawn.getWorld().spawn(spawn, Horse.class);

        horse.setAdult();
        horse.setJumpStrength(1);
        horse.setTamed(true);
        horse.getInventory().setSaddle(new ItemStack(Material.SADDLE));

        // TODO Fix this for 1.8
        horse.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(.5D);

        horse.addPassenger(player);
    }

    @Override
    public void onGameStart() {
        setStartBlocks(Material.AIR);
    }

    private void setStartBlocks(Material material) {
        for (Block block : startLine) {
            block.setType(material);
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

        int current = checkpoints.get(player);
        int checkpointIndex = getCheckpoint(player);

        int distance = (current != -1 ? distances[current] : 0) + (int) player.getLocation().distanceSquared(checkpointLocs.get(current));

        ((ScoreSidebar) getSidebar()).setScore(player, distance);

        // Not near a checkpoint
        if (checkpointIndex == -1) {
            return;
        }

        if (checkpointIndex < current) {
            player.sendMessage("§cYou're going backwards!");
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

    private void endGame() {
        GameManager manager = this.getAPI().getGameManager();

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (manager.isAlive(player)) {
                manager.setAlive(player, false);
            }
        }
    }

    @EventHandler
    public void onLeaveHorse(VehicleExitEvent e) {
        e.setCancelled(true);
    }

    @Override
    public void onGameEnd() {}
}