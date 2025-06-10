package de.zappler2k.bedWars.managers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import de.zappler2k.bedWars.hibernate.managers.MapEntityManager;
import de.zappler2k.bedWars.json.init.LocationTypeAdapter;
import de.zappler2k.bedWars.map.objects.GameMap;
import lombok.Getter;
import lombok.SneakyThrows;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.FileWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class MapManager {

    @Getter
    private List<GameMap> loadedMaps;
    private Plugin plugin;
    @Getter
    private MapEntityManager mapEntityManager;
    @Getter
    private YamlConfiguration config;

    public MapManager(Plugin plugin, MapEntityManager mapEntityManager, YamlConfiguration config) {
        this.loadedMaps = new ArrayList<>();
        this.plugin = plugin;
        this.mapEntityManager = mapEntityManager;
        this.config = config;
    }

    @SneakyThrows
    public void loadAllConfigs() {
        File dirFolder = new File(this.plugin.getDataFolder() + "/configs");
        if (!dirFolder.exists()) {
            dirFolder.mkdirs();
            return;
        }

        File[] files = dirFolder.listFiles();
        if (files == null) {
            return;
        }

        for (File file : files) {
            if (file.isFile() && file.getName().endsWith(".json")) {
                try {
                    String json = new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8);
                    GameMap map = new GsonBuilder()
                            .registerTypeAdapter(Location.class, new LocationTypeAdapter())
                            .create()
                            .fromJson(json, GameMap.class);
                    if (map != null) {
                        loadedMaps.add(map);
                        plugin.getLogger().log(Level.INFO, "The map " + map.getName() + " is now loaded.");
                    }
                } catch (Exception e) {
                    plugin.getLogger().warning("Failed to load map config: " + file.getName() + " - " + e.getMessage());
                }
            }
        }
    }

    public String getConfigsInSave() {
        if (loadedMaps.isEmpty()) {
            return "§cNo maps are currently loaded.";
        }

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("§7=== Loaded Maps ===\n");
        
        for (int i = 0; i < loadedMaps.size(); i++) {
            GameMap map = loadedMaps.get(i);
            stringBuilder.append("§7Map ").append(i + 1).append(":\n");
            stringBuilder.append("§7- Name: §e").append(map.getName()).append("\n");
            stringBuilder.append("§7- World: §e").append(map.getWordName()).append("\n");
            stringBuilder.append("§7- Teams: §e").append(map.getTeams() != null ? map.getTeams().size() : 0)
                    .append(" §7teams configured\n");
            stringBuilder.append("§7- Spawners: §e").append(map.getSpawners() != null ? map.getSpawners().size() : 0)
                    .append(" §7placed\n");
            stringBuilder.append("§7- Villagers: §e").append(map.getVillagers() != null ? map.getVillagers().size() : 0)
                    .append(" §7placed\n");
            
            // Add team details
            if (map.getTeams() != null && !map.getTeams().isEmpty()) {
                stringBuilder.append("§7  Teams:\n");
                for (int j = 0; j < map.getTeams().size(); j++) {
                    var team = map.getTeams().get(j);
                    stringBuilder.append("§7  - Team ").append(j + 1).append(": §e")
                            .append(team.getName())
                            .append(" §7(").append(team.getColor().toString()).append(")\n");
                }
            }
            
            // Add a separator between maps
            if (i < loadedMaps.size() - 1) {
                stringBuilder.append("\n");
            }
        }
        
        stringBuilder.append("\n§7Total Maps: §e").append(loadedMaps.size());
        return stringBuilder.toString();
    }

    public GameMap getGameMapByName(String mapName) {
        return loadedMaps.stream().filter(gameMap -> gameMap.getName().equalsIgnoreCase(mapName)).findAny().orElse(null);
    }

    public String saveConfigToSQL(String mapName) {
        GameMap gameMap = getGameMapByName(mapName);
        if (gameMap == null) {
            return "§cError: Map configuration '§e" + mapName + "§c' does not exist!";
        }

        try {
            // Validate map before saving
            if (!validateMap(gameMap)) {
                return "§cError: Map '§e" + mapName + "§c' is not valid! Please check the console for details.";
            }

            // Check if map already exists in database
            if (mapEntityManager.getGameMapSaveByName(mapName) != null) {
                return "§cError: A map with the name '§e" + mapName + "§c' already exists in the database!";
            }

            // Save the map to database
            mapEntityManager.createGameMapSave(gameMap);
            return "§aSuccessfully saved map '§e" + mapName + "§a' to the database!";
        } catch (Exception e) {
            plugin.getLogger().severe("Error saving map '" + mapName + "' to database: " + e.getMessage());
            return "§cError saving map to database: " + e.getMessage();
        }
    }

    private boolean validateMap(GameMap map) {
        if (map == null) {
            plugin.getLogger().severe("Map is null!");
            return false;
        }

        if (map.getName() == null || map.getName().isEmpty()) {
            plugin.getLogger().severe("Map name is missing!");
            return false;
        }

        if (map.getWordName() == null || map.getWordName().isEmpty()) {
            plugin.getLogger().severe("World name is missing for map '" + map.getName() + "'!");
            return false;
        }

        if (map.getMaxTeams() <= 0) {
            plugin.getLogger().severe("Invalid max teams for map '" + map.getName() + "'!");
            return false;
        }

        if (map.getMaxPlayersPerTeam() <= 0) {
            plugin.getLogger().severe("Invalid max players per team for map '" + map.getName() + "'!");
            return false;
        }

        if (map.getTeams() == null || map.getTeams().isEmpty()) {
            plugin.getLogger().severe("No teams configured for map '" + map.getName() + "'!");
            return false;
        }

        // Validate each team
        for (var team : map.getTeams()) {
            if (team.getName() == null || team.getName().isEmpty()) {
                plugin.getLogger().severe("Team name is missing in map '" + map.getName() + "'!");
                return false;
            }
            if (team.getColor() == null) {
                plugin.getLogger().severe("Team color is missing for team '" + team.getName() + "' in map '" + map.getName() + "'!");
                return false;
            }
            if (team.getSpawnLoaction() == null) {
                plugin.getLogger().severe("Spawn location is missing for team '" + team.getName() + "' in map '" + map.getName() + "'!");
                return false;
            }
            if (team.getLowerBedLocation() == null || team.getUpperBedLocation() == null) {
                plugin.getLogger().severe("Bed locations are missing for team '" + team.getName() + "' in map '" + map.getName() + "'!");
                return false;
            }
        }

        return true;
    }

    public String importAndSaveConfigFromSQL(String mapName) {
        try {
            // Check if map already exists locally
            if (getGameMapByName(mapName) != null) {
                return "§cError: A map with the name '§e" + mapName + "§c' already exists locally!";
            }

            // Check if map exists in database
            GameMap importedMap = mapEntityManager.getGameMapSaveByName(mapName);
            if (importedMap == null) {
                return "§cError: Map '§e" + mapName + "§c' does not exist in the database!";
            }

            // Validate imported map
            if (!validateMap(importedMap)) {
                return "§cError: Imported map '§e" + mapName + "§c' is not valid! Please check the console for details.";
            }

            // Save the imported map to local config
            File configDir = new File(plugin.getDataFolder() + "/configs");
            if (!configDir.exists()) {
                configDir.mkdirs();
            }

            File file = new File(configDir, mapName + ".json");
            Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(Location.class, new LocationTypeAdapter())
                .create();

            try (FileWriter fileWriter = new FileWriter(file)) {
                fileWriter.write(gson.toJson(importedMap));
                fileWriter.flush();
            }

            // Add to loaded maps
            loadedMaps.add(importedMap);
            plugin.getLogger().info("Successfully imported map '" + mapName + "' from database.");
            return "§aSuccessfully imported map '§e" + mapName + "§a' from the database!";
        } catch (Exception e) {
            plugin.getLogger().severe("Error importing map '" + mapName + "' from database: " + e.getMessage());
            return "§cError importing map from database: " + e.getMessage();
        }
    }

    public String importConfigsByVariantFromSQL() {
        try {
            // Get variant from config
            String variant = config.getString("variant");
            if (variant == null || variant.isEmpty()) {
                return "\u001B[31mError: No variant specified in config.yml! Please set 'variant' in your config.\u001B[0m";
            }

            // Get all maps with the specified variant from database
            List<GameMap> variantMaps = mapEntityManager.getGameMapSaveByVariant(variant);
            if (variantMaps.isEmpty()) {
                return "\u001B[31mNo maps found with variant '\u001B[33m" + variant + "\u001B[31m' in the database.\u001B[0m";
            }

            // Filter out maps that already exist locally and validate them
            List<GameMap> validNewMaps = new ArrayList<>();
            List<String> invalidMaps = new ArrayList<>();
            List<String> existingMaps = new ArrayList<>();

            for (GameMap map : variantMaps) {
                if (getGameMapByName(map.getName()) != null) {
                    existingMaps.add(map.getName());
                    continue;
                }

                if (!validateMap(map)) {
                    invalidMaps.add(map.getName());
                    continue;
                }

                validNewMaps.add(map);
            }

            // Build response message
            StringBuilder response = new StringBuilder();
            response.append("\u001B[36m=== Import Results for Variant '\u001B[33m").append(variant).append("\u001B[36m' ===\u001B[0m\n");

            if (!validNewMaps.isEmpty()) {
                loadedMaps.addAll(validNewMaps);
                response.append("\u001B[32m✓ Successfully imported \u001B[33m").append(validNewMaps.size()).append("\u001B[32m maps:\u001B[0m\n");
                for (GameMap map : validNewMaps) {
                    response.append("  - \u001B[33m").append(map.getName())
                           .append("\u001B[0m (\u001B[33m").append(map.getTeams().size()).append("\u001B[0m teams)\n");
                }
            }

            if (!existingMaps.isEmpty()) {
                response.append("\n\u001B[33m⚠ Skipped \u001B[31m").append(existingMaps.size())
                       .append("\u001B[33m existing maps:\u001B[0m\n");
                for (String mapName : existingMaps) {
                    response.append("  - \u001B[33m").append(mapName).append("\u001B[0m\n");
                }
            }

            if (!invalidMaps.isEmpty()) {
                response.append("\n\u001B[31m✗ Skipped \u001B[33m").append(invalidMaps.size())
                       .append("\u001B[31m invalid maps:\u001B[0m\n");
                for (String mapName : invalidMaps) {
                    response.append("  - \u001B[33m").append(mapName).append("\u001B[0m\n");
                }
            }

            if (validNewMaps.isEmpty() && existingMaps.isEmpty() && invalidMaps.isEmpty()) {
                return "\u001B[31mNo valid maps found with variant '\u001B[33m" + variant + "\u001B[31m' in the database.\u001B[0m";
            }

            response.append("\n\u001B[36m=== Summary ===\u001B[0m\n");
            response.append("Total maps found: \u001B[33m").append(variantMaps.size()).append("\u001B[0m\n");
            response.append("Successfully imported: \u001B[32m").append(validNewMaps.size()).append("\u001B[0m\n");
            response.append("Already existing: \u001B[33m").append(existingMaps.size()).append("\u001B[0m\n");
            response.append("Invalid maps: \u001B[31m").append(invalidMaps.size()).append("\u001B[0m");

            return response.toString();
        } catch (Exception e) {
            plugin.getLogger().severe("Error importing maps from database: " + e.getMessage());
            return "\u001B[31mError importing maps from database: " + e.getMessage() + "\u001B[0m";
        }
    }

    public String importAndSaveConfigsByVariantFromSQL(String variant) {
        try {
            // Get all maps with the specified variant from database
            List<GameMap> variantMaps = mapEntityManager.getGameMapSaveByVariant(variant);
            if (variantMaps.isEmpty()) {
                return "§cNo maps found with variant '§e" + variant + "§c' in the database.";
            }

            // Filter out maps that already exist locally
            List<GameMap> newMaps = variantMaps.stream()
                .filter(map -> getGameMapByName(map.getName()) == null)
                .toList();

            if (newMaps.isEmpty()) {
                return "§eAll maps with variant '§e" + variant + "§e' are already loaded locally.";
            }

            // Create config directory if it doesn't exist
            File configDir = new File(plugin.getDataFolder() + "/configs");
            if (!configDir.exists()) {
                configDir.mkdirs();
            }

            // Save each map to local config and add to loaded maps
            Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(Location.class, new LocationTypeAdapter())
                .create();

            int savedCount = 0;
            for (GameMap map : newMaps) {
                try {
                    File file = new File(configDir, map.getName() + ".json");
                    try (FileWriter fileWriter = new FileWriter(file)) {
                        fileWriter.write(gson.toJson(map));
                        fileWriter.flush();
                    }
                    loadedMaps.add(map);
                    savedCount++;
                } catch (Exception e) {
                    return "§cError saving map '§e" + map.getName() + "§c': " + e.getMessage();
                }
            }

            return "§aSuccessfully imported and saved §e" + savedCount + " §amaps with variant '§e" + variant + "§a' from the database!";
        } catch (Exception e) {
            return "§cError importing maps from database: " + e.getMessage();
        }
    }

    public String importConfigFromSQL(String mapName) {
        try {
            // Check if map already exists locally
            if (getGameMapByName(mapName) != null) {
                return "§cMap '§e" + mapName + "§c' is already loaded locally!";
            }

            // Get map from database
            GameMap map = mapEntityManager.getGameMapSaveByName(mapName);
            if (map == null) {
                return "§cMap '§e" + mapName + "§c' not found in the database!";
            }

            // Add to loaded maps
            loadedMaps.add(map);
            return "§aSuccessfully imported map '§e" + mapName + "§a' from the database!";
        } catch (Exception e) {
            return "§cError importing map from database: " + e.getMessage();
        }
    }
}
