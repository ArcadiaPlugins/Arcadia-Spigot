package me.redraskal.arcadia.runnable;

import me.redraskal.arcadia.Arcadia;
import me.redraskal.arcadia.ArcadiaAPI;
import me.redraskal.arcadia.Utils;
import me.redraskal.arcadia.api.game.BaseGame;
import me.redraskal.arcadia.api.game.GameState;
import me.redraskal.arcadia.api.game.VotingData;
import me.redraskal.arcadia.api.map.GameMap;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.boss.BarColor;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class GameVotingRunnable extends BukkitRunnable implements Listener {

    private final ArcadiaAPI api;
    private Map<Player, Class<? extends BaseGame>> votes = new HashMap<>();
    private Map<Integer, Class<? extends BaseGame>> slots = new HashMap<>();
    private int seconds = 16;

    public GameVotingRunnable() {
        this.api = Arcadia.getPlugin(Arcadia.class).getAPI();
        api.getGameManager().getMainBossBar().setColor(BarColor.YELLOW);
        List<Class<? extends BaseGame>> temp = api.getGameRegistry().getRegisteredGames();
        List<String> allowedGames = api.getPlugin().mainConfiguration.fetch().getStringList("games-in-voting");
        Iterator<Class<? extends BaseGame>> iterator = temp.iterator();
        while(iterator.hasNext()) {
            Class<? extends BaseGame> next = iterator.next();
            if(!allowedGames.contains(next.getName())) iterator.remove();
        }
        Collections.shuffle(temp);
        int slot = 2;
        for(int i=0; i<5; i++) {
            if(i < temp.size()) {
                slots.put(slot, temp.get(i));
                slot++;
            }
        }
        Bukkit.getOnlinePlayers().forEach(player -> {
            for(int i=0; i<9; i++) {
                Utils.setSpacer(player, i);
            }
            int s = 2;
            for(Class<? extends BaseGame> key : slots.values()) {
                final VotingData votingData = api.getGameRegistry().getVotingData(key);
                ItemStack itemStack = new ItemStack(votingData.getMaterialData().getItemType(), 1,
                    votingData.getMaterialData().getData());
                ItemMeta itemMeta = itemStack.getItemMeta();
                itemMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&b&l" + votingData.getGameName()));
                itemStack.setItemMeta(itemMeta);
                player.getInventory().setItem(s, itemStack);
                s++;
            }
        });
        api.getPlugin().getServer().getPluginManager().registerEvents(this, api.getPlugin());
        this.runTaskTimer(Arcadia.getPlugin(Arcadia.class), 0, 20L);
    }

    private void setVote(Player player) {
        if(votes.containsKey(player)) {
            if(votes.get(player) == slots.get(player.getInventory().getHeldItemSlot())) {
                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BASS, 1f, 1f);
            } else {
                player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1f, 1f);
                votes.put(player, slots.get(player.getInventory().getHeldItemSlot()));
            }
        } else {
            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1f, 1f);
            votes.put(player, slots.get(player.getInventory().getHeldItemSlot()));
        }
    }

    private int getVotes(int slot) {
        int result = 0;
        for(Map.Entry<Player, Class<? extends BaseGame>> entry : votes.entrySet()) {
            if(slots.get(slot) == entry.getValue()) {
                result++;
            }
        }
        return result;
    }

    private void updateVoting(int slot) {
        Bukkit.getOnlinePlayers().forEach(player -> {
            ItemStack itemStack = player.getInventory().getItem(slot);
            itemStack.setAmount(getVotes(slot));
            if(votes.containsKey(player)) {
                for(int i=2; i<7; i++) {
                    ItemStack temp = player.getInventory().getItem(i);
                    if(temp != null) {
                        if(temp.getType() != Material.AIR) {
                            if(slots.get(i) != votes.get(player)) {
                                if(temp.getEnchantments().containsKey(Enchantment.DURABILITY)) {
                                    temp.removeEnchantment(Enchantment.DURABILITY);
                                }
                                player.getInventory().setItem(i, temp);
                            } else {
                                temp.addUnsafeEnchantment(Enchantment.DURABILITY, 1);
                                player.getInventory().setItem(i, temp);
                            }
                        }
                    }
                }
            }
        });
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if(event.getItem() != null && event.getItem().getType() != Material.AIR) {
            if (event.getAction().toString().contains("RIGHT")) {
                if (event.getItem().getItemMeta() != null
                        && event.getItem().getItemMeta().hasDisplayName()) {
                    if(slots.containsKey(event.getPlayer().getInventory().getHeldItemSlot())) {
                        this.setVote(event.getPlayer());
                        this.updateVoting(event.getPlayer().getInventory().getHeldItemSlot());
                    }
                }
            }
        }
    }

    @Override
    public void run() {
        if(api.getGameManager().getGameState() != GameState.FINISHED) {
            this.cancel();
            return;
        }
        if(seconds <= 0) {
            this.cancel();
            HandlerList.unregisterAll(this);
            Map<String, Integer> finalVotes = new HashMap<>();
            for(Map.Entry<Player, Class<? extends BaseGame>> entry : votes.entrySet()) {
                if(finalVotes.containsKey(entry.getValue().getName())) {
                    finalVotes.put(entry.getValue().getName(), (finalVotes.get(entry.getValue().getName())+1));
                } else {
                    finalVotes.put(entry.getValue().getName(), 1);
                }
            }
            if(finalVotes.isEmpty()) {
                slots.values().forEach(game -> {
                    finalVotes.put(game.getName(), new Random().nextInt(5));
                });
            }
            List<Map.Entry<String, Integer>> sorted = Utils.entriesSortedByValues(finalVotes);
            //TODO: Some sort of cool animation idk
            api.getGameManager().nullifyGame();
            try {
                Class<? extends BaseGame> clazz
                    = (Class<? extends BaseGame>) Class.forName(sorted.get(0).getKey());
                List<GameMap> possibleMaps = api.getGameRegistry().getMaps(clazz);
                api.getGameManager().setCurrentGame(clazz,
                    possibleMaps.get(new Random().nextInt(possibleMaps.size())));
            } catch (Exception e) {
                e.printStackTrace();
            }
            return;
        } else {
            seconds--;
            if(seconds <= 3) {
                if(seconds > 0) {
                    for(Player player : Bukkit.getOnlinePlayers()) {
                        player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1f, 0.9f);
                    }
                } else {
                    for(Player player : Bukkit.getOnlinePlayers()) {
                        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_PLING, 1f, 1.1f);
                    }
                }
            }
            if(seconds > 0) {
                api.getGameManager().getMainBossBar().setTitle(ChatColor.translateAlternateColorCodes('&', "&6&lSwitching Game In: &c&l" + Utils.formatTimeFancy(0, seconds)));
                api.getGameManager().getMainBossBar().setProgress(1D-(double)seconds/15D);
            } else {
                api.getGameManager().getMainBossBar().setTitle(ChatColor.translateAlternateColorCodes('&', "&6&lLoading game..."));
                api.getGameManager().getMainBossBar().setProgress(1D);
            }
        }
        api.getGameManager().getCurrentGame().getSidebar().updateDisplayName(0, seconds);
    }
}