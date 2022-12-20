package com.josemarcellio.fkey.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;

import java.util.HashMap;
import java.util.UUID;

public class PlayerSwapHandItemsListener implements Listener {
    private final HashMap<UUID, String> commandMap;

    public PlayerSwapHandItemsListener(HashMap<UUID, String> commandMap) {
        this.commandMap = commandMap;
    }

    @EventHandler
    public void onPlayerSwapHandItems(PlayerSwapHandItemsEvent event) {
        event.setCancelled(true);

        String command = commandMap.get(event.getPlayer().getUniqueId());
        if (command != null) {
            event.getPlayer().performCommand(command);
        }
    }
}
