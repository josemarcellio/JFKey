package com.josemarcellio.jfkey.softdepend;

import com.josemarcellio.jfkey.JFKey;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;

import java.util.HashMap;
import java.util.UUID;

public class PlaceholderAPIHook extends PlaceholderExpansion {

    private final JFKey plugin;
    private final HashMap<UUID, String> commandMap;

    public PlaceholderAPIHook(JFKey plugin, HashMap<UUID, String> commandMap) {
        this.plugin = plugin;
        this.commandMap = commandMap;
    }

    @Override
    public boolean canRegister() {
        return true;
    }

    @Override
    public String getAuthor() {
        return "JoseMarcellio";
    }

    @Override
    public String getIdentifier() {
        return "jfkey";
    }

    @Override
    public String getVersion() {
        return plugin.getDescription().getVersion();
    }

    @Override
    public String onRequest(OfflinePlayer player, String params) {
        String command = commandMap.get(player.getUniqueId());
        command = command.replace("no_command_set", "");
        if (params.equalsIgnoreCase("command")) {
            return command;
        }
        return null;
    }

}
