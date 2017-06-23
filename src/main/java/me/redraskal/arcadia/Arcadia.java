package me.redraskal.arcadia;

import me.redraskal.arcadia.api.game.BaseGame;
import me.redraskal.arcadia.api.map.GameMap;
import me.redraskal.arcadia.command.SpectateCommand;
import me.redraskal.arcadia.game.*;
import me.redraskal.arcadia.listener.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.List;
import java.util.Random;

public class Arcadia extends JavaPlugin {

    private ArcadiaAPI api;

    public void onEnable() {
        this.api = new ArcadiaAPI(this);

        this.getServer().getPluginManager().registerEvents(new ConnectionListener(), this);
        this.getServer().getPluginManager().registerEvents(new DamageListener(), this);
        this.getServer().getPluginManager().registerEvents(new WorldListener(), this);
        this.getServer().getPluginManager().registerEvents(new SpectatorListener(), this);
        this.getServer().getPluginManager().registerEvents(new ChatListener(), this);
        this.getServer().getPluginManager().registerEvents(new ItemListener(), this);

        this.getCommand("spec").setExecutor(new SpectateCommand());

        // Register Default Games (togglable later in the config)
        this.getAPI().getGameRegistry().registerGame(DeadEndGame.class);
        this.getAPI().getGameRegistry().registerGame(ColorShuffleGame.class);
        this.getAPI().getGameRegistry().registerGame(MineFieldGame.class);
        this.getAPI().getGameRegistry().registerGame(ElectricFloorGame.class);
        this.getAPI().getGameRegistry().registerGame(KingOfTheHillGame.class);

        final File mapFolder = new File(this.getDataFolder().getPath() + "/maps/");
        mapFolder.mkdirs();
        this.getAPI().getMapRegistry().loadMaps(mapFolder);

        // Custom rotation soontm
        for(Class<? extends BaseGame> game
                : this.getAPI().getGameRegistry().getRegisteredGames()) {
            this.getAPI().getGameManager().getRotation().addGame(game);
        }

        this.nextGameInRotation(true);
    }

    public void onDisable() {
        for(Player player : Bukkit.getOnlinePlayers()) {
            player.kickPlayer(ChatColor.RED + "Server is now restarting.");
        }
        removeCustomWorlds();
    }

    public void removeCustomWorlds() {
        if(this.getAPI().getMapRegistry().getCurrentWorld() != null) {
            this.getAPI().getMapRegistry().unloadWorld(this.getAPI().getMapRegistry().getCurrentWorld());
        }
        if(this.getAPI().getMapRegistry().oldWorld != null) {
            this.getAPI().getMapRegistry().unloadWorld(this.getAPI().getMapRegistry().oldWorld);
        }
    }

    /**
     * Switches to the next game in the rotation!
     */
    public void nextGameInRotation(boolean first) {
        if(!first) this.getAPI().getGameManager().getRotation().nextGame();
        List<GameMap> possibleMaps = this.getAPI().getGameRegistry().getMaps(this.getAPI().getGameManager().getRotation().getCurrentGame());
        this.getAPI().getGameManager().setCurrentGame(
                this.getAPI().getGameManager().getRotation().getCurrentGame(),
                possibleMaps.get(new Random().nextInt(possibleMaps.size())));
    }

    /**
     * Returns the fun API for this madness.
     * @return
     */
    public ArcadiaAPI getAPI() {
        return this.api;
    }
}