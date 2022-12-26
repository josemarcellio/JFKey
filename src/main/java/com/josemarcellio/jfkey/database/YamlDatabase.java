package com.josemarcellio.jfkey.database;

import com.josemarcellio.jfkey.JFKey;
import com.josemarcellio.jfkey.api.database.DatabaseAPI;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class YamlDatabase implements DatabaseAPI {
    private final JFKey plugin;
    private File configFile;
    private YamlConfiguration config;

    public YamlDatabase(JFKey plugin) {
        this.plugin = plugin;
    }

    @Override
    public void setup() {
        configFile = new File(plugin.getDataFolder(), "/data/playerdata.yml");
        config = YamlConfiguration.loadConfiguration(configFile);
    }

    @Override
    public String getCommand(UUID playerId) {
        String command = config.getString(playerId + ".command");
        if (command != null) {
            return command;
        } else {
            return "no_command_set";
        }
    }

    @Override
    public void setCommand(UUID playerId, Player player, String command) {
        config.set(playerId + ".name", player.getName());
        config.set(playerId + ".command", command);
        try {
            config.save(configFile);
        } catch (IOException e) {
            plugin.getLogger().severe("There is an error when trying to save playerdata: " + e.getMessage());
        }
    }
}
