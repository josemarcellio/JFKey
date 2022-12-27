package com.josemarcellio.jfkey.command.subcommand;

import com.josemarcellio.jfkey.JFKey;
import com.josemarcellio.jfkey.api.command.SubCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ClearSubCommand extends SubCommand {

    private final JFKey plugin;

    public ClearSubCommand(JFKey plugin) {
        this.plugin = plugin;
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

        if (plugin.getCommandMap().get(player.getUniqueId()) != null && !plugin.getCommandMap().get(player.getUniqueId()).equals("no_command_set")) {
            String lastCommand = plugin.getCommandMap().get(player.getUniqueId());
            String messagesClear = plugin.getConfig().getString("messages.clear");
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', messagesClear.replace("{command}", lastCommand)));
            plugin.getCommandMap().put(player.getUniqueId(), "no_command_set");
        } else {
            String messagesNoCommand = plugin.getConfig().getString("messages.no-command");
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', messagesNoCommand));
        }
    }
}