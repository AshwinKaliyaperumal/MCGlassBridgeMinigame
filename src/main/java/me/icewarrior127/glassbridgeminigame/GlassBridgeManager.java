package me.icewarrior127.glassbridgeminigame;

import me.icewarrior127.glassbridgeminigame.SchematicRegion;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.session.ClipboardHolder;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import org.bukkit.Location;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileInputStream;
import java.util.*;

public class GlassBridgeManager {
    private final JavaPlugin plugin;
    private final Map<SchematicRegion, Boolean> regionSafetyMap = new HashMap<>(); // region -> isSafe=false
    private final List<Location[]> panePairs = new ArrayList<>();

    private static final int SCHEMATIC_SIZE = 3;
    private static final int SCHEMATIC_SPACING = 3;
    private static final int TOTAL_OFFSET = SCHEMATIC_SIZE + SCHEMATIC_SPACING;
    private static final int MANUAL_SHIFT = 3; // i think i saved the schematic weirdly so this adjusts for that


    public GlassBridgeManager(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void initializeBridge(Location startLocation, int pairs) {
        regionSafetyMap.clear();
        panePairs.clear();

        try {
            File schematicFile = new File(plugin.getDataFolder(), "glass_pane.schem");
            ClipboardFormat format = ClipboardFormats.findByFile(schematicFile);
            Clipboard clipboard = format.getReader(new FileInputStream(schematicFile)).read();

            int width = clipboard.getDimensions().getX();
            int height = clipboard.getDimensions().getY();
            int length = clipboard.getDimensions().getZ();

            Random rand = new Random();

            for (int i = 0; i < pairs; i++) {
                int zOffset = i * TOTAL_OFFSET;
                Location left = startLocation.clone().add(0, 0, zOffset);
                Location right = startLocation.clone().add(TOTAL_OFFSET, 0, zOffset);

                pasteSchematic(clipboard, left);
                pasteSchematic(clipboard, right);

                // manual shifting
                left.add(3, 0, 2);
                right.add(3, 0, 2);

                SchematicRegion leftRegion = new SchematicRegion(left, width, height, length);
                SchematicRegion rightRegion = new SchematicRegion(right, width, height, length);

                boolean leftSafe = rand.nextBoolean();

                regionSafetyMap.put(leftRegion, leftSafe);
                regionSafetyMap.put(rightRegion, !leftSafe);

                panePairs.add(new Location[]{left, right});
            }

            debugPrintRegions();

        } catch (Exception e) {
            e.printStackTrace();
            plugin.getLogger().severe("Failed to load or paste schematic");
        }
    }

    private void pasteSchematic(Clipboard clipboard, Location location) {
        try (EditSession session = WorldEdit.getInstance().newEditSession(BukkitAdapter.adapt(location.getWorld()))) {
            ClipboardHolder holder = new ClipboardHolder(clipboard);
            Operation operation = holder
                    .createPaste(BukkitAdapter.adapt(location.getWorld()))
                    .to(BlockVector3.at(location.getBlockX(), location.getBlockY(), location.getBlockZ()))
                    .ignoreAirBlocks(true)
                    .build();
            Operations.complete(operation);
        } catch (Exception e) {
            e.printStackTrace();
            plugin.getLogger().severe("Error pasting schematic at " + location);
        }
    }

    // Determine the schematic region this location is in
    public SchematicRegion getRegionAt(Location loc) {
        for (SchematicRegion region : regionSafetyMap.keySet()) {
            if (region.contains(loc)) return region;
        }
        return null;
    }

    public boolean isNonSafe(Location loc) {
        SchematicRegion region = getRegionAt(loc);
        return region != null && !regionSafetyMap.get(region);
    }

    public void breakSchematicAt(Location loc) {
        SchematicRegion region = getRegionAt(loc);
        if (region == null) return;

        for (Block block : region.getAllBlocks()) {
            block.setType(Material.AIR);
        }
    }

    // DEBUG METHODS

    public void debugPrintRegions() {
        plugin.getLogger().info("=== Region Safety Map ===");
        for (Map.Entry<SchematicRegion, Boolean> entry : regionSafetyMap.entrySet()) {
            SchematicRegion region = entry.getKey();
            boolean isSafe = entry.getValue();
            plugin.getLogger().info("Region at base: " + locToString(region.getBaseLocation()) +
                    " | Safe: " + isSafe);
        }
    }

    private String locToString(Location loc) {
        return "(" + loc.getWorld().getName() + ", " + loc.getBlockX() + ", " + loc.getBlockY() + ", " + loc.getBlockZ() + ")";
    }
}


