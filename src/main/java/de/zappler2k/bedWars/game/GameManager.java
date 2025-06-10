package de.zappler2k.bedWars.game;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import de.zappler2k.bedWars.game.configs.LobbyConfig;
import de.zappler2k.bedWars.json.init.LocationTypeAdapter;
import lombok.SneakyThrows;
import org.bukkit.Location;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.nio.file.Files;
import java.util.logging.Level;

public class GameManager {

    private GameState currentGameState;
    private Plugin plugin;
    private LobbyConfig lobbyConfig;

    @SneakyThrows
    public GameManager(Plugin plugin) {
        this.plugin = plugin;
        lobbyConfig = new GsonBuilder().registerTypeAdapter(Location.class, new LocationTypeAdapter()).create().fromJson(Files.readAllBytes(new File(this.plugin.getDataFolder() + "/configs/lobby.json").toPath()).toString(), LobbyConfig.class);
        if(lobbyConfig == null) {
            currentGameState = GameState.ERROR;
            this.plugin.getLogger().log(Level.INFO, "There is a error in the lobby configuration. You need to configure the LobbyConfig.");
            return;
        }
    }
}
