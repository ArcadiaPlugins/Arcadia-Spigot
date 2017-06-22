package me.redraskal.arcadia;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

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

    public static Location parseLocation(String location) {
        if(location.split(",").length > 3) {
            return new Location(Bukkit.getWorld("game"),
                    Double.valueOf(location.split(",")[0]), Double.valueOf(location.split(",")[1]), Double.valueOf(location.split(",")[2]),
                    Float.valueOf(location.split(",")[3]), Float.valueOf(location.split(",")[4]));
        } else {
            return new Location(Bukkit.getWorld("game"),
                    Double.valueOf(location.split(",")[0]), Double.valueOf(location.split(",")[1]), Double.valueOf(location.split(",")[2]));
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
}