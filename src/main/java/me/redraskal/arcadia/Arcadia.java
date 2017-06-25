package me.redraskal.arcadia;

import me.redraskal.arcadia.api.game.BaseGame;
import me.redraskal.arcadia.api.game.GameState;
import me.redraskal.arcadia.api.game.RotationOrder;
import me.redraskal.arcadia.api.map.GameMap;
import me.redraskal.arcadia.command.SpectateCommand;
import me.redraskal.arcadia.listener.*;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.List;
import java.util.Random;

public class Arcadia extends JavaPlugin {

    private ArcadiaAPI api;
    public Configuration mainConfiguration;

    public void onEnable() {
        this.api = new ArcadiaAPI(this);
        new File(this.getDataFolder().getPath() + "/translations/").mkdirs();
        if(new File(this.getDataFolder().getPath() + "/translations/").listFiles().length == 0) {
            this.getAPI().getTranslationManager().saveDefaultLocale("en_us.properties");
        }
        this.getAPI().getTranslationManager().refreshCache();

        this.getServer().getPluginManager().registerEvents(new ConnectionListener(), this);
        this.getServer().getPluginManager().registerEvents(new DamageListener(), this);
        this.getServer().getPluginManager().registerEvents(new WorldListener(), this);
        this.getServer().getPluginManager().registerEvents(new SpectatorListener(), this);
        this.getServer().getPluginManager().registerEvents(new ChatListener(), this);
        this.getServer().getPluginManager().registerEvents(new ItemListener(), this);

        this.getCommand("spec").setExecutor(new SpectateCommand());

        this.mainConfiguration = new Configuration(this.getDataFolder(), "config.yml", this);
        this.mainConfiguration.copyDefaults();

        this.getAPI().getTranslationManager().setDefaultLocale(mainConfiguration.fetch().getString("language"));
        this.getAPI().getTranslationManager().autoDetectLanguage
            = this.mainConfiguration.fetch().getBoolean("auto-detect-language");

        this.mainConfiguration.fetch().getStringList("default-rotation").forEach(line -> {
            try {
                Class<? extends BaseGame> clazz = (Class<? extends BaseGame>) Class.forName(line);
                if(this.getAPI().getGameRegistry().registerGame(clazz)) {
                    this.getAPI().getGameManager().getRotation().addGame(clazz);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        this.mainConfiguration.fetch().getStringList("map-directories").forEach(line -> {
            File mapFolder = new File(line.replace("%data_folder%", this.getDataFolder().getPath()));
            mapFolder.mkdirs();
            this.getAPI().getMapRegistry().loadMaps(mapFolder);
        });

        if(this.mainConfiguration.fetch().getBoolean("randomize")) {
            this.getAPI().getGameManager().getRotation().setRotationOrder(RotationOrder.RANDOM);
        }
        if(this.mainConfiguration.fetch().getBoolean("allow-game-voting")) {
            this.getAPI().getGameManager().getRotation().setRotationOrder(RotationOrder.VOTE);
        }
        this.nextGameInRotation(true);
    }

    public void onDisable() {
        this.getAPI().getGameManager().setGameState(GameState.FINISHED);
        for(Player player : Bukkit.getOnlinePlayers()) {
            if(player.getVehicle() != null) {
                Entity vehicle = player.getVehicle();
                vehicle.eject();
                vehicle.remove();
            }
            this.getAPI().getGameManager().setAlive(player, false);
        }
        this.getAPI().getTranslationManager().kickPlayers("common.server-restarting");
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