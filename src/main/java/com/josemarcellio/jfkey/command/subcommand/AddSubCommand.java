package com.josemarcellio.jfkey.command.subcommand;

import com.josemarcellio.jfkey.JFKey;
import com.josemarcellio.jfkey.api.command.SubCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class AddSubCommand extends SubCommand {

    private final JFKey plugin;
    private final HashMap<UUID, String> commandMap;

    public AddSubCommand(JFKey plugin, HashMap<UUID, String> commandMap) {
        this.plugin = plugin;
        this.commandMap = commandMap;
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
        commandMap.put(player.getUniqueId(), commandToAdd);
        String messagesAdded = plugin.getConfig().getString("messages.added");
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', messagesAdded.replace("{command}", commandToAdd)));
    }
}