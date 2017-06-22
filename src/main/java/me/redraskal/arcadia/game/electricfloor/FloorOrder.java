package me.redraskal.arcadia.game.electricfloor;

import org.bukkit.block.Block;
import org.bukkit.material.MaterialData;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FloorOrder {

    private final List<MaterialData> materialData;
    private Map<Block, Integer> current = new HashMap<>();

    public FloorOrder(List<MaterialData> materialData) {
        this.materialData = materialData;
    }

    public MaterialData first() {
        return materialData.get(0);
    }

    public MaterialData currentData(Block block) {
        if(!current.containsKey(block)) return this.first();
        return materialData.get(current.get(block));
    }

    public MaterialData nextData(Block block) {
        if(current.containsKey(block)) {
            if((current.get(block)+1) < materialData.size()) {
                current.put(block, (current.get(block)+1));
            }
        } else {
            current.put(block, 1);
        }
        return materialData.get(current.get(block));
    }

    public MaterialData last() {
        return materialData.get((materialData.size()-1));
    }

    public boolean match(Block block, MaterialData data) {
        return block.getType() == data.getItemType() && block.getData() == data.getData();
    }

    public boolean contains(Block block) {
        for(MaterialData data : materialData) {
            if(this.match(block, data)) return true;
        }
        return false;
    }
}