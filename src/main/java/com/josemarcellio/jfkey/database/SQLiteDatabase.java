package com.josemarcellio.jfkey.database;

import com.josemarcellio.jfkey.JFKey;
import com.josemarcellio.jfkey.api.database.DatabaseAPI;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
public class SQLiteDatabase implements DatabaseAPI {
    private final JFKey plugin;
    public SQLiteDatabase(JFKey plugin) {
        this.plugin = plugin;
    }

    @Override
    public void setup() {
        try (Connection conn = getConnection()) {
            PreparedStatement stmt = conn.prepareStatement("CREATE TABLE IF NOT EXISTS josefkey_database (player_id TEXT PRIMARY KEY, player_name TEXT, command TEXT)");
            stmt.executeUpdate();
        } catch (SQLException e) {
            plugin.getLogger().severe("Error creating SQLite database: " + e.getMessage());
        }
    }

    @Override
    public void setCommand(UUID playerId, Player player, String command) {
        try (Connection conn = getConnection()) {
            PreparedStatement stmt = conn.prepareStatement("INSERT OR REPLACE INTO josefkey_database (player_id, player_name, command) VALUES (?, ?, ?)");
            stmt.setString(1, playerId.toString());
            stmt.setString(2, player.getName());
            stmt.setString(3, command);
            stmt.executeUpdate();
        } catch (SQLException e) {
            plugin.getLogger().severe("Error saving player command to SQLite: " + e.getMessage());
        }
    }

    @Override
    public String getCommand(UUID playerId) {
        try (Connection conn = getConnection()) {
            PreparedStatement stmt = conn.prepareStatement("SELECT command FROM josefkey_database WHERE player_id = ?");
            stmt.setString(1, playerId.toString());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("command");
            } else {
                return null;
            }
        } catch (SQLException e) {
            plugin.getLogger().severe("Error load player command from SQLite: " + e.getMessage());
            return null;
        }
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:sqlite:" + plugin.getDataFolder() + "/jfkey.db");
    }
}