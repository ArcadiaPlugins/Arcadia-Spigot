package me.redraskal.arcadia.api.game;

import com.google.common.base.Preconditions;
import me.redraskal.arcadia.Arcadia;
import me.redraskal.arcadia.ArcadiaAPI;
import me.redraskal.arcadia.Utils;
import me.redraskal.arcadia.api.game.event.GameEndEvent;
import me.redraskal.arcadia.api.game.event.GameStateUpdateEvent;
import me.redraskal.arcadia.api.game.event.PlayerAliveStatusEvent;
import me.redraskal.arcadia.api.map.GameMap;
import me.redraskal.arcadia.api.music.defaults.EndGameMusic;
import me.redraskal.arcadia.runnable.GameSwitchRunnable;
import me.redraskal.arcadia.runnable.GameVotingRunnable;
import me.redraskal.arcadia.runnable.PreGameRunnable;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GameManager {

    private Rotation rotation = new Rotation();
    private BaseGame currentGame;
    private GameState gameState = GameState.STARTING;
    private List<Player> alive = new ArrayList<>();
    private List<Player> spec = new ArrayList<>();
    private BossBar mainBossBar = Bukkit.createBossBar("Waiting...", BarColor.BLUE, BarStyle.SOLID);

    /**
     * Returns the main BossBar.
     * @return
     */
    public BossBar getMainBossBar() {
        return this.mainBossBar;
    }

    /**
     * Returns the current Rotation.
     * @return
     */
    public Rotation getRotation() {
        return this.rotation;
    }

    /**
     * Returns the current BaseGame.
     * @return
     */
    public BaseGame getCurrentGame() {
        return this.currentGame;
    }

    public boolean setCurrentGame(Class<? extends BaseGame> registeredGame, GameMap gameMap) {
        Preconditions.checkNotNull(registeredGame, "Game cannot be null");
        final ArcadiaAPI api = Arcadia.getPlugin(Arcadia.class).getAPI();
        if(!api.getGameRegistry().getRegisteredGames().contains(registeredGame)) return false;
        if(!api.getGameRegistry().getMaps(registeredGame).contains(gameMap)) return false;
        this.endGame();
        this.nullifyGame();
        try {
            this.currentGame = registeredGame.getConstructor(GameMap.class).newInstance(gameMap);
            this.gameState = GameState.STARTING;
            for(String requiredSetting : currentGame.getRequiredSettings()) {
                if(!gameMap.doesSettingExist(requiredSetting)) return false;
            }
            if(api.getMapRegistry().loadWorld(gameMap) != null) {
                new PreGameRunnable();
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Attempts to end the current BaseGame.
     * @return
     */
    public boolean endGame() {
        if(this.gameState == GameState.FINISHED || this.currentGame == null) return false;
        this.gameState = GameState.FINISHED;
        final ArcadiaAPI api = Arcadia.getPlugin(Arcadia.class).getAPI();
        HandlerList.unregisterAll(this.currentGame);
        HandlerList.unregisterAll(this.currentGame.getSidebar());
        this.currentGame.onGameEnd();
        this.currentGame.allowPVP = false;
        new EndGameMusic();

        String firstPlace = Utils.parseWinner(this.currentGame.getSidebarSettings().getWinMethod().calculateWinner(1));
        String secondPlace = Utils.parseWinner(this.currentGame.getSidebarSettings().getWinMethod().calculateWinner(2));
        String thirdPlace = Utils.parseWinner(this.currentGame.getSidebarSettings().getWinMethod().calculateWinner(3));

        String playerColor = api.getTranslationManager().fetchTranslation("ui.player-color").build();
        String firstTranslation = api.getTranslationManager().fetchTranslation("ui.first").build();
        String secondTranslation = api.getTranslationManager().fetchTranslation("ui.second").build();
        String thirdTranslation = api.getTranslationManager().fetchTranslation("ui.third").build();
        String placeTranslation = " " + api.getTranslationManager().fetchTranslation("ui.place").build();

        Bukkit.broadcastMessage(firstTranslation + placeTranslation + ": " + playerColor + firstPlace);
        Bukkit.broadcastMessage(secondTranslation + placeTranslation + ": " + playerColor + secondPlace);
        Bukkit.broadcastMessage(thirdTranslation + placeTranslation + ": " + playerColor + thirdPlace);
        Bukkit.getOnlinePlayers().forEach(player -> {
            player.sendTitle(firstTranslation + ": " + playerColor + firstPlace,
                    secondTranslation + ": " + playerColor + secondPlace + ", "
                    + thirdTranslation + ": " + playerColor + thirdPlace, 0, 80, 20);
        });
        Bukkit.getServer().getPluginManager().callEvent(new GameEndEvent());
        return true;
    }

    public boolean nullifyGame() {
        if(this.currentGame == null) return false;
        this.currentGame.getSidebar().getSidebar().unregister();
        this.currentGame = null;
        return true;
    }

    /**
     * Returns the current GameState.
     * @return
     */
    public GameState getGameState() {
        return this.gameState;
    }

    /**
     * Sets the current GameState.
     * @param gameState
     */
    public void setGameState(GameState gameState) {
        this.gameState = gameState;
        Bukkit.getServer().getPluginManager().callEvent(new GameStateUpdateEvent(this.gameState));
    }

    /**
     * Returns true is the specified player is alive.
     * @param player
     * @return
     */
    public boolean isAlive(Player player) {
        return this.alive.contains(player);
    }

    /**
     * Returns a list of alive players.
     * @return
     */
    public List<Player> getAlivePlayers() {
        return this.alive;
    }

    /**
     * Returns true if the specified player is in Spectator Mode.
     * @param player
     * @return
     */
    public boolean isSpectating(Player player) {
        return this.spec.contains(player);
    }

    /**
     * Toggles whether a player is dead/alive.
     * @param player
     * @param toggle
     */
    public void setAlive(Player player, boolean toggle) {
        if(toggle) {
            player.setGameMode(GameMode.ADVENTURE);
            player.setAllowFlight(false);
            player.setFlying(false);
            if(!alive.contains(player)) alive.add(player);
            if(this.isSpectating(player)) this.setSpectating(player, false);
        } else {
            if(alive.contains(player)) alive.remove(player);
            if(player.getVehicle() != null) {
                Entity vehicle = player.getVehicle();
                vehicle.eject();
                vehicle.remove();
            }
            Utils.resetPlayer(player);

            ItemStack itemStack = new ItemStack(Material.GOLD_INGOT, 1);
            ItemMeta itemMeta = itemStack.getItemMeta();

            itemMeta.setDisplayName(ChatColor.GOLD + "Party Shop");
            itemMeta.setLore(Arrays.asList(new String[]{ChatColor.GRAY + "Spend coins on awesome things"}));
            itemStack.setItemMeta(itemMeta);
            player.getInventory().setItem(1, itemStack);

            itemStack = new ItemStack(Material.COMPASS, 1);
            itemMeta = itemStack.getItemMeta();
            itemMeta.setDisplayName(ChatColor.GOLD + "Cosmetics");
            itemStack.setItemMeta(itemMeta);
            player.getInventory().setItem(6, itemStack);

            itemStack = new ItemStack(Material.BED, 1, (byte) 14);
            itemMeta = itemStack.getItemMeta();
            itemMeta.setDisplayName(ChatColor.RED + "Go to Party Lobby");
            itemMeta.setLore(Arrays.asList(new String[]{ChatColor.GRAY + "Come back soon!"}));
            itemStack.setItemMeta(itemMeta);
            player.getInventory().setItem(7, itemStack);

            player.setAllowFlight(true);
            player.setFlying(true);
            if(Arcadia.getPlugin(Arcadia.class).getAPI()
                .getMapRegistry().getCurrentWorld() != null && this.currentGame != null) {
                player.teleport(
                    Utils.parseLocation((String) this.currentGame.getGameMap().fetchSetting("spectatorLocation")));
            }
        }
        Bukkit.getServer().getPluginManager().callEvent(new PlayerAliveStatusEvent(player, toggle, false));
        if(this.getPlayersAlive() <= 0 && this.getGameState() == GameState.INGAME) {
            for(Player other : Bukkit.getOnlinePlayers()) {
                if(this.isAlive(other)) this.setAlive(other, false);
            }
            if(this.endGame()) {
                if(Arcadia.getPlugin(Arcadia.class).mainConfiguration.fetch().getBoolean("allow-game-voting")) {
                    new GameVotingRunnable();
                } else {
                    new GameSwitchRunnable();
                }
            }
        }
        if(this.getPlayersAlive() <= 1 && this.getGameState() == GameState.INGAME && Bukkit.getOnlinePlayers().size() > 1) {
            for(Player other : Bukkit.getOnlinePlayers()) {
                if(this.isAlive(other)) this.setAlive(other, false);
            }
            this.endGame();
            if(Arcadia.getPlugin(Arcadia.class).mainConfiguration.fetch().getBoolean("allow-game-voting")) {
                new GameVotingRunnable();
            } else {
                new GameSwitchRunnable();
            }
        }
    }

    /**
     * Toggles whether a player is in Spectator Mode.
     * @param player
     * @param toggle
     */
    public void setSpectating(Player player, boolean toggle) {
        if(toggle && spec.contains(player)) return;
        if(!toggle && !spec.contains(player)) return;
        if(toggle) {
            player.setAllowFlight(true);
            player.setFlying(true);
            player.setGameMode(GameMode.SPECTATOR);
            spec.add(player);
        } else {
            player.setGameMode(GameMode.ADVENTURE);
            spec.remove(player);
        }
        this.setAlive(player, false);
        Bukkit.getServer().getPluginManager().callEvent(new PlayerAliveStatusEvent(player, false, toggle));
    }

    /**
     * Returns the amount of players alive.
     * @return
     */
    public int getPlayersAlive() {
        return this.alive.size();
    }
}