package me.redraskal.arcadia.command;

import me.redraskal.arcadia.Arcadia;
import me.redraskal.arcadia.ArcadiaAPI;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SpectateCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if(s.equalsIgnoreCase("spec")) {
            if(!(commandSender instanceof Player)) {
                commandSender.sendMessage(ChatColor.RED + "You must be a player to execute this command.");
                return false;
            }
            Player player = (Player) commandSender;
            ArcadiaAPI api = Arcadia.getPlugin(Arcadia.class).getAPI();
            api.getGameManager().setSpectating(player, !api.getGameManager().isSpectating(player));
            if(api.getGameManager().isSpectating(player)) {
                player.sendMessage(ChatColor.GRAY + "You are now spectating the game. Type " + ChatColor.GREEN + "/spec " + ChatColor.GRAY + "again to play.");
            } else {
                player.sendMessage(ChatColor.GRAY + "You will be entered into the next game as a player.");
            }
        }
        return false;
    }
}