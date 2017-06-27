package me.redraskal.arcadia.game;

import me.redraskal.arcadia.Arcadia;
import me.redraskal.arcadia.Cuboid;
import me.redraskal.arcadia.Utils;
import me.redraskal.arcadia.api.game.BaseGame;
import me.redraskal.arcadia.api.game.GameState;
import me.redraskal.arcadia.api.map.GameMap;
import me.redraskal.arcadia.api.scoreboard.SidebarSettings;
import me.redraskal.arcadia.api.scoreboard.WinMethod;
import me.redraskal.arcadia.api.scoreboard.defaults.RelativeDistanceSidebar;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Iterator;
import java.util.Random;

public class RedLightGreenLightGame extends BaseGame {

    private Location startPosition;
    private Location targetPosition;
    private String towards;
    private Cuboid glass;
    private boolean redLight = false;
    private Random random = new Random();

    public RedLightGreenLightGame(GameMap gameMap) {
        super(Arcadia.getPlugin(Arcadia.class).getAPI().getTranslationManager().fetchTranslation("game.redlightgreenlight.name").build(),
                new String[]{"startPosition", "targetPosition", "targetTowards", "glassBoundsA", "glassBoundsB"},
                new SidebarSettings(RelativeDistanceSidebar.class,
                    WinMethod.HIGHEST_SCORE, 1, 30), gameMap,
                Arcadia.getPlugin(Arcadia.class).getAPI().getTranslationManager().fetchTranslation("game.redlightgreenlight.desc").build());
    }

    @Override
    public void onPreStart() {
        this.startPosition = Utils.parseLocation((String) this.getGameMap().fetchSetting("startPosition"));
        for(Player player : Bukkit.getOnlinePlayers()) {
            if(!this.getAPI().getGameManager().isAlive(player)) continue;
            player.teleport(startPosition);
            player.setGameMode(GameMode.ADVENTURE);
        }
        this.targetPosition = Utils.parseLocation((String) this.getGameMap().fetchSetting("targetPosition"));
        this.towards = (String) this.getGameMap().fetchSetting("targetTowards");
        ((RelativeDistanceSidebar) this.getSidebar()).setTarget(targetPosition, towards);
        this.glass = new Cuboid(Utils.parseLocation((String) this.getGameMap().fetchSetting("glassBoundsA")),
                Utils.parseLocation((String) this.getGameMap().fetchSetting("glassBoundsB")));
        Iterator<Block> glassBlocks = glass.iterator();
        while(glassBlocks.hasNext()) {
            glassBlocks.next().setType(Material.GLASS);
        }
    }

    @Override
    public void onGameStart() {
        Iterator<Block> glassBlocks = glass.iterator();
        while(glassBlocks.hasNext()) {
            glassBlocks.next().setType(Material.AIR);
        }
        this.nextEvent();
    }

    public void nextEvent() {
        if(getAPI().getGameManager().getGameState() != GameState.INGAME) return;
        this.redLight = false;
        final int totalTicks = 20+random.nextInt(100);
        ItemStack itemStack = new ItemStack(Material.WOOL, 1, (byte) 5);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(ChatColor.GREEN + "" + ChatColor.BOLD + "GREEN");
        itemStack.setItemMeta(itemMeta);
        Bukkit.getOnlinePlayers().forEach(player -> {
            if(getAPI().getGameManager().isAlive(player)) {
                for(int i=0; i<9; i++) {
                    player.getInventory().setItem(i, itemStack);
                }
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
                    redLight = true;
                    ItemStack itemStack = new ItemStack(Material.WOOL, 1, (byte) 14);
                    ItemMeta itemMeta = itemStack.getItemMeta();
                    itemMeta.setDisplayName(ChatColor.RED + "" + ChatColor.BOLD + "RED");
                    itemStack.setItemMeta(itemMeta);
                    Bukkit.getOnlinePlayers().forEach(player -> {
                        if(getAPI().getGameManager().isAlive(player)) {
                            for(int i=0; i<9; i++) {
                                player.getInventory().setItem(i, itemStack);
                            }
                        }
                    });
                    new BukkitRunnable() {
                        public void run() {
                            nextEvent();
                        }
                    }.runTaskLater(Arcadia.getPlugin(Arcadia.class), 40L);
                } else {
                    if(ticks == Math.floor(totalTicks/2)) {
                        ItemStack itemStack = new ItemStack(Material.WOOL, 1, (byte) 4);
                        ItemMeta itemMeta = itemStack.getItemMeta();
                        itemMeta.setDisplayName(ChatColor.YELLOW + "" + ChatColor.BOLD + "YELLOW");
                        itemStack.setItemMeta(itemMeta);
                        Bukkit.getOnlinePlayers().forEach(player -> {
                            if(getAPI().getGameManager().isAlive(player)) {
                                for(int i=0; i<9; i++) {
                                    player.getInventory().setItem(i, itemStack);
                                }
                            }
                        });
                    }
                }
                ticks++;
            }
        }.runTaskTimer(this.getAPI().getPlugin(), 0, 1L);
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if(this.getAPI().getGameManager().isAlive(event.getPlayer())) {
            if(this.getSidebar().getSidebar().getScore(event.getPlayer().getName()).getScore() >= -1) {
                this.endGame();
            }
            if(!this.redLight) return;
            if(event.getTo().distance(this.startPosition) <= 3) return;
            if(event.getFrom().distance(event.getTo()) > 0.1D) {
                event.setTo(this.startPosition);
            }
        }
    }

    @Override
    public void onGameEnd() {}
}