package com.josemarcellio.jfkey.listener;

import com.josemarcellio.jfkey.JFKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
public class PlayerJoinQuitListener
        implements Listener {
    private final JFKey
            plugin;

    public PlayerJoinQuitListener(
            JFKey plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        String command = plugin.getDatabaseAPI()
                .getCommand(player.getUniqueId());
        if (command == null) {
            plugin.getCommandMap().put(
                    player.getUniqueId(), "no_command_set");
        } else {
            plugin.getCommandMap().put(
                    player.getUniqueId(), command);
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        String command = plugin.getCommandMap()
                .get(player.getUniqueId());

        if (command != null) {
            plugin.getDatabaseAPI().setCommand(
                    player.getUniqueId(), player, command);
        }
    }
}



