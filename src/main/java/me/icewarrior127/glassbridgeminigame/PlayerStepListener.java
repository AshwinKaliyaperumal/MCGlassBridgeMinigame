package me.icewarrior127.glassbridgeminigame;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;

public class PlayerStepListener implements Listener {

    private final GlassBridgeManager bridgeManager;

    public PlayerStepListener(GlassBridgeManager manager) {
        this.bridgeManager = manager;
    }

    @EventHandler
    public void onPlayerStep(PlayerMoveEvent event) {

        Player player = event.getPlayer();
        Location to = event.getTo();
        if (to == null) return;

        Location below = to.clone().subtract(0, 1, 0);
        Material blockType = below.getBlock().getType();

        if (below.getBlock().getType() == Material.RED_STAINED_GLASS && bridgeManager.isNonSafe(below)) {
            bridgeManager.breakSchematicAt(below);
            player.sendMessage("You stepped on a non-safe pane! It broke!");
            Bukkit.getLogger().info(player.getName() + " stepped on a non-safe pane!");
        }
    }

}

