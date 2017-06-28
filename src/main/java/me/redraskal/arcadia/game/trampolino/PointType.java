package me.redraskal.arcadia.game.trampolino;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.material.MaterialData;

public enum PointType {

    TEN(new MaterialData(Material.WOOL, (byte) 14), 10, ChatColor.RED + "+10"),
    FIVE(new MaterialData(Material.WOOL, (byte) 4), 5, ChatColor.YELLOW + "+5"),
    ONE(new MaterialData(Material.WOOL, (byte) 5), 1, ChatColor.GREEN + "+1"),
    SUPER_BOOST(new MaterialData(Material.WEB), 0, ChatColor.AQUA + "Super Boost");

    private final MaterialData materialData;
    private final int points;
    private final String translation;

    private PointType(MaterialData materialData, int points, String translation) {
        this.materialData = materialData;
        this.points = points;
        this.translation = translation;
    }

    public MaterialData getMaterialData() {
        return this.materialData;
    }

    public int getPoints() {
        return this.points;
    }

    public String getTranslation() {
        return this.translation;
    }

    public static PointType fetch() {
        double random = Math.random();
        if(random < 0.5D) {
            return PointType.ONE;
        } else if(random < 0.7D) {
            return PointType.TEN;
        } else if(random < 0.93D) {
            return PointType.FIVE;
        } else {
            return PointType.SUPER_BOOST;
        }
    }
}