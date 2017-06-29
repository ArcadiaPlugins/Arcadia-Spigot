package me.redraskal.arcadia;

import com.google.common.collect.Maps;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
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

    public static String formatTimeFancy(int minutes, int seconds) {
        String temp = "";
        if(minutes > 0) temp+=minutes+"m";
        if(seconds > 0) temp+=seconds+"s";
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
        if(winner == null) return Arcadia.getPlugin(Arcadia.class).getAPI().getTranslationManager()
            .fetchTranslation("ui.unknown-player").build();
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
        ItemStack blankItem = new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) 15);
        ItemMeta blankMeta = blankItem.getItemMeta();
        blankMeta.setDisplayName("" + ChatColor.RED);
        blankItem.setItemMeta(blankMeta);
        for(int i=9; i<=35; i++) {
            player.getInventory().setItem(i, blankItem);
        }
        player.getInventory().setHelmet(new ItemStack(Material.AIR, 1));
        player.getInventory().setChestplate(new ItemStack(Material.AIR, 1));
        player.getInventory().setLeggings(new ItemStack(Material.AIR, 1));
        player.getInventory().setBoots(new ItemStack(Material.AIR, 1));
        for(PotionEffect potionEffect : player.getActivePotionEffects()) {
            player.removePotionEffect(potionEffect.getType());
        }
        player.setFireTicks(0);
    }

    public static void setSpacer(Player player, int slot) {
        ItemStack blankItem = new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) 15);
        ItemMeta blankMeta = blankItem.getItemMeta();
        blankMeta.setDisplayName("" + ChatColor.RED);
        blankItem.setItemMeta(blankMeta);
        player.getInventory().setItem(slot, blankItem);
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

    public static org.bukkit.util.Vector getRandomCircleVector() {
        double rnd = new Random().nextDouble() * 2.0D * Math.PI;
        double x = Math.cos(rnd);
        double z = Math.sin(rnd);
        return new org.bukkit.util.Vector(x, 0.0D, z);
    }

    public static Location getLocationAroundCircle(Location center, double radius, double angleInRadian) {
        double x = center.getX() + radius * Math.cos(angleInRadian);
        double z = center.getZ() + radius * Math.sin(angleInRadian);
        double y = center.getY();

        Location loc = new Location(center.getWorld(), x, y, z);
        org.bukkit.util.Vector difference = center.toVector().clone().subtract(loc.toVector());
        loc.setDirection(difference);

        return loc;
    }

    public static void showNotification(String title, String icon,
            AdvancementAPI.FrameType frameType, Player... players) {
        AdvancementAPI.builder(new NamespacedKey("test", "custom/" + UUID.randomUUID().toString()))
                .title(title)
                .description("")
                .icon(icon)
                .hidden(false)
                .toast(true)
                .announce(false)
                .trigger(AdvancementAPI.Trigger.builder(AdvancementAPI.Trigger.TriggerType.IMPOSSIBLE, "default"))
                .background("minecraft:textures/gui/advancements/backgrounds/stone.png")
                .frame(frameType)
                .build().show(Arcadia.getPlugin(Arcadia.class), players);
    }
}