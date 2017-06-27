package me.redraskal.arcadia.game;

import me.redraskal.arcadia.Arcadia;
import me.redraskal.arcadia.Cuboid;
import me.redraskal.arcadia.Utils;
import me.redraskal.arcadia.api.game.BaseGame;
import me.redraskal.arcadia.api.game.GameState;
import me.redraskal.arcadia.api.map.GameMap;
import me.redraskal.arcadia.api.scoreboard.SidebarSettings;
import me.redraskal.arcadia.api.scoreboard.WinMethod;
import me.redraskal.arcadia.api.scoreboard.defaults.ScoreSidebar;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;
import org.bukkit.scheduler.BukkitRunnable;

public class KingOfTheHillGame extends BaseGame {

    private Cuboid hill;

    public KingOfTheHillGame(GameMap gameMap) {
        super(Arcadia.getPlugin(Arcadia.class).getAPI().getTranslationManager().fetchTranslation("game.koth.name").build(),
                new String[]{"startPosition", "hillBoundsA", "hillBoundsB", "item", "knockbackAmount"},
                new SidebarSettings(ScoreSidebar.class,
                        WinMethod.HIGHEST_SCORE, 1, 0), gameMap,
                Arcadia.getPlugin(Arcadia.class).getAPI().getTranslationManager().fetchTranslation("game.koth.desc").build());
    }

    @Override
    public void onPreStart() {
        Location spawnLocation = Utils.parseLocation((String) this.getGameMap().fetchSetting("startPosition"));
        MaterialData materialData = Utils.parseMaterialData((String) this.getGameMap().fetchSetting("item"));
        ItemStack itemStack = new ItemStack(materialData.getItemType(), 1, materialData.getData());
        itemStack.addUnsafeEnchantment(Enchantment.KNOCKBACK,
            Integer.parseInt((String) this.getGameMap().fetchSetting("knockbackAmount")));
        for(Player player : Bukkit.getOnlinePlayers()) {
            if(!this.getAPI().getGameManager().isAlive(player)) continue;
            player.teleport(spawnLocation);
            player.setGameMode(GameMode.ADVENTURE);
            player.getInventory().addItem(itemStack);
        }
        this.hill = new Cuboid(Utils.parseLocation((String) this.getGameMap().fetchSetting("hillBoundsA")),
            Utils.parseLocation((String) this.getGameMap().fetchSetting("hillBoundsB")));
    }

    @Override
    public void onGameStart() {
        this.allowPVP = true;
        new BukkitRunnable() {
            public void run() {
                if(getAPI().getGameManager().getGameState() != GameState.INGAME) {
                    this.cancel();
                    return;
                }
                for(Player player : Bukkit.getOnlinePlayers()) {
                    if(!getAPI().getGameManager().isAlive(player)) continue;
                    if(hill.contains(player.getLocation())) {
                        ScoreSidebar scoreSidebar = (ScoreSidebar) getSidebar();
                        scoreSidebar.setScore(player, (scoreSidebar.getScore(player)+1));
                    }
                }
            }
        }.runTaskTimer(this.getAPI().getPlugin(), 0, 20L);
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        event.setDamage(0);
    }

    @Override
    public void onGameEnd() {}
}