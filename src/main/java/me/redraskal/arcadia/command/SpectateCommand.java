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
                api.getTranslationManager().sendTranslation("command.spec.enabled", player);
            } else {
                api.getTranslationManager().sendTranslation("command.spec.disabled", player);
            }
            if(api.getGameManager().getCurrentGame() != null) {
                if(!api.getGameManager().getCurrentGame().spectatorCache.contains(player)) {
                    api.getGameManager().getCurrentGame().spectatorCache.add(player);
                }
            }
        }
        return false;
    }
}