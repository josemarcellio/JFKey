package com.josemarcellio.jfkey.listener;

import com.josemarcellio.jfkey.JFKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import java.util.HashMap;
import java.util.UUID;

public class PlayerJoinQuitListener implements Listener {
    private final JFKey plugin;
    private final HashMap<UUID, String> commandMap;

    public PlayerJoinQuitListener(JFKey plugin, HashMap<UUID, String> commandMap) {
        this.plugin = plugin;
        this.commandMap = commandMap;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        String command = plugin.getDatabaseAPI().getCommand(player.getUniqueId());
        commandMap.put(player.getUniqueId(), command);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        String command = commandMap.get(player.getUniqueId());
        if (command != null) {
            plugin.getDatabaseAPI().setCommand(player.getUniqueId(), player, command);
        }
    }
}



