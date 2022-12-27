package com.josemarcellio.jfkey.command;

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

    public JFKeyCommand(JFKey plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        FileConfiguration configuration = plugin.getConfig();

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
                new AddSubCommand(plugin),
                new ClearSubCommand(plugin),
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