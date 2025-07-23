package me.icewarrior127.glassbridgeminigame;

import org.bukkit.plugin.java.JavaPlugin;

public class GlassBridgeMinigame extends JavaPlugin {

    @Override
    public void onEnable() {
        // Register your command executor for "pasteglassbridge"
        this.getCommand("pasteglassbridge").setExecutor(new PasteSchematicCommand(this));

        getLogger().info("GlassBridgeMinigame enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("GlassBridgeMinigame disabled!");
    }
}
