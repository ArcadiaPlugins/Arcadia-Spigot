package me.redraskal.arcadia.game;

import me.redraskal.arcadia.Utils;
import me.redraskal.arcadia.api.game.BaseGame;
import me.redraskal.arcadia.api.game.GameState;
import me.redraskal.arcadia.api.game.event.GameTickEvent;
import me.redraskal.arcadia.api.map.GameMap;
import me.redraskal.arcadia.api.scoreboard.SidebarSettings;
import me.redraskal.arcadia.api.scoreboard.WinMethod;
import me.redraskal.arcadia.api.scoreboard.defaults.PlayersLeftSidebar;
import org.bukkit.*;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class PotionDropGame extends BaseGame {

    private Location potionDropLocation;
    private double potionVelocityMultiplier;
    private double healthDropPerTwoSeconds;
    private int potionDropPerSecond;

    private int poisonEffectDelayInSeconds;
    private int witherEffectDelayInSeconds;

    public PotionDropGame(GameMap gameMap) {
        super("Potion Drop", new String[]{"startPosition", "potionDropPosition", "potionVelocityMultiplier",
                    "potionDropPerSecond", "healthDropPerTwoSeconds"},
                new SidebarSettings(PlayersLeftSidebar.class,
                        WinMethod.LAST_PLAYER_STANDING, 2, 0), gameMap,
                "Drink the potions that fall from the sky to save your life!");
        this.allowPVP = true;
    }

    @Override
    public void onPreStart() {
        Location spawnLocation = Utils.parseLocation((String) this.getGameMap().fetchSetting("startPosition"));
        for(Player player : Bukkit.getOnlinePlayers()) {
            if(!this.getAPI().getGameManager().isAlive(player)) continue;
            player.teleport(spawnLocation);
            player.setGameMode(GameMode.ADVENTURE);
        }
        this.potionDropLocation = Utils.parseLocation((String) this.getGameMap().fetchSetting("potionDropPosition"));
        this.potionVelocityMultiplier = Double.parseDouble((String) this.getGameMap().fetchSetting("potionVelocityMultiplier"));
        this.healthDropPerTwoSeconds = Double.parseDouble((String) this.getGameMap().fetchSetting("healthDropPerTwoSeconds"));
        this.potionDropPerSecond = Integer.parseInt((String) this.getGameMap().fetchSetting("potionDropPerSecond"));

        this.poisonEffectDelayInSeconds = Integer.parseInt((String) this.getGameMap().fetchSetting("poisonEffectDelayInSeconds"))+1;
        this.witherEffectDelayInSeconds = Integer.parseInt((String) this.getGameMap().fetchSetting("witherEffectDelayInSeconds"))+1;
    }

    @Override
    public void onGameStart() {
        new BukkitRunnable() {
            int seconds = 0;
            public void run() {
                if(getAPI().getGameManager().getGameState() != GameState.INGAME) {
                    this.cancel();
                    return;
                }
                seconds++;
                if(seconds % 2 == 0) {
                    Bukkit.getOnlinePlayers().forEach(player -> {
                        if(getAPI().getGameManager().isAlive(player)) {
                            if((player.getHealth()-healthDropPerTwoSeconds) <= 0) {
                                getAPI().getGameManager().setAlive(player, false);
                            } else {
                                player.damage(healthDropPerTwoSeconds);
                            }
                        }
                    });
                } else {
                    Bukkit.getOnlinePlayers().forEach(player -> {
                        if(getAPI().getGameManager().isAlive(player)) {
                            if((player.getHealth()-0.1) <= 0) {
                                getAPI().getGameManager().setAlive(player, false);
                            } else {
                                player.damage(0.1);
                            }
                        }
                    });
                }
                if(seconds == poisonEffectDelayInSeconds) {
                    Bukkit.broadcastMessage(ChatColor.GOLD + "All players now have the poison effect.");
                    Bukkit.getOnlinePlayers().forEach(player -> {
                        if(getAPI().getGameManager().isAlive(player)) {
                            player.addPotionEffect(new PotionEffect(PotionEffectType.POISON, Integer.MAX_VALUE, 0), true);
                            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_HARP, 1f, 0.7f);
                            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BASS, 1f, 1f);
                        }
                    });
                }
                if(seconds == witherEffectDelayInSeconds) {
                    Bukkit.broadcastMessage(ChatColor.GOLD + "All players now have the wither effect.");
                    Bukkit.getOnlinePlayers().forEach(player -> {
                        if(getAPI().getGameManager().isAlive(player)) {
                            player.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, Integer.MAX_VALUE, 1), true);
                            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_HARP, 1f, 0.7f);
                            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BASS, 1f, 1f);
                        }
                    });
                }
            }
        }.runTaskTimer(this.getAPI().getPlugin(), 0, 20L);
    }

    @EventHandler
    public void onGameTick(GameTickEvent event) {
        if(event.getTicksInSecond() < potionDropPerSecond) {
            ItemStack itemStack = new ItemStack(Material.POTION, 1, (byte) 8197);
            Item entity = this.potionDropLocation.getWorld().dropItem(potionDropLocation, itemStack);
            entity.setCustomName(ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "Potion");
            entity.setCustomNameVisible(true);
            entity.setVelocity(new Vector(0, 0.47, 0).add(Utils.getRandomCircleVector()
                .multiply(potionVelocityMultiplier*Math.random())));
        }
    }

    @EventHandler
    public void onHealthRegain(EntityRegainHealthEvent event) {
        if(event.getRegainReason() == EntityRegainHealthEvent.RegainReason.SATIATED) {
            event.setCancelled(true);
        }
    }

    @Override
    public void onGameEnd() {}
}