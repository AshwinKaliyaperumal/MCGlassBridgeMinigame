package me.icewarrior127.glassbridgeminigame;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;

import java.util.ArrayList;
import java.util.List;

public class SchematicRegion {
    private final Location baseLocation;
    private final int width, height, length;

    public SchematicRegion(Location baseLocation, int width, int height, int length) {
        this.baseLocation = baseLocation;
        this.width = width;
        this.height = height;
        this.length = length;
    }

    public boolean contains(Location loc) {
        if (!loc.getWorld().equals(baseLocation.getWorld())) return false;

        int dx = loc.getBlockX() - baseLocation.getBlockX();
        int dy = loc.getBlockY() - baseLocation.getBlockY();
        int dz = loc.getBlockZ() - baseLocation.getBlockZ();

        return dx >= 0 && dx < width &&
                dy >= 0 && dy < height &&
                dz >= 0 && dz < length;
    }

    public List<Block> getAllBlocks() {
        List<Block> blocks = new ArrayList<>();
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                for (int z = 0; z < length; z++) {
                    blocks.add(baseLocation.clone().add(x, y, z).getBlock());
                }
            }
        }
        return blocks;
    }

    public Location getBaseLocation() {
        return baseLocation;
    }
}


