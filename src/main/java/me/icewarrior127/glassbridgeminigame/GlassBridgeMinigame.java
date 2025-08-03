package me.icewarrior127.glassbridgeminigame;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.entity.Player;

public class GlassBridgeMinigame extends JavaPlugin {

    private GlassBridgeManager bridgeManager;
    private final static int BRIDGE_SIZE = 10;

    @Override
    public void onEnable() {
        this.bridgeManager = new GlassBridgeManager(this);
        getServer().getPluginManager().registerEvents(new PlayerStepListener(bridgeManager), this);

        this.getCommand("startbridge").setExecutor((sender, command, label, args) -> {
            if (!(sender instanceof Player)) {
                sender.sendMessage("Only players can run this command!");
                return true;
            }
            Player player = (Player) sender;
            Location loc = player.getLocation();

            bridgeManager.initializeBridge(loc, BRIDGE_SIZE);  // 10 pairs, adjust if you want

            player.sendMessage("Glass bridge started at your location!");
            return true;
        });

    }

    @Override
    public void onDisable() {
        // cleanup if needed
    }
}
