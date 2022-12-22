package com.josemarcellio.jfkey;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.josemarcellio.jfkey.command.JFKeyCommand;
import com.josemarcellio.jfkey.listener.PlayerJoinQuitListener;
import com.josemarcellio.jfkey.listener.PlayerSwapHandItemsListener;
import com.josemarcellio.jfkey.metrics.Metrics;
import com.josemarcellio.jfkey.softdepend.PlaceholderAPIHook;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class JFKey extends JavaPlugin {
    private final HashMap<UUID, String> commandMap;
    private File configFile;
    private YamlConfiguration config;
    public HikariDataSource dataSource;
    public boolean useMySQL;

    public JFKey() {
        this.commandMap = new HashMap<>();
    }

    @Override
    public void onEnable() {

        new Metrics(this, 17123);

        getLogger().info("JFKey by JoseMarcellio");

        configFile = new File(getDataFolder(), "/data/playerdata.yml");
        config = YamlConfiguration.loadConfiguration(configFile);

        useMySQL = getConfig().getBoolean("mysql.enabled");

        saveDefaultConfig();

        useMySQL = getConfig().getBoolean("mysql.enabled");
        if (useMySQL) {
            setupMySQL();
        } else {
            loadCommandsFromConfig();
        }

        if (getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            PlaceholderAPIHook placeholder = new PlaceholderAPIHook(this, commandMap);
            placeholder.register();
            getLogger().info("PlaceholderAPI found, hooked JFKey to PlaceholderAPI!");
        }

        PlayerJoinQuitListener playerJoinQuitListener = new PlayerJoinQuitListener(this, commandMap, config);
        getServer().getPluginManager().registerEvents(playerJoinQuitListener, this);

        PlayerSwapHandItemsListener playerSwapHandItemsListener = new PlayerSwapHandItemsListener(commandMap);
        getServer().getPluginManager().registerEvents(playerSwapHandItemsListener, this);

        JFKeyCommand jfKeyCommand = new JFKeyCommand(this, commandMap);
        getCommand("jfkey").setExecutor(jfKeyCommand);

    }

    @Override
    public void onDisable() {
        if (useMySQL) {
            for (Map.Entry<UUID, String> entry : commandMap.entrySet()) {
                UUID playerId = entry.getKey();
                String command = entry.getValue();
                Player player = getServer().getPlayer(playerId);
                if (player != null) {
                    String playerName = player.getName();
                    try (Connection conn = dataSource.getConnection()) {
                        PreparedStatement stmt = conn.prepareStatement("INSERT INTO josefkey_database (player_id, player_name, command) VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE player_name = ?, command = ?");
                        stmt.setString(1, playerId.toString());
                        stmt.setString(2, playerName);
                        stmt.setString(3, command);
                        stmt.setString(4, playerName);
                        stmt.setString(5, command);
                        stmt.executeUpdate();
                    } catch (SQLException e) {
                        getLogger().severe("Error saving player command to MySQL database: " + e.getMessage());
                    }
                }
            }
        } else {
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

    private void setupMySQL() {
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl("jdbc:mysql://" + getConfig().getString("mysql.address") + ":" + getConfig().getInt("mysql.port") + "/" + getConfig().getString("mysql.database"));
        hikariConfig.setUsername(getConfig().getString("mysql.username"));
        hikariConfig.setPassword(getConfig().getString("mysql.password"));
        hikariConfig.setMinimumIdle(getConfig().getInt("mysql.pool-settings.minimum-idle"));
        hikariConfig.setMaximumPoolSize(getConfig().getInt("mysql.pool-settings.maximum-pool-size"));
        hikariConfig.setMaxLifetime(getConfig().getLong("mysql.pool-settings.maximum-lifetime"));
        hikariConfig.setConnectionTimeout(getConfig().getLong("mysql.pool-settings.connection-timeout"));
        hikariConfig.setKeepaliveTime(getConfig().getLong("mysql.pool-settings.keepalive-time"));
        dataSource = new HikariDataSource(hikariConfig);
        try (Connection conn = dataSource.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement("CREATE TABLE IF NOT EXISTS josefkey_database (player_id VARCHAR(36) NOT NULL, player_name VARCHAR(255) NOT NULL, command VARCHAR(255) NOT NULL, PRIMARY KEY (player_id))");
            stmt.executeUpdate();
        } catch (SQLException e) {
            getLogger().severe("Error creating josefkey_database table: " + e.getMessage());
        }
    }
}
