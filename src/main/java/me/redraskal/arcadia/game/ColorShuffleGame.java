package me.redraskal.arcadia.game;

import me.redraskal.arcadia.Arcadia;
import me.redraskal.arcadia.Utils;
import me.redraskal.arcadia.api.game.BaseGame;
import me.redraskal.arcadia.api.game.GameState;
import me.redraskal.arcadia.api.map.GameMap;
import me.redraskal.arcadia.api.scoreboard.SidebarSettings;
import me.redraskal.arcadia.api.scoreboard.WinMethod;
import me.redraskal.arcadia.api.scoreboard.defaults.PlayersLeftSidebar;
import org.apache.commons.lang.StringUtils;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MaterialData;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Random;

public class ColorShuffleGame extends BaseGame {

    private MaterialData[][] colors;
    private int squareSizes;
    private int squaresPerSide;
    private Location location;
    private int currentLevel = 0;

    public ColorShuffleGame(GameMap gameMap) {
        super(Arcadia.getPlugin(Arcadia.class).getAPI().getTranslationManager().fetchTranslation("game.colorshuffle.name").build(),
                new String[]{"startPosition", "floorLevel", "boardBoundsA", "boardBoundsB", "colors", "squareSizes"},
                new SidebarSettings(PlayersLeftSidebar.class,
                        WinMethod.LAST_PLAYER_STANDING, 1, 30), gameMap,
                Arcadia.getPlugin(Arcadia.class).getAPI().getTranslationManager().fetchTranslation("game.colorshuffle.desc").build());
    }

    @Override
    public void onPreStart() {
        Location spawnLocation = Utils.parseLocation((String) this.getGameMap().fetchSetting("startPosition"));
        for(Player player : Bukkit.getOnlinePlayers()) {
            if(!this.getAPI().getGameManager().isAlive(player)) continue;
            player.teleport(spawnLocation);
            player.setGameMode(GameMode.ADVENTURE);
        }
        this.squareSizes = Integer.parseInt((String) this.getGameMap().fetchSetting("squareSizes"));
        Location boardBoundsA = Utils.parseLocation((String) this.getGameMap().fetchSetting("boardBoundsA"));
        Location boardBoundsB = Utils.parseLocation((String) this.getGameMap().fetchSetting("boardBoundsB"));
        if(boardBoundsA.getX() > boardBoundsB.getX()
                && boardBoundsA.getZ() > boardBoundsB.getZ()) {
            this.squaresPerSide = (boardBoundsA.getBlockX() - boardBoundsB.getBlockX()) / squareSizes;
        } else {
            this.squaresPerSide = (boardBoundsB.getBlockX() - boardBoundsA.getBlockX()) / squareSizes;
        }
        this.squaresPerSide++;
        this.colors = new MaterialData[squaresPerSide][squaresPerSide];
        this.location = boardBoundsB;
        if(boardBoundsA.getX() < boardBoundsB.getX()
                && boardBoundsA.getZ() < boardBoundsB.getZ()) {
            location = boardBoundsA;
        }
    }

    @Override
    public void onGameStart() {
        this.nextEvent();
    }

    public void shuffleColors() {
        for (int x = 0; x < squaresPerSide; x++) {
            for (int z = 0; z < squaresPerSide; z++) {
                colors[x][z] = fetchRandomColor();
            }
        }
        for (int x = 0; x < squaresPerSide * squareSizes; x++) {
            int offsetX = location.getBlockX() + x;
            int squareX = x / 3;
            for (int z = 0; z < squaresPerSide * squareSizes; z++) {
                int offsetZ = location.getBlockZ() + z;
                int squareZ = z / 3;
                MaterialData materialData = colors[squareX][squareZ];
                Block block = new Location(location.getWorld(), offsetX, location.getY(), offsetZ).getBlock();
                block.setType(materialData.getItemType());
                block.setData(materialData.getData());
            }
        }
    }

    public void removeColors(MaterialData allowed) {
        for (int x = 0; x < squaresPerSide * squareSizes; x++) {
            int offsetX = location.getBlockX() + x;
            int squareX = x / 3;
            for (int z = 0; z < squaresPerSide * squareSizes; z++) {
                int offsetZ = location.getBlockZ() + z;
                int squareZ = z / 3;
                MaterialData materialData = colors[squareX][squareZ];
                if(materialData.getItemType() != allowed.getItemType()
                        || materialData.getData() != allowed.getData()) {
                    new Location(location.getWorld(), offsetX, location.getY(), offsetZ)
                        .getBlock().setType(Material.AIR);
                }
            }
        }
    }

    private MaterialData fetchRandomColor() {
        String[] data = ((String) this.getGameMap().fetchSetting("colors")).split(",");
        return Utils.parseMaterialData(data[new Random().nextInt(data.length)]);
    }

    public void nextEvent() {
        if(getAPI().getGameManager().getGameState() != GameState.INGAME) return;
        this.currentLevel++;
        this.shuffleColors();
        final int totalTicks = 100-(5*currentLevel);
        final MaterialData nextColor = fetchRandomColor();
        ItemStack itemStack = new ItemStack(nextColor.getItemType(), 1, nextColor.getData());
        ItemMeta itemMeta = itemStack.getItemMeta();
        if(itemStack.getType() == Material.WOOL) {
            DyeColor dye = DyeColor.getByWoolData(itemStack.getData().getData());
            String name = dye.toString().toLowerCase().replace("_", " ");
            name = StringUtils.capitalize(name);
            itemMeta.setDisplayName(Utils.convertDyeColor(dye) + "" + ChatColor.BOLD + name);
        } else {
            if(itemMeta.getLocalizedName() != null) {
                itemMeta.setDisplayName(ChatColor.YELLOW + "" + ChatColor.BOLD + itemMeta.getLocalizedName());
            }
        }
        itemStack.setItemMeta(itemMeta);
        Bukkit.getOnlinePlayers().forEach(player -> {
            if(getAPI().getGameManager().isAlive(player)) {
                player.setExp(1f);
                player.setLevel(currentLevel);
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
                    removeColors(nextColor);
                    Bukkit.getOnlinePlayers().forEach(player -> {
                        if(getAPI().getGameManager().isAlive(player)) {
                            player.setExp(0F);
                            for(int i=0; i<9; i++) {
                                player.getInventory().setItem(i, new ItemStack(Material.AIR, 1));
                            }
                        }
                    });
                    new BukkitRunnable() {
                        public void run() {
                            nextEvent();
                        }
                    }.runTaskLater(Arcadia.getPlugin(Arcadia.class), 40L);
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
    public void onPlayerMove(PlayerMoveEvent event) {
        if(this.getAPI().getGameManager().isAlive(event.getPlayer())) {
            if(event.getTo().getY() <= Integer.valueOf((String) this.getGameMap().fetchSetting("floorLevel"))) {
                this.getAPI().getGameManager().setAlive(event.getPlayer(), false);
            }
        }
    }

    @Override
    public void onGameEnd() {}
}