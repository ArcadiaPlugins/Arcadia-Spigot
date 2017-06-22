package me.redraskal.arcadia.api.game;

import com.google.common.base.Preconditions;
import me.redraskal.arcadia.Arcadia;
import me.redraskal.arcadia.ArcadiaAPI;
import me.redraskal.arcadia.Utils;
import me.redraskal.arcadia.api.map.GameMap;
import me.redraskal.arcadia.runnable.PreGameRunnable;
import org.bukkit.Bukkit;
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
        try {
            this.currentGame = registeredGame.newInstance();
            this.gameState = GameState.STARTING;
            for(String requiredSetting : currentGame.getRequiredSettings()) {
                if(!gameMap.doesSettingExist(requiredSetting)) return false;
            }
            api.getMapRegistry().loadWorld(gameMap);
            new PreGameRunnable();
            Bukkit.getServer().getPluginManager().registerEvents(currentGame, Arcadia.getPlugin(Arcadia.class));
            return true;
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
        if(this.currentGame == null) return false;
        this.gameState = GameState.FINISHED;
        HandlerList.unregisterAll(this.currentGame);
        this.currentGame.onGameEnd();
        this.currentGame.getSidebar().getSidebar().unregister();
        //TODO: Fun sound effect thing/winners announced
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
            player.setAllowFlight(false);
            player.setFlying(false);
            if(!alive.contains(player)) alive.add(player);
        } else {
            player.setAllowFlight(true);
            player.setFlying(true);
            if(Arcadia.getPlugin(Arcadia.class).getAPI()
                .getMapRegistry().getCurrentWorld() != null && this.currentGame != null) {
                player.teleport(
                    Utils.parseLocation((String) this.currentGame.getGameMap().fetchSetting("spectatorLocation")));
            }
            if(alive.contains(player)) alive.remove(player);
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
            spec.remove(player);
        }
        this.setAlive(player, false);
    }

    /**
     * Returns the amount of players alive.
     * @return
     */
    public int getPlayersAlive() {
        return this.alive.size();
    }
}