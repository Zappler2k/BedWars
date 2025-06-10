package de.zappler2k.bedWars.commands.lobby.init;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import de.zappler2k.bedWars.BedWars;
import de.zappler2k.bedWars.command.SubCommand;
import de.zappler2k.bedWars.game.configs.LobbyConfig;
import de.zappler2k.bedWars.json.init.LocationTypeAdapter;
import de.zappler2k.bedWars.setup.map.LobbySetupManager;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.FileWriter;
import java.util.List;
import java.util.UUID;

public class LobbyFinishSubCommand extends SubCommand {

    private final BedWars plugin;
    private final LobbySetupManager lobbySetupManager;

    public LobbyFinishSubCommand(BedWars plugin, LobbySetupManager lobbySetupManager) {
        super("bedwars.admin.setup.lobby.finish");
        this.plugin = plugin;
        this.lobbySetupManager = lobbySetupManager;
    }

    @Override
    public boolean executeSubCommand(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cThis command can only be executed by players!");
            return true;
        }

        UUID uuid = player.getUniqueId();
        if (!lobbySetupManager.isSetupComplete(uuid)) {
            player.sendMessage("§cNot all lobby locations have been set!");
            return true;
        }

        try {
            // Create configs directory if it doesn't exist
            File configsDir = new File(plugin.getDataFolder(), "configs");
            if (!configsDir.exists()) {
                configsDir.mkdirs();
            }

            // Create lobby config file
            File lobbyFile = new File(configsDir, "lobby.json");
            if (lobbyFile.exists()) {
                lobbyFile.delete();
            }

            // Get all locations
            Location lobbyLocation = lobbySetupManager.getLobbyLocation(uuid);
            Location lifetimeStatsLocation = lobbySetupManager.getLifetimeStatsBoardLocation(uuid);
            Location limitedStatsLocation = lobbySetupManager.getLimitedStatsBoardLocation(uuid);

            // Create lobby config object
            LobbyConfig lobbyConfig = new LobbyConfig(
                    lobbyLocation,
                    lifetimeStatsLocation,
                    limitedStatsLocation
            );

            // Save to file
            Gson gson = new GsonBuilder()
                    .setPrettyPrinting()
                    .registerTypeAdapter(Location.class, new LocationTypeAdapter())
                    .create();

            try (FileWriter writer = new FileWriter(lobbyFile)) {
                gson.toJson(lobbyConfig, writer);
            }

            player.sendMessage("§aLobby configuration has been saved successfully!");
            return true;
        } catch (Exception e) {
            player.sendMessage("§cError saving lobby configuration: " + e.getMessage());
            plugin.getLogger().severe("Error saving lobby configuration: " + e.getMessage());
            return true;
        }
    }

    @Override
    public List<String> getSubCommandTabComplete(CommandSender sender, String[] args) {
        return List.of();
    }

    @Override
    public String getPermission() {
        return permission;
    }
} 