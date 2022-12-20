package com.josemarcellio.fkey;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.josemarcellio.fkey.command.JFKeyCommand;
import com.josemarcellio.fkey.listener.PlayerJoinQuitListener;
import com.josemarcellio.fkey.listener.PlayerSwapHandItemsListener;
import com.josemarcellio.fkey.metrics.Metrics;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class JFKey extends JavaPlugin {
    private final HashMap<UUID, String> commandMap;
    private File configFile;
    private YamlConfiguration config;

    public JFKey() {
        this.commandMap = new HashMap<>();
    }

    @Override
    public void onEnable() {

        new Metrics(this, 17123);

        getLogger().info("JFKey by JoseMarcellio");

        configFile = new File(getDataFolder(), "/data/playerdata.yml");
        config = YamlConfiguration.loadConfiguration(configFile);

        loadCommandsFromConfig();

        PlayerJoinQuitListener playerJoinQuitListener = new PlayerJoinQuitListener(this, commandMap, config);
        getServer().getPluginManager().registerEvents(playerJoinQuitListener, this);

        PlayerSwapHandItemsListener playerSwapHandItemsListener = new PlayerSwapHandItemsListener(commandMap);
        getServer().getPluginManager().registerEvents(playerSwapHandItemsListener, this);

        JFKeyCommand jfKeyCommand = new JFKeyCommand(commandMap);
        getCommand("jfkey").setExecutor(jfKeyCommand);

    }

    @Override
    public void onDisable() {

        getLogger().info("JFKey by JoseMarcellio");

        for (Map.Entry<UUID, String> entry : commandMap.entrySet()) {
            UUID playerId = entry.getKey();
            String command = entry.getValue();
            config.set(playerId + ".command", command);
            Player player = getServer().getPlayer(playerId);
            if (player != null) {
                config.set(playerId + ".name", player.getName());
            }
        }
        saveConfig();
    }

    private void loadCommandsFromConfig() {
        for (String key : config.getKeys(false)) {
            UUID playerId = UUID.fromString(key);
            String command = config.getString(key + ".command");
            commandMap.putIfAbsent(playerId, command);
        }
    }

    public void saveConfig() {
        try {
            config.save(configFile);
        } catch (IOException e) {
            getLogger().severe("There is an error when trying to save the config: " + e.getMessage());
        }
    }
}
