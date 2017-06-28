package me.redraskal.arcadia.command;

import me.redraskal.arcadia.Arcadia;
import me.redraskal.arcadia.ArcadiaAPI;
import me.redraskal.arcadia.api.game.BaseGame;
import me.redraskal.arcadia.api.map.GameMap;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
import java.util.Random;

public class SetGameCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if(s.equalsIgnoreCase("setgame")) {
            if(!commandSender.isOp()) {
                commandSender.sendMessage(ChatColor.RED + "You do not have permission to execute this command.");
                return false;
            }
            if(strings.length <= 0) {
                commandSender.sendMessage(ChatColor.RED + "Please supply a class name. Ex: me.redraskal.arcadia.game.BombardmentGame");
                return false;
            }
            final ArcadiaAPI api = Arcadia.getPlugin(Arcadia.class).getAPI();
            try {
                Class<? extends BaseGame> clazz = (Class<? extends BaseGame>) Class.forName(strings[0]);
                api.getGameManager().endGame();
                new BukkitRunnable() {
                    public void run() {
                        api.getGameManager().nullifyGame();
                        List<GameMap> possibleMaps = api.getGameRegistry().getMaps(clazz);
                        api.getGameManager().setCurrentGame(clazz, possibleMaps.get(new Random().nextInt(possibleMaps.size())));
                    }
                }.runTaskLater(api.getPlugin(), 60L);
                commandSender.sendMessage(ChatColor.GREEN + "Game has been changed.");
            } catch (Exception e) {
                commandSender.sendMessage(ChatColor.RED + "Invalid class name.");
            }
        }
        return false;
    }
}