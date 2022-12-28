package com.josemarcellio.jfkey.command.subcommand;

import com.josemarcellio.jfkey.JFKey;
import com.josemarcellio.jfkey.api.command.SubCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class ReloadSubCommand extends SubCommand {

    private final JFKey plugin;

    public ReloadSubCommand(JFKey plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getCommandName() {
        return "reload";
    }

    @Override
    public String getPermission() {
        return "jfkey.admin";
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        plugin.reloadConfig();
        String messagesReload = plugin.getConfig()
                .getString("messages.reload");

        sender.sendMessage(
                ChatColor.translateAlternateColorCodes('&',
                        messagesReload));
    }
}