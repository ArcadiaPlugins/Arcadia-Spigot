package me.redraskal.arcadia;

import me.redraskal.arcadia.api.game.BaseGame;
import me.redraskal.arcadia.api.game.GameState;
import me.redraskal.arcadia.api.game.RotationOrder;
import me.redraskal.arcadia.api.game.VotingData;
import me.redraskal.arcadia.api.map.GameMap;
import me.redraskal.arcadia.command.SetGameCommand;
import me.redraskal.arcadia.command.SpectateCommand;
import me.redraskal.arcadia.game.*;
import me.redraskal.arcadia.listener.*;
import me.redraskal.arcadia.support.UltraCosmeticsSupport;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.material.MaterialData;
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
        this.getCommand("setgame").setExecutor(new SetGameCommand());

        this.mainConfiguration = new Configuration(this.getDataFolder(), "config.yml", this);
        this.mainConfiguration.copyDefaults();

        this.getAPI().getTranslationManager().setDefaultLocale(mainConfiguration.fetch().getString("language"));
        this.getAPI().getTranslationManager().autoDetectLanguage
            = this.mainConfiguration.fetch().getBoolean("auto-detect-language");

        this.getAPI().getGameRegistry().registerGame(BombardmentGame.class);
        this.getAPI().getGameRegistry().registerGame(ColorShuffleGame.class);
        this.getAPI().getGameRegistry().registerGame(DeadEndGame.class);
        this.getAPI().getGameRegistry().registerGame(ElectricFloorGame.class);
        this.getAPI().getGameRegistry().registerGame(HorseRaceGame.class);
        this.getAPI().getGameRegistry().registerGame(KingOfTheHillGame.class);
        this.getAPI().getGameRegistry().registerGame(MineFieldGame.class);
        this.getAPI().getGameRegistry().registerGame(MusicalMinecartsGame.class);
        this.getAPI().getGameRegistry().registerGame(PotionDropGame.class);
        this.getAPI().getGameRegistry().registerGame(RainbowJumpGame.class);
        this.getAPI().getGameRegistry().registerGame(RedLightGreenLightGame.class);
        this.getAPI().getGameRegistry().registerGame(SpleefGame.class);
        this.getAPI().getGameRegistry().registerGame(TrampolinioGame.class);
        this.getAPI().getGameRegistry().registerGame(WingRushGame.class);

        this.getAPI().getGameRegistry().setVotingData(BombardmentGame.class,
            new VotingData(new MaterialData(Material.COAL_BLOCK),
                getAPI().getTranslationManager().fetchTranslation("game.bombardment.name").build()));
        this.getAPI().getGameRegistry().setVotingData(ColorShuffleGame.class,
                new VotingData(new MaterialData(Material.WOOL),
                        getAPI().getTranslationManager().fetchTranslation("game.colorshuffle.name").build()));
        this.getAPI().getGameRegistry().setVotingData(DeadEndGame.class,
                new VotingData(new MaterialData(Material.GOLD_BLOCK),
                        getAPI().getTranslationManager().fetchTranslation("game.deadend.name").build()));
        this.getAPI().getGameRegistry().setVotingData(ElectricFloorGame.class,
                new VotingData(new MaterialData(Material.STAINED_GLASS, (byte) 14),
                        getAPI().getTranslationManager().fetchTranslation("game.electricfloor.name").build()));
        this.getAPI().getGameRegistry().setVotingData(HorseRaceGame.class,
                new VotingData(new MaterialData(Material.GOLD_BARDING),
                        getAPI().getTranslationManager().fetchTranslation("game.horserace.name").build()));
        this.getAPI().getGameRegistry().setVotingData(KingOfTheHillGame.class,
                new VotingData(new MaterialData(Material.RAW_FISH),
                        getAPI().getTranslationManager().fetchTranslation("game.koth.name").build()));
        this.getAPI().getGameRegistry().setVotingData(MineFieldGame.class,
                new VotingData(new MaterialData(Material.STONE_PLATE),
                        getAPI().getTranslationManager().fetchTranslation("game.minefield.name").build()));
        this.getAPI().getGameRegistry().setVotingData(MusicalMinecartsGame.class,
                new VotingData(new MaterialData(Material.MINECART),
                        getAPI().getTranslationManager().fetchTranslation("game.musicalminecarts.name").build()));
        this.getAPI().getGameRegistry().setVotingData(PotionDropGame.class,
                new VotingData(new MaterialData(Material.POTION, (byte) 8197),
                        getAPI().getTranslationManager().fetchTranslation("game.potiondrop.name").build()));
        this.getAPI().getGameRegistry().setVotingData(RainbowJumpGame.class,
                new VotingData(new MaterialData(Material.WOOL, (byte) 10),
                        getAPI().getTranslationManager().fetchTranslation("game.rainbowjump.name").build()));
        this.getAPI().getGameRegistry().setVotingData(RedLightGreenLightGame.class,
                new VotingData(new MaterialData(Material.WOOL, (byte) 14),
                        getAPI().getTranslationManager().fetchTranslation("game.redlightgreenlight.name").build()));
        this.getAPI().getGameRegistry().setVotingData(SpleefGame.class,
                new VotingData(new MaterialData(Material.SNOW_BLOCK),
                        getAPI().getTranslationManager().fetchTranslation("game.spleef.name").build()));
        this.getAPI().getGameRegistry().setVotingData(TrampolinioGame.class,
                new VotingData(new MaterialData(Material.WOOL, (byte) 15),
                        getAPI().getTranslationManager().fetchTranslation("game.trampolinio.name").build()));
        this.getAPI().getGameRegistry().setVotingData(WingRushGame.class,
                new VotingData(new MaterialData(Material.ELYTRA),
                        getAPI().getTranslationManager().fetchTranslation("game.wingrush.name").build()));

        this.mainConfiguration.fetch().getStringList("default-rotation").forEach(line -> {
            try {
                Class<? extends BaseGame> clazz = (Class<? extends BaseGame>) Class.forName(line);
                this.getAPI().getGameManager().getRotation().addGame(clazz);
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
        if(this.mainConfiguration.fetch().getBoolean("randomize-first-rotation")) {
            this.getAPI().getGameManager().getRotation().shuffle();
        }
        this.nextGameInRotation(true);

        if(this.getServer().getPluginManager().isPluginEnabled("UltraCosmetics")) {
            this.getServer().getPluginManager().registerEvents(new UltraCosmeticsSupport(), this);
        }
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