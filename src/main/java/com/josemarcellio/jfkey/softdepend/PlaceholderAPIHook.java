package com.josemarcellio.jfkey.softdepend;

import com.josemarcellio.jfkey.JFKey;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;

public class PlaceholderAPIHook extends PlaceholderExpansion {

    private final JFKey plugin;

    public PlaceholderAPIHook(JFKey plugin) {
        this.plugin = plugin;
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
        String command = plugin.getCommandMap().get(player.getUniqueId());
        command = command.replace("no_command_set", "");
        if (params.equalsIgnoreCase("command")) {
            return command;
        }
        return null;
    }

}
