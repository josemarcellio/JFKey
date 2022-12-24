package com.josemarcellio.jfkey.api;

import org.bukkit.entity.Player;

import java.util.UUID;

public interface DatabaseAPI {
    void setup();
    void setCommand(UUID playerId, Player player, String command);
    String getCommand(UUID playerId);
}
