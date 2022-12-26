package com.josemarcellio.jfkey;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.josemarcellio.jfkey.api.database.DatabaseAPI;
import com.josemarcellio.jfkey.command.JFKeyCommand;
import com.josemarcellio.jfkey.database.SQLiteDatabase;
import com.josemarcellio.jfkey.database.YamlDatabase;
import com.josemarcellio.jfkey.database.MySQLDatabase;
import com.josemarcellio.jfkey.listener.PlayerJoinQuitListener;
import com.josemarcellio.jfkey.listener.PlayerSwapHandItemsListener;
import com.josemarcellio.jfkey.metrics.Metrics;
import com.josemarcellio.jfkey.softdepend.PlaceholderAPIHook;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class JFKey extends JavaPlugin {
    private final HashMap<UUID, String> commandMap;

    public String storageMethod;

    private DatabaseAPI databaseAPI;

    public JFKey() {
        this.commandMap = new HashMap<>();
    }


    @Override
    public void onEnable() {

        new Metrics(this, 17123);

        getLogger().info("JFKey by JoseMarcellio");

        storageMethod = getConfig().getString("storage-method");

        saveDefaultConfig();

        switch (storageMethod.toLowerCase()) {
            case "mysql":
                databaseAPI = new MySQLDatabase(this);
                break;
            case "sqlite":
                databaseAPI = new SQLiteDatabase(this);
                break;
            case "yaml":
            default:
                databaseAPI = new YamlDatabase(this);
                break;
        }
        databaseAPI.setup();


        if (getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            PlaceholderAPIHook placeholder = new PlaceholderAPIHook(this, commandMap);
            placeholder.register();
            getLogger().info("PlaceholderAPI found!, hook JFKey into PlaceholderAPI");
        }

        PlayerJoinQuitListener playerJoinQuitListener = new PlayerJoinQuitListener(this, commandMap);
        getServer().getPluginManager().registerEvents(playerJoinQuitListener, this);

        PlayerSwapHandItemsListener playerSwapHandItemsListener = new PlayerSwapHandItemsListener(commandMap);
        getServer().getPluginManager().registerEvents(playerSwapHandItemsListener, this);

        JFKeyCommand jfKeyCommand = new JFKeyCommand(this, commandMap);
        getCommand("jfkey").setExecutor(jfKeyCommand);

    }

    @Override
    public void onDisable() {
        for (Map.Entry<UUID, String> entry : commandMap.entrySet()) {
            UUID playerId = entry.getKey();
            String command = entry.getValue();
            Player player = getServer().getPlayer(playerId);
            if (player != null) {
                this.getDatabaseAPI().setCommand(player.getUniqueId(), player, command);
            }
        }
    }

    public DatabaseAPI getDatabaseAPI() {
        return databaseAPI;
    }
}
