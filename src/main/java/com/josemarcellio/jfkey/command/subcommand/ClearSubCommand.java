package com.josemarcellio.jfkey.command.subcommand;

import java.util.HashMap;
import java.util.UUID;

import com.josemarcellio.jfkey.JFKey;
import com.josemarcellio.jfkey.api.command.SubCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ClearSubCommand extends SubCommand {

    private final JFKey plugin;
    private final HashMap<UUID, String> commandMap;

    public ClearSubCommand(JFKey plugin, HashMap<UUID, String> commandMap) {
        this.plugin = plugin;
        this.commandMap = commandMap;
    }

    @Override
    public String getCommandName() {
        return "clear";
    }

    @Override
    public String getPermission() {
        return "jfkey.clear";
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        if (!(sender instanceof Player)) {
            sender.sendMessage("Only player can use this command!");
            return;
        }

        Player player = (Player) sender;

        if (commandMap.get(player.getUniqueId()) != null && !commandMap.get(player.getUniqueId()).equals("no_command_set")) {
            String lastCommand = commandMap.get(player.getUniqueId());
            String messagesClear = plugin.getConfig().getString("messages.clear");
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', messagesClear.replace("{command}", lastCommand)));
            commandMap.put(player.getUniqueId(), "no_command_set");
        } else {
            String messagesNoCommand = plugin.getConfig().getString("messages.no-command");
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', messagesNoCommand));
        }
    }
}