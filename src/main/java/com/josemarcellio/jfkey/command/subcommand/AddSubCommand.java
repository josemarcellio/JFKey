package com.josemarcellio.jfkey.command.subcommand;

import com.josemarcellio.jfkey.JFKey;
import com.josemarcellio.jfkey.api.command.SubCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AddSubCommand extends SubCommand {

    private final JFKey plugin;

    public AddSubCommand(JFKey plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getCommandName() {
        return "add";
    }

    @Override
    public String getPermission() {
        return "jfkey.add";
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        if (!(sender instanceof Player)) {
            sender.sendMessage("Only player can use this command!");
            return;
        }

        Player player = (Player) sender;

        if (args.length < 2) {
            String messagesInvalid = plugin.getConfig().getString("messages.invalid");
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', messagesInvalid));
            return;
        }
        StringBuilder commandToAddBuilder = new StringBuilder(args[1]);
        for (int i = 2; i < args.length; i++) {
            commandToAddBuilder.append(" ").append(args[i]);
        }
        String commandToAdd = commandToAddBuilder.toString();
        plugin.getCommandMap().put(player.getUniqueId(), commandToAdd);
        String messagesAdded = plugin.getConfig().getString("messages.added");
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', messagesAdded.replace("{command}", commandToAdd)));
    }
}