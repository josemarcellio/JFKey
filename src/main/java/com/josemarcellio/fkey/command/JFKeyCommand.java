package com.josemarcellio.fkey.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class JFKeyCommand implements CommandExecutor {
    private final HashMap<UUID, String> commandMap;

    public JFKeyCommand(HashMap<UUID, String> commandMap) {
        this.commandMap = commandMap;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 1) {
            sender.sendMessage("/jfkey <command>");
            return true;
        }

        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be used by a player!");
            return true;
        }

        Player player = (Player) sender;
        UUID playerId = player.getUniqueId();
        String commandString = String.join(" ", args);
        commandMap.put(playerId, commandString);
        sender.sendMessage("Set command to " + commandString);
        return true;
    }
}