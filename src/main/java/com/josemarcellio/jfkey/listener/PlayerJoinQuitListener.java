package com.josemarcellio.jfkey.listener;

import com.josemarcellio.jfkey.JFKey;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.UUID;

public class PlayerJoinQuitListener implements Listener {
    private final JFKey plugin;
    private final HashMap<UUID, String> commandMap;
    private final YamlConfiguration config;

    public PlayerJoinQuitListener(JFKey plugin, HashMap<UUID, String> commandMap, YamlConfiguration config) {
        this.plugin = plugin;
        this.commandMap = commandMap;
        this.config = config;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (plugin.useMySQL) {
            try (Connection conn = plugin.dataSource.getConnection()) {
                PreparedStatement stmt = conn.prepareStatement("SELECT command FROM josefkey_database WHERE player_id = ?");
                stmt.setString(1, player.getUniqueId().toString());
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    String command = rs.getString("command");
                    commandMap.put(player.getUniqueId(), command);
                } else {
                    commandMap.put(player.getUniqueId(), "no_command_set");
                }
            } catch (SQLException e) {
                plugin.getLogger().severe("Error loading player command from MySQL database: " + e.getMessage());
            }
        } else {
            String command = config.getString(player.getUniqueId() + ".command");
            if (command != null) {
                commandMap.put(player.getUniqueId(), command);
            } else {
                commandMap.put(player.getUniqueId(), "no_command_set");
            }
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        String command = commandMap.get(player.getUniqueId());
        if (command != null) {
            if (plugin.useMySQL) {
                try (Connection conn = plugin.dataSource.getConnection()) {
                    PreparedStatement stmt = conn.prepareStatement("INSERT INTO josefkey_database (player_id, player_name, command) VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE player_name = ?, command = ?");
                    stmt.setString(1, player.getUniqueId().toString());
                    stmt.setString(2, player.getName());
                    stmt.setString(3, command);
                    stmt.setString(4, player.getName());
                    stmt.setString(5, command);
                    stmt.executeUpdate();
                } catch (SQLException e) {
                    plugin.getLogger().severe("Error saving player command to MySQL database: " + e.getMessage());
                }
            } else {
                config.set(player.getUniqueId() + ".name", player.getName());
                config.set(player.getUniqueId() + ".command", command);
                plugin.saveConfig();
            }
        }
    }
}



