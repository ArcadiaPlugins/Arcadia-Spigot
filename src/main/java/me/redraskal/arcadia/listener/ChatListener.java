package me.redraskal.arcadia.listener;

import me.redraskal.arcadia.Arcadia;
import me.redraskal.arcadia.api.translation.Translation;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatListener implements Listener {

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Translation translation = Arcadia.getPlugin(Arcadia.class).getAPI()
            .getTranslationManager().fetchTranslation("common.chat-message", event.getPlayer());
        if(translation != null) {
            final String message = translation.build("%s", "%s");
            if(!message.isEmpty()) event.setFormat(message);
        }
    }
}