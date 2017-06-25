package me.redraskal.arcadia.api.translation;

import org.bukkit.ChatColor;

import java.util.Locale;

public class Translation {

    private final String name;
    private final String translatedContent;
    private final Locale translationLocale;
    private final TranslationManager translationManager;

    public Translation(String name, String translatedContent,
                       Locale translationLocale, TranslationManager translationManager) {
        this.name = name;
        this.translatedContent = translatedContent;
        this.translationLocale = translationLocale;
        this.translationManager = translationManager;
    }

    public String getName() {
        return this.name;
    }

    public String getTranslatedContent() {
        return this.translatedContent;
    }

    public Locale getTranslationLocale() {
        return this.translationLocale;
    }

    public String build(Object... args) {
        String temp = String.format(this.translatedContent, args);
        return ChatColor.translateAlternateColorCodes('&', temp);
    }
}