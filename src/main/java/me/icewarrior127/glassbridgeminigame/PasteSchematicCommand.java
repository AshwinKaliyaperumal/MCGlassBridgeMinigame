package me.icewarrior127.glassbridgeminigame;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.session.ClipboardHolder;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.math.BlockVector3;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.InputStream;
import java.io.IOException;

public class PasteSchematicCommand implements CommandExecutor {
    private final JavaPlugin plugin;

    public PasteSchematicCommand(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can use this command.");
            return true;
        }

        // Load the schematic from resources inside the JAR
        try (InputStream schemStream = getClass().getResourceAsStream("/glass_bridge.schem")) {
            if (schemStream == null) {
                sender.sendMessage("Schematic file not found in plugin resources.");
                return true;
            }

            ClipboardFormat format = ClipboardFormats.findByAlias("schem");
            ClipboardReader reader = format.getReader(schemStream);
            Clipboard clipboard = reader.read();

            World weWorld = BukkitAdapter.adapt(player.getWorld());
            BlockVector3 location = BlockVector3.at(
                    player.getLocation().getBlockX(),
                    player.getLocation().getBlockY(),
                    player.getLocation().getBlockZ()
            );

            try (EditSession editSession = WorldEdit.getInstance().newEditSession(weWorld)) {
                Operation operation = new ClipboardHolder(clipboard)
                        .createPaste(editSession)
                        .to(location)
                        .ignoreAirBlocks(false)
                        .build();
                Operations.complete(operation);
                sender.sendMessage("Glass bridge schematic pasted!");
            }

        } catch (IOException | com.sk89q.worldedit.WorldEditException e) {
            e.printStackTrace();
            sender.sendMessage("Error pasting schematic: " + e.getMessage());
        }

        return true;
    }
}


