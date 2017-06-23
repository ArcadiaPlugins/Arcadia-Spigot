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
import me.redraskal.arcadia.runnable.PreGameRunnable;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

import java.util.ArrayList;
import java.util.List;

public class GameManager {

    private Rotation rotation = new Rotation();
    private BaseGame currentGame;
    private GameState gameState = GameState.STARTING;
    private List<Player> alive = new ArrayList<>();
    private List<Player> spec = new ArrayList<>();

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
        HandlerList.unregisterAll(this.currentGame);
        HandlerList.unregisterAll(this.currentGame.getSidebar());
        this.currentGame.onGameEnd();
        this.currentGame.allowPVP = false;
        new EndGameMusic();
        Bukkit.broadcastMessage(ChatColor.YELLOW + "1st place: " + ChatColor.WHITE
            + Utils.parseWinner(this.currentGame.getSidebarSettings().getWinMethod().calculateWinner(1)));
        Bukkit.broadcastMessage(ChatColor.GRAY + "2nd place: " + ChatColor.WHITE
            + Utils.parseWinner(this.currentGame.getSidebarSettings().getWinMethod().calculateWinner(2)));
        Bukkit.broadcastMessage(ChatColor.RED + "3rd place: " + ChatColor.WHITE
            + Utils.parseWinner(this.currentGame.getSidebarSettings().getWinMethod().calculateWinner(3)));
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
            Utils.resetPlayer(player);
            //TODO: Fun inventory
            player.setAllowFlight(true);
            player.setFlying(true);
            if(Arcadia.getPlugin(Arcadia.class).getAPI()
                .getMapRegistry().getCurrentWorld() != null && this.currentGame != null) {
                player.teleport(
                    Utils.parseLocation((String) this.currentGame.getGameMap().fetchSetting("spectatorLocation")));
            }
            if(alive.contains(player)) alive.remove(player);
        }
        Bukkit.getServer().getPluginManager().callEvent(new PlayerAliveStatusEvent(player, toggle, false));
        if(this.getPlayersAlive() <= 0 && this.getGameState() == GameState.INGAME) {
            for(Player other : Bukkit.getOnlinePlayers()) {
                if(this.isAlive(other)) this.setAlive(other, false);
            }
            if(this.endGame()) {
                new GameSwitchRunnable();
            }
        }
        if(this.getPlayersAlive() <= 1 && this.getGameState() == GameState.INGAME && Bukkit.getOnlinePlayers().size() > 1) {
            for(Player other : Bukkit.getOnlinePlayers()) {
                if(this.isAlive(other)) this.setAlive(other, false);
            }
            this.endGame();
            new GameSwitchRunnable();
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