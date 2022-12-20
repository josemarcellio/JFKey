package com.josemarcellio.jfkey.listener;

import com.josemarcellio.jfkey.JFKey;
import org.bukkit.configuration.file.FileConfiguration;
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
    private final FileConfiguration config;

    public PlayerJoinQuitListener(JFKey plugin, HashMap<UUID, String> commandMap, FileConfiguration config) {
        this.plugin = plugin;
        this.commandMap = commandMap;
        this.config = config;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        UUID playerId = player.getUniqueId();
        String command = commandMap.get(playerId);
        if (command == null || command.isEmpty()) {
            if (config.contains(playerId.toString())) {
                String playerName = config.getString(playerId + ".name");
                command = config.getString(playerId + ".command");
                command = String.format(command, playerName);
                commandMap.put(playerId, command);
            }
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        UUID playerId = player.getUniqueId();
        String command = commandMap.get(playerId);
        if (command != null) {
            String configCommand = config.getString(playerId + ".command");
            if (!command.equals(configCommand)) {
                config.set(playerId + ".name", player.getName());
                config.set(playerId + ".command", command);
                plugin.saveConfig();
            }
        }
    }
}



