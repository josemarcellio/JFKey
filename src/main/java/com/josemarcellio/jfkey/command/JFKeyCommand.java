package com.josemarcellio.jfkey.command;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import com.josemarcellio.jfkey.JFKey;

public class JFKeyCommand implements CommandExecutor {
    private final HashMap<UUID, String> commandMap;
    private final JFKey plugin;

    public JFKeyCommand(HashMap<UUID, String> commandMap, JFKey plugin) {
        this.commandMap = commandMap;
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player)) {
            sender.sendMessage("Only player can use this command!");
            return true;
        }

        Player player = (Player) sender;

        FileConfiguration configuration = this.plugin.getConfig();

        String messagesHelp = configuration.getString("messages.help");

        if (args.length == 0) {
            player.sendMessage(setColor(messagesHelp));
            return true;
        }

        switch (args[0]) {
            case "add":
                if (args.length < 2) {
                    String messagesInvalid = configuration.getString("messages.invalid");
                    player.sendMessage(setColor(messagesInvalid));
                    return true;
                }
                StringBuilder commandToAddBuilder = new StringBuilder(args[1]);
                for (int i = 2; i < args.length; i++) {
                    commandToAddBuilder.append(" ").append(args[i]);
                }
                String commandToAdd = commandToAddBuilder.toString();
                commandMap.put(player.getUniqueId(), commandToAdd);
                String messagesAdded = configuration.getString("messages.added");
                player.sendMessage(setColor(messagesAdded.replace("{command}", commandToAdd)));
                break;
            case "clear":
                if (commandMap.get(player.getUniqueId()) != null && !commandMap.get(player.getUniqueId()).equals("no_command_set")) {
                    String lastCommand = commandMap.get(player.getUniqueId());
                    String messagesClear = configuration.getString("messages.clear");
                    player.sendMessage(setColor(messagesClear.replace("{command}", lastCommand)));
                    commandMap.put(player.getUniqueId(), "no_command_set");
                } else {
                    String messagesNoCommand = configuration.getString("messages.no-command");
                    player.sendMessage(setColor(messagesNoCommand));
                }
                break;
            case "reload":
                if (!player.hasPermission("jfkey.admin")) {
                    String messagesNoPermission = configuration.getString("messages.no-permission");
                    player.sendMessage(setColor(messagesNoPermission));
                    return true;
                }
                plugin.reloadConfig();
                String messagesReload = configuration.getString("messages.reload");
                player.sendMessage(setColor(messagesReload));
                break;
            default:
                player.sendMessage(setColor(messagesHelp));
                break;
        }

        return true;
    }

    private String setColor(String string) {
        return ChatColor.translateAlternateColorCodes('&', string);
    }
}
