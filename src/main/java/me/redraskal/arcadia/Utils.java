package me.redraskal.arcadia;

import com.google.common.collect.Maps;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;
import org.bukkit.potion.PotionEffect;

import java.io.File;
import java.lang.reflect.Method;
import java.util.*;

public class Utils {

    public static String formatTime(int minutes, int seconds) {
        String temp = "";
        if(minutes < 10) temp+="0";
        temp+=minutes;
        temp+=":";
        if(seconds < 10) temp+="0";
        temp+=seconds;
        return temp;
    }

    private static Map<DyeColor, ChatColor> dyeChatMap;
    static {
        dyeChatMap = Maps.newHashMap();
        dyeChatMap.put(DyeColor.BLACK, ChatColor.DARK_GRAY);
        dyeChatMap.put(DyeColor.BLUE, ChatColor.DARK_BLUE);
        dyeChatMap.put(DyeColor.BROWN, ChatColor.GOLD);
        dyeChatMap.put(DyeColor.CYAN, ChatColor.AQUA);
        dyeChatMap.put(DyeColor.GRAY, ChatColor.GRAY);
        dyeChatMap.put(DyeColor.GREEN, ChatColor.DARK_GREEN);
        dyeChatMap.put(DyeColor.LIGHT_BLUE, ChatColor.BLUE);
        dyeChatMap.put(DyeColor.LIME, ChatColor.GREEN);
        dyeChatMap.put(DyeColor.MAGENTA, ChatColor.LIGHT_PURPLE);
        dyeChatMap.put(DyeColor.ORANGE, ChatColor.GOLD);
        dyeChatMap.put(DyeColor.PINK, ChatColor.LIGHT_PURPLE);
        dyeChatMap.put(DyeColor.PURPLE, ChatColor.DARK_PURPLE);
        dyeChatMap.put(DyeColor.RED, ChatColor.DARK_RED);
        dyeChatMap.put(DyeColor.SILVER, ChatColor.GRAY);
        dyeChatMap.put(DyeColor.WHITE, ChatColor.WHITE);
        dyeChatMap.put(DyeColor.YELLOW, ChatColor.YELLOW);
    }

    public static ChatColor convertDyeColor(DyeColor dyeColor) {
        return dyeChatMap.get(dyeColor);
    }

    public static String parseWinner(Player winner) {
        if(winner == null) return "---";
        return winner.getName();
    }

    public static Location parseLocation(String location) {
        if(location.split(",").length > 3) {
            return new Location(Arcadia.getPlugin(Arcadia.class).getAPI().getMapRegistry().getCurrentWorld(),
                    Double.valueOf(location.split(",")[0]), Double.valueOf(location.split(",")[1]), Double.valueOf(location.split(",")[2]),
                    Float.valueOf(location.split(",")[3]), Float.valueOf(location.split(",")[4]));
        } else {
            return new Location(Arcadia.getPlugin(Arcadia.class).getAPI().getMapRegistry().getCurrentWorld(),
                    Double.valueOf(location.split(",")[0]), Double.valueOf(location.split(",")[1]), Double.valueOf(location.split(",")[2]));
        }
    }

    public static MaterialData parseMaterialData(String data) {
        if(data.split(":").length > 1) {
            return new MaterialData(Material.getMaterial(data.split(":")[0]),
                Integer.valueOf(data.split(":")[1]).byteValue());
        } else {
            return new MaterialData(Material.getMaterial(data.split(":")[0]));
        }
    }

    public static String getNMSVersion() {
        return Bukkit.getServer().getClass().getPackage().getName().replace(".",  ",").split(",")[3];
    }

    public static boolean fullyUnloadWorld(World world) {
        for(Chunk chunk : world.getLoadedChunks()) {
            chunk.unload(false);
        }
        if(Bukkit.unloadWorld(world, false)) {
            flushRegionFileCache();
            FileUtils.deleteDirectory(new File(Bukkit.getWorldContainer().getAbsolutePath(), world.getName()));
            return true;
        }
        return false;
    }

    public static void flushRegionFileCache() {
        try {
            Method method = Class.forName("net.minecraft.server." + getNMSVersion() + ".RegionFileCache").getMethod("a");
            method.invoke(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void resetPlayer(Player player) {
        player.setLevel(0);
        player.setExp(0);
        player.setFoodLevel(20);
        player.setHealth(20);
        player.setHealthScale(20);
        player.setExhaustion(0);
        player.getInventory().clear();
        player.getInventory().setHelmet(new ItemStack(Material.AIR, 1));
        player.getInventory().setChestplate(new ItemStack(Material.AIR, 1));
        player.getInventory().setLeggings(new ItemStack(Material.AIR, 1));
        player.getInventory().setBoots(new ItemStack(Material.AIR, 1));
        for(PotionEffect potionEffect : player.getActivePotionEffects()) {
            player.removePotionEffect(potionEffect.getType());
        }
    }

    public static List<Map.Entry<String, Integer>> entriesSortedByValues(Map<String,Integer> map) {
            List<Map.Entry<String,Integer>> sortedEntries = new ArrayList<>(map.entrySet());
            Collections.sort(sortedEntries,
                new Comparator<Map.Entry<String, Integer>>() {
                    @Override
                    public int compare(Map.Entry<String, Integer> e1, Map.Entry<String, Integer> e2) {
                        return e2.getValue().compareTo(e1.getValue());
                    }
                }
        );
        return sortedEntries;
    }
}