package me.redraskal.arcadia.api.game;

import org.bukkit.material.MaterialData;

public class VotingData {

    private final MaterialData materialData;
    private final String gameName;

    public VotingData(MaterialData materialData, String gameName) {
        this.materialData = materialData;
        this.gameName = gameName;
    }

    public MaterialData getMaterialData() {
        return this.materialData;
    }

    public String getGameName() {
        return this.gameName;
    }
}