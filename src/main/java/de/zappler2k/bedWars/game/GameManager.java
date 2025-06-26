package de.zappler2k.bedWars.game;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import de.zappler2k.bedWars.BedWars;
import de.zappler2k.bedWars.game.configs.LobbyConfig;
import de.zappler2k.bedWars.json.init.LocationTypeAdapter;
import de.zappler2k.bedWars.managers.lobby.LobbyManager;
import de.zappler2k.bedWars.managers.shop.ShopManager;
import de.zappler2k.bedWars.normal.shop.DefaultShop;
import de.zappler2k.bedWars.objects.shop.Shop;
import lombok.SneakyThrows;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.nio.file.Files;
import java.util.logging.Level;

public class GameManager {

    private GameState currentGameState;
    private Plugin plugin;
    private LobbyConfig lobbyConfig;
    private Shop currentShop;
    private ShopManager shopManager;

    @SneakyThrows
    public GameManager(Plugin plugin, YamlConfiguration yamlConfigurationConfig) {
        this.plugin = plugin;
        LobbyManager lobbyManager = new LobbyManager(plugin);
        this.shopManager = new ShopManager(plugin);
        
        // Get the current shop name from config.yml
        String currentShopName = yamlConfigurationConfig.getString("currentShop");
        if (currentShopName == null || currentShopName.isEmpty()) {
            currentShopName = "default";
            yamlConfigurationConfig.set("currentShop", currentShopName);
            // Save the config to persist the default value
            yamlConfigurationConfig.save(new File(plugin.getDataFolder(), "config.yml"));
        }
        
        // Load the shop configuration
        File shopFile = new File(plugin.getDataFolder(), "configs/shops/" + currentShopName + ".yml");
        if (!shopFile.exists() || shopManager.loadShop(shopFile) == null) {
            // Create default shop if it doesn't exist
            DefaultShop defaultShop = new DefaultShop();
            defaultShop.setShopName(currentShopName);
            shopManager.saveShop(defaultShop);
            this.currentShop = shopManager.getShop(currentShopName);
            plugin.getLogger().log(Level.INFO, "\u001B[36m╔════════════════════════════════════════════════════════════╗\u001B[0m\n" +
                    "\u001B[36m║\u001B[0m \u001B[33mCreated and loaded default shop: \u001B[36m" + currentShopName + "\u001B[0m \u001B[36m║\u001B[0m\n" +
                    "\u001B[36m╚════════════════════════════════════════════════════════════╝\u001B[0m");
        } else {
            this.currentShop = shopManager.getShop(currentShopName);
            plugin.getLogger().log(Level.INFO, "\u001B[36m╔════════════════════════════════════════════════════════════╗\u001B[0m\n" +
                    "\u001B[36m║\u001B[0m \u001B[32mSuccessfully loaded shop: \u001B[36m" + currentShopName + "\u001B[0m \u001B[36m║\u001B[0m\n" +
                    "\u001B[36m╚════════════════════════════════════════════════════════════╝\u001B[0m");
        }
        
        plugin.getLogger().log(Level.INFO, lobbyManager.loadConfig());
        lobbyConfig = lobbyManager.getLobbyConfig();
    }
}
