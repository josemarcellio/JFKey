package com.josemarcellio.jfkey.command;

import java.util.HashMap;
import java.util.UUID;

import com.josemarcellio.jfkey.api.command.SubCommand;
import com.josemarcellio.jfkey.command.subcommand.AddSubCommand;
import com.josemarcellio.jfkey.command.subcommand.ClearSubCommand;
import com.josemarcellio.jfkey.command.subcommand.ReloadSubCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;

import com.josemarcellio.jfkey.JFKey;

public class JFKeyCommand implements CommandExecutor {

    private final JFKey plugin;
    private final HashMap<UUID, String> commandMap;

    public JFKeyCommand(JFKey plugin, HashMap<UUID, String> commandMap) {
        this.plugin = plugin;
        this.commandMap = commandMap;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        FileConfiguration configuration = this.plugin.getConfig();

        String messagesHelp = configuration.getString("messages.help");

        if (args.length == 0) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', messagesHelp));
            return true;
        }

        SubCommand subCommand = getSubCommand(args[0]);
        if (subCommand == null) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', messagesHelp));
            return true;
        }

        if (!sender.hasPermission(subCommand.getPermission())) {
            String messagesNoPermission = configuration.getString("messages.no-permission");
            messagesNoPermission = messagesNoPermission.replace("{permission}", subCommand.getPermission());
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', messagesNoPermission));
            return true;
        }

        subCommand.execute(sender, args);
        return true;
    }

    private SubCommand getSubCommand(String commandName) {
        SubCommand[] subCommands = {
                new AddSubCommand(plugin, commandMap),
                new ClearSubCommand(plugin, commandMap),
                new ReloadSubCommand(plugin)
        };

        for (SubCommand subCommand : subCommands) {
            if (subCommand.getCommandName().equalsIgnoreCase(commandName)) {
                return subCommand;
            }
        }
        return null;
    }
}