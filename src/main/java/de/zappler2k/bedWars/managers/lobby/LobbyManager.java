package de.zappler2k.bedWars.managers.lobby;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import de.zappler2k.bedWars.game.configs.LobbyConfig;
import de.zappler2k.bedWars.json.init.LocationTypeAdapter;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Level;

@Getter
public class LobbyManager {

    private final Plugin plugin;
    private LobbyConfig lobbyConfig;
    private final File configFile;
    private final Gson gson;

    public LobbyManager(Plugin plugin) {
        this.plugin = plugin;
        this.configFile = new File(plugin.getDataFolder(), "configs/lobby.json");
        this.gson = new GsonBuilder().setPrettyPrinting().create();
        createConfigDirectory();
    }

    private void createConfigDirectory() {
        if (!configFile.getParentFile().exists()) {
            configFile.getParentFile().mkdirs();
        }
    }

    public String getErrorByLoadingLobbyConfig() {
        return "\u001B[36m╔════════════════════════════════════════════════════════════╗\u001B[0m\n" +
               "\u001B[36m║\u001B[0m \u001B[31mError: No lobby config found in the configs folder.\u001B[0m \u001B[36m║\u001B[0m\n" +
               "\u001B[36m╚════════════════════════════════════════════════════════════╝\u001B[0m";
    }

    public String loadConfig() {
        try {
            if (!configFile.exists()) {
                return getErrorByLoadingLobbyConfig();
            }

            try (FileReader reader = new FileReader(configFile)) {
                lobbyConfig = new GsonBuilder().registerTypeAdapter(Location.class, new LocationTypeAdapter()).create().fromJson(reader, LobbyConfig.class);
                return "\u001B[36m╔════════════════════════════════════════════════════════════╗\u001B[0m\n" +
                       "\u001B[36m║\u001B[0m \u001B[32mSuccessfully loaded lobby configuration\u001B[0m \u001B[36m║\u001B[0m\n" +
                       "\u001B[36m╚════════════════════════════════════════════════════════════╝\u001B[0m";
            }
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to load lobby config", e);
            return "\u001B[36m╔════════════════════════════════════════════════════════════╗\u001B[0m\n" +
                   "\u001B[36m║\u001B[0m \u001B[31mError loading lobby config: " + e.getMessage() + "\u001B[0m \u001B[36m║\u001B[0m\n" +
                   "\u001B[36m╚════════════════════════════════════════════════════════════╝\u001B[0m";
        }
    }
}
