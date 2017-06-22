package me.redraskal.arcadia.listener;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatListener implements Listener {

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        event.setFormat(ChatColor.GRAY + "%s " + ChatColor.DARK_GRAY + "»" + ChatColor.GRAY + " %s");
    }
}