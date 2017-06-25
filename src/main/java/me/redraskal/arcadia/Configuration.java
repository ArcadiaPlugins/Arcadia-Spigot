package me.redraskal.arcadia;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.nio.file.Files;

public class Configuration {

    private final JavaPlugin plugin;
    private final File folder;
    private final String fileName;
    private final File file;

    private FileConfiguration bukkitImpl;

    /**
     * A simplistic configuration system built off of Bukkit.
     * @param folder
     * @param fileName
     * @param plugin
     */
    public Configuration(File folder, String fileName, JavaPlugin plugin) {
        this.folder = folder;
        this.fileName = fileName;
        this.file = new File(folder, fileName);
        this.plugin = plugin;

        this.reload();
    }

    /**
     * Reloads the configuration contents.
     */
    public void reload() {
        if(!file.exists()) return;
        this.bukkitImpl = YamlConfiguration.loadConfiguration(file);
        try {
            Reader reader = new InputStreamReader(new FileInputStream(file), "UTF8");
            if(reader != null) {
                YamlConfiguration temp = YamlConfiguration.loadConfiguration(reader);
                bukkitImpl.setDefaults(temp);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Fetches the configuration instance itself.
     * @return
     */
    public FileConfiguration fetch() {
        if(bukkitImpl == null) reload();
        return bukkitImpl;
    }

    /**
     * Attempts to delete the file.
     * @return
     */
    public boolean delete() {
        try {
            Files.delete(file.toPath());
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Saves the file.
     */
    public void save() {
        if(bukkitImpl == null) return;
        try {
            fetch().save(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Copies the contents of the embedded file.
     */
    public void copyDefaults() {
        if(bukkitImpl == null) reload();
        String resourcePath = fileName;
        if(!file.exists()) {
            if (resourcePath == null || resourcePath.equals("")) return;
            resourcePath = resourcePath.replace('\\', '/');
            InputStream in = plugin.getResource(resourcePath);
            if (in == null) return;
            File outFile = new File(folder, resourcePath);
            int lastIndex = resourcePath.lastIndexOf('/');
            File outDir = new File(folder, resourcePath.substring(0, lastIndex >= 0 ? lastIndex : 0));
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
}