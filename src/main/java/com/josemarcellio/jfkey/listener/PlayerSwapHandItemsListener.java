package com.josemarcellio.jfkey.listener;

import com.josemarcellio.jfkey.JFKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;

public class PlayerSwapHandItemsListener
        implements Listener {

    private final JFKey
            plugin;

    public PlayerSwapHandItemsListener(
            JFKey plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerSwapHandItems(PlayerSwapHandItemsEvent event) {
        String command = plugin.getCommandMap()
                .get(event.getPlayer().getUniqueId());

        if (command != null && !command.equals("no_command_set")) {
            event.setCancelled(true);
            event.getPlayer().performCommand(command);
        }
    }
}
