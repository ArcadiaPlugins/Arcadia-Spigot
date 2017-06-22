package me.redraskal.arcadia;

import me.redraskal.arcadia.api.game.BaseGame;
import me.redraskal.arcadia.api.map.GameMap;
import me.redraskal.arcadia.game.DeadEndGame;
import me.redraskal.arcadia.listener.ConnectionListener;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.List;
import java.util.Random;

public class Arcadia extends JavaPlugin {

    private ArcadiaAPI api;

    public void onEnable() {
        this.api = new ArcadiaAPI(this);
        //TODO: Actually make stuff work properly

        this.getServer().getPluginManager().registerEvents(new ConnectionListener(), this);

        // Register Default Games (togglable later in the config)
        this.getAPI().getGameRegistry().registerGame(DeadEndGame.class);

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
        if(this.getAPI().getMapRegistry().getCurrentWorld() != null) {
            this.getAPI().getMapRegistry().unloadWorld();
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