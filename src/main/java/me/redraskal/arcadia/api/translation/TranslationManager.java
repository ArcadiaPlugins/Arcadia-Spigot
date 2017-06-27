package me.redraskal.arcadia.api.translation;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.util.*;

public class TranslationManager {

    private final JavaPlugin plugin;
    private File dataFolder;
    private Map<Locale, List<Translation>> cache = new HashMap<>();
    private Locale defaultLocale;
    public boolean autoDetectLanguage = false;

    public TranslationManager(JavaPlugin javaPlugin) {
        this.plugin = javaPlugin;
    }

    /**
     * Returns a translation in the requested locale.
     * @param name
     * @param translationLocale
     * @return
     */
    public Translation fetchTranslation(String name,
                                        Locale translationLocale) {
        for(Map.Entry<Locale, List<Translation>> entry : cache.entrySet()) {
            if(entry.getKey() == translationLocale) {
                for(Translation translation : entry.getValue()) {
                    if(translation.getName().equalsIgnoreCase(name)) {
                        return translation;
                    }
                }
            }
        }
        return this.fetchTranslation(name);
    }

    /**
     * Returns a translation in the requested locale.
     * @param name
     * @return
     */
    public Translation fetchTranslation(String name) {
        if(defaultLocale == null) return null;
        for(Map.Entry<Locale, List<Translation>> entry : cache.entrySet()) {
            if(entry.getKey() == defaultLocale) {
                for(Translation translation : entry.getValue()) {
                    if(translation.getName().equalsIgnoreCase(name)) {
                        return translation;
                    }
                }
            }
        }
        return null;
    }

    /**
     * Returns a translation in the player's locale.
     * @param name
     * @param player
     * @return
     */
    public Translation fetchTranslation(String name, Player player) {
        if(player.getLocale() == null || player.getLocale().isEmpty()) {
            return this.fetchTranslation(name);
        } else {
            return this.fetchTranslation(name,
                new Locale(player.getLocale().split("_")[0], player.getLocale().split("_")[1]));
        }
    }

    /**
     * Sends a translation to specific player(s).
     * @param name
     * @param viewers
     */
    public void sendTranslation(String name, Player[] viewers, Object... args) {
        if(this.autoDetectLanguage) {
            for(Player player : viewers) {
                final Translation translation = this.fetchTranslation(name, player);
                if(translation != null) {
                    final String message = translation.build(args);
                    if(message.isEmpty()) return;
                    player.sendMessage(message);
                }
            }
        } else {
            final Translation translation = this.fetchTranslation(name);
            if(translation != null) {
                final String message = translation.build(args);
                if(message.isEmpty()) return;
                for(Player player : viewers) {
                    player.sendMessage(message);
                }
            }
        }
    }

    /**
     * Sends a translation to a player.
     * @param name
     */
    public void sendTranslation(String name, Player player, Object... args) {
        if(this.autoDetectLanguage) {
            final Translation translation = this.fetchTranslation(name, player);
            if(translation != null) {
                final String message = translation.build(args);
                if(message.isEmpty()) return;
                player.sendMessage(message);
            }
        } else {
            final Translation translation = this.fetchTranslation(name);
            if(translation != null) {
                final String message = translation.build(args);
                if(message.isEmpty()) return;
                player.sendMessage(message);
            }
        }
    }

    /**
     * Sends a translation to every player.
     * @param name
     */
    public void sendTranslation(String name, Object... args) {
        this.sendTranslation(name,
                Bukkit.getOnlinePlayers().toArray(new Player[Bukkit.getOnlinePlayers().size()]), args);
    }

    /**
     * Kicks the specified player(s) with a translation.
     * @param name
     * @param viewers
     */
    public void kickPlayer(String name, Player[] viewers, Object... args) {
        if(this.autoDetectLanguage) {
            for(Player player : viewers) {
                final Translation translation = this.fetchTranslation(name, player);
                if(translation != null) {
                    String message = translation.build(args);
                    if(message.isEmpty()) message = "Unknown kick message.";
                    player.kickPlayer(message);
                }
            }
        } else {
            final Translation translation = this.fetchTranslation(name);
            if(translation != null) {
                String message = translation.build(args);
                if(message.isEmpty()) message = "Unknown kick message.";
                for(Player player : viewers) {
                    player.kickPlayer(message);
                }
            }
        }
    }

    /**
     * Kicks every player with the specified translation.
     * @param name
     */
    public void kickPlayers(String name, Object... args) {
        this.kickPlayer(name,
                Bukkit.getOnlinePlayers().toArray(new Player[Bukkit.getOnlinePlayers().size()]), args);
    }

    /**
     * Sets the default translation locale.
     * @param locale
     */
    public void setDefaultLocale(Locale locale) {
        if(defaultLocale == locale) return;
        plugin.getLogger().info("[Utils] The default locale has been set to "
            + locale.getLanguage() + "_" + locale.getCountry() + ".");
        this.defaultLocale = locale;
    }

    /**
     * Sets the default translation locale.
     * @param locale
     */
    public void setDefaultLocale(String locale) {
        for(Locale key : cache.keySet()) {
            if(locale.equalsIgnoreCase(key.getLanguage() + "_" + key.getCountry())) setDefaultLocale(key);
        }
    }

    public void saveDefaultLocale(String fileName) {
        String resourcePath = fileName;
        if(dataFolder == null) {
            this.dataFolder = new File(plugin.getDataFolder().getPath()
                    + "/translations/");
        }
        if(!new File(dataFolder, fileName).exists()) {
            if (resourcePath == null || resourcePath.equals("")) return;
            resourcePath = resourcePath.replace('\\', '/');
            InputStream in = plugin.getResource(resourcePath);
            if (in == null) return;
            File outFile = new File(dataFolder, resourcePath);
            int lastIndex = resourcePath.lastIndexOf('/');
            File outDir = new File(dataFolder, resourcePath.substring(0, lastIndex >= 0 ? lastIndex : 0));
            if (!outDir.exists()) outDir.mkdirs();
            try {
                if (!outFile.exists()) {
                    OutputStream out = new FileOutputStream(outFile);
                    byte[] buf = new byte[1024];
                    int len;
                    while ((len = in.read(buf)) > 0) {
                        out.write(buf, 0, len);
                    }
                    out.close();
                    in.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void refreshCache() {
        plugin.getLogger().info("[Locale] Refreshing translation cache...");
        cache.clear();
        this.dataFolder = new File(plugin.getDataFolder().getPath()
                + "/translations/");
        dataFolder.mkdirs();
        for(File translationFile : dataFolder.listFiles()) {
            if(!translationFile.isDirectory()) {
                if(translationFile.getName().endsWith(".properties")) {
                    String fileName = translationFile.getName().substring(0,
                            translationFile.getName().lastIndexOf("."));
                    Locale locale = new Locale(fileName.split("_")[0], fileName.split("_")[1]);
                    List<Translation> translations = new ArrayList<>();
                    Properties properties = new Properties();
                    try {
                        FileInputStream inputStream = new FileInputStream(translationFile);
                        properties.load(new InputStreamReader(inputStream, "UTF-8"));
                        inputStream.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    properties.entrySet().forEach(entry -> {
                        translations.add(new Translation((String) entry.getKey(),
                                (String) entry.getValue(),
                                locale, this));
                    });
                    plugin.getLogger().info("[Locale] Adding " + locale.getLanguage() + "_" + locale.getCountry()
                            + " to the translation cache with " + translations.size() + " translation(s).");
                    cache.put(locale, translations);
                    if(defaultLocale == null) setDefaultLocale(locale);
                }
            }
        }
    }
}