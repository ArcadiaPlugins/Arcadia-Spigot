package me.redraskal.arcadia.support;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.cosmetics.emotes.Emote;
import be.isach.ultracosmetics.cosmetics.gadgets.Gadget;
import be.isach.ultracosmetics.cosmetics.hats.Hat;
import be.isach.ultracosmetics.cosmetics.particleeffects.ParticleEffect;
import be.isach.ultracosmetics.cosmetics.suits.ArmorSlot;
import be.isach.ultracosmetics.cosmetics.suits.Suit;
import be.isach.ultracosmetics.player.UltraPlayer;
import me.redraskal.arcadia.api.game.event.GameLoadEvent;
import me.redraskal.arcadia.api.game.event.PlayerAliveStatusEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.HashMap;
import java.util.Map;

public class UltraCosmeticsSupport implements Listener {

    private final UltraCosmetics plugin = UltraCosmetics.getPlugin(UltraCosmetics.class);

    @EventHandler
    public void onAliveStatusChange(PlayerAliveStatusEvent event) {
        this.resetPlayer(this.plugin.getPlayerManager().getUltraPlayer(event.getPlayer()));
    }

    @EventHandler
    public void onGameLoad(GameLoadEvent event) {
        this.plugin.getPlayerManager().getUltraPlayers().forEach(player -> {
            this.resetPlayer(player);
        });
    }

    private void resetPlayer(UltraPlayer player) {
        final Gadget gadget = player.getCurrentGadget();
        final ParticleEffect particleEffect = player.getCurrentParticleEffect();
        final Hat hat = player.getCurrentHat();
        final Emote emote = player.getCurrentEmote();
        Map<ArmorSlot, Suit> suit = new HashMap<>();
        for(ArmorSlot armorSlot : ArmorSlot.values()) {
            Suit temp = player.getSuit(armorSlot);
            if(temp != null) suit.put(armorSlot, temp);
        }
        player.clear();
        if(gadget != null) gadget.getType().equip(player, plugin);
        if(particleEffect != null) particleEffect.getType().equip(player, plugin);
        if(hat != null) hat.getType().equip(player, plugin);
        if(emote != null) emote.getType().equip(player, plugin);
        for(Map.Entry<ArmorSlot, Suit> entry : suit.entrySet()) {
            entry.getValue().getType().equip(player, plugin, entry.getKey());
        }
    }
}