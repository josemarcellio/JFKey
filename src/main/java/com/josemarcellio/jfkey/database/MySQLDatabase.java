package com.josemarcellio.jfkey.database;

import com.josemarcellio.jfkey.JFKey;
import com.josemarcellio.jfkey.api.database.DatabaseAPI;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class MySQLDatabase
        implements DatabaseAPI {

    private final JFKey plugin;
    private HikariDataSource dataSource;

    public MySQLDatabase(
            JFKey plugin) {
        this.plugin = plugin;
    }

    @Override
    public void setup() {
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl("jdbc:mysql://" + plugin.getConfig()
                .getString("mysql.address") + ":"
                + plugin.getConfig().getInt("mysql.port")
                + "/" + plugin.getConfig().getString(
                        "mysql.database"));
        hikariConfig.setUsername(
                plugin.getConfig().getString(
                        "mysql.username"));
        hikariConfig.setPassword(
                plugin.getConfig().getString(
                        "mysql.password"));
        hikariConfig.setMinimumIdle(
                plugin.getConfig().getInt(
                        "mysql.pool-settings.minimum-idle"));
        hikariConfig.setMaximumPoolSize(
                plugin.getConfig().getInt(
                        "mysql.pool-settings.maximum-pool-size"));
        hikariConfig.setMaxLifetime(
                plugin.getConfig().getLong(
                        "mysql.pool-settings.maximum-lifetime"));
        hikariConfig.setConnectionTimeout(
                plugin.getConfig().getLong(
                        "mysql.pool-settings.connection-timeout"));
        hikariConfig.setKeepaliveTime(
                plugin.getConfig().getLong(
                        "mysql.pool-settings.keepalive-time"));
        dataSource = new HikariDataSource(hikariConfig);
        try (Connection conn = dataSource.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(
                    "CREATE TABLE IF NOT EXISTS josefkey_database " +
                            "(player_id VARCHAR(36) NOT NULL, player_name " +
                            "VARCHAR(255) NOT NULL, command VARCHAR(255) NOT " +
                            "NULL, PRIMARY KEY (player_id))");
            stmt.executeUpdate();
        } catch (SQLException e) {
            plugin.getLogger().severe(
                    "Error creating josefkey_database table: " + e.getMessage());
        }
    }

    @Override
    public void setCommand(
            UUID playerId, Player player, String command) {

        try (Connection conn = dataSource.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(
                    "INSERT INTO josefkey_database (player_id, player_name, " +
                            "command) VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE " +
                            "player_name = ?, command = ?");
            stmt.setString(1, playerId.toString());
            stmt.setString(2, player.getName());
            stmt.setString(3, command);
            stmt.setString(4, player.getName());
            stmt.setString(5, command);
            stmt.executeUpdate();
        } catch (SQLException e) {
            plugin.getLogger().severe(
                    "Error saving player command to MySQL: "
                            + e.getMessage());
        }
    }

    @Override
    public String getCommand(UUID playerId) {
        try (Connection conn = dataSource.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(
                    "SELECT command FROM josefkey_database " +
                            "WHERE player_id = ?");
            stmt.setString(1, playerId.toString());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("command");
            } else {
                return "no_command_set";
            }
        } catch (SQLException e) {
            plugin.getLogger().severe(
                    "Error load player command from MySQL: "
                            + e.getMessage());
            return "no_command_set";
        }
    }
}