package de.zappler2k.bedWars.managers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import de.zappler2k.bedWars.hibernate.managers.MapEntityManager;
import de.zappler2k.bedWars.json.init.LocationTypeAdapter;
import de.zappler2k.bedWars.managers.world.WorldManager;
import de.zappler2k.bedWars.map.objects.GameMap;
import de.zappler2k.bedWars.map.objects.Spawner;
import de.zappler2k.bedWars.map.objects.Villager;
import de.zappler2k.bedWars.map.objects.init.SpawnerType;
import de.zappler2k.bedWars.map.objects.init.VillagerType;
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

public class MapManager {

    @Getter
    private List<GameMap> loadedMaps;
    private Plugin plugin;
    @Getter
    private MapEntityManager mapEntityManager;
    @Getter
    private YamlConfiguration config;
    private WorldManager worldManager;

    public MapManager(Plugin plugin, MapEntityManager mapEntityManager, YamlConfiguration config, WorldManager worldManager) {
        this.loadedMaps = new ArrayList<>();
        this.plugin = plugin;
        this.mapEntityManager = mapEntityManager;
        this.config = config;
        this.worldManager = worldManager;
    }

    @SneakyThrows
    public String loadAllConfigs() {
        File dirFolder = new File(this.plugin.getDataFolder() + "/configs");
        if (!dirFolder.exists()) {
            dirFolder.mkdirs();
            return "\u001B[31mNo config directory found. Created empty directory.\u001B[0m";
        }

        File[] files = dirFolder.listFiles();
        if (files == null || files.length == 0) {
            return "\u001B[31mNo map configurations found in the configs directory.\u001B[0m";
        }

        // Filter and validate maps
        List<GameMap> validMaps = new ArrayList<>();
        List<String> invalidMaps = new ArrayList<>();
        List<String> existingMaps = new ArrayList<>();

        for (File file : files) {
            if (file.isFile() && file.getName().endsWith(".json") && !file.getName().equalsIgnoreCase("lobby.json")) {
                try {
                    String json = new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8);
                    GameMap map = new GsonBuilder()
                            .registerTypeAdapter(Location.class, new LocationTypeAdapter())
                            .create()
                            .fromJson(json, GameMap.class);

                    if (map == null) {
                        invalidMaps.add(file.getName().replace(".json", ""));
                        continue;
                    }

                    // Check if map already exists
                    if (getGameMapByName(map.getName()) != null) {
                        existingMaps.add(map.getName());
                        continue;
                    }

                    // Validate map
                    if (!validateMap(map)) {
                        invalidMaps.add(map.getName());
                        continue;
                    }

                    validMaps.add(map);
                    loadedMaps.add(map);
                } catch (Exception e) {
                    invalidMaps.add(file.getName().replace(".json", ""));
                    plugin.getLogger().warning("Failed to load map config: " + file.getName() + " - " + e.getMessage());
                }
            }
        }

        // Build response message
        StringBuilder response = new StringBuilder();
        response.append("\u001B[36m=== Local Map Loading Results ===\u001B[0m\n");

        if (!validMaps.isEmpty()) {
            response.append("\u001B[32m✓ Successfully loaded \u001B[33m").append(validMaps.size()).append("\u001B[32m maps:\u001B[0m\n");
            for (GameMap map : validMaps) {
                response.append("  - \u001B[33m").append(map.getName())
                        .append("\u001B[0m (\u001B[33m").append(map.getTeams().size()).append("\u001B[0m teams)\n");
            }
        }

        if (!existingMaps.isEmpty()) {
            response.append("\n\u001B[33m⚠ Skipped \u001B[31m").append(existingMaps.size())
                    .append("\u001B[33m already loaded maps:\u001B[0m\n");
            for (String mapName : existingMaps) {
                response.append("  - \u001B[33m").append(mapName).append("\u001B[0m\n");
            }
        }

        if (!invalidMaps.isEmpty()) {
            response.append("\n\u001B[31m✗ Failed to load \u001B[33m").append(invalidMaps.size())
                    .append("\u001B[31m maps:\u001B[0m\n");
            for (String mapName : invalidMaps) {
                response.append("  - \u001B[33m").append(mapName).append("\u001B[0m\n");
            }
        }

        if (validMaps.isEmpty() && existingMaps.isEmpty() && invalidMaps.isEmpty()) {
            return "\u001B[31mNo valid map configurations found in the configs directory.\u001B[0m";
        }

        response.append("\n\u001B[36m=== Summary ===\u001B[0m\n");
        response.append("Total files found: \u001B[33m").append(files.length).append("\u001B[0m\n");
        response.append("Successfully loaded: \u001B[32m").append(validMaps.size()).append("\u001B[0m\n");
        response.append("Already loaded: \u001B[33m").append(existingMaps.size()).append("\u001B[0m\n");
        response.append("Failed to load: \u001B[31m").append(invalidMaps.size()).append("\u001B[0m");

        return response.toString();
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
            stringBuilder.append("§7- World: §e").append(map.getWorldName()).append("\n");
            stringBuilder.append("§7- Teams: §e").append(map.getTeams() != null ? map.getTeams().size() : 0)
                    .append(" §7teams configured\n");
            stringBuilder.append("§7- Spawners: §e").append(map.getSpawners() != null ? map.getSpawners().size() : 0)
                    .append(" §7placed\n");
            stringBuilder.append("§7- Villagers: §e").append(map.getVillagers() != null ? map.getVillagers().size() : 0)
                    .append(" §7placed\n");
            stringBuilder.append("§7- Spectator Spawn: §e").append(map.getSpectatorLocation() != null ? "§aSet" : "§cNot Set").append("\n");

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
            
            // Save the world to database
            if (!worldManager.saveWorldToDatabase(gameMap.getWorldName())) {
                mapEntityManager.deleteByMapName(mapName);
                return "§cError: Failed to save world '§e" + gameMap.getWorldName() + "§c' to the database!";
            }
            
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

        boolean isValid = true;
        StringBuilder errorLog = new StringBuilder();

        // Validate basic map properties
        if (!validateBasicProperties(map, errorLog)) {
            isValid = false;
        }

        // Validate spectator spawn
        if (!validateSpectatorSpawn(map, errorLog)) {
            isValid = false;
        }

        // Validate teams
        if (!validateTeams(map, errorLog)) {
            isValid = false;
        }

        // Validate spawners
        if (!validateSpawners(map, errorLog)) {
            isValid = false;
        }

        // Validate villagers
        if (!validateVillagers(map, errorLog)) {
            isValid = false;
        }

        if (!isValid) {
            plugin.getLogger().severe("Map validation failed for '" + map.getName() + "':\n" + errorLog.toString());
        }

        return isValid;
    }

    private boolean validateBasicProperties(GameMap map, StringBuilder errorLog) {
        boolean isValid = true;

        // Validate name
        if (map.getName() == null || map.getName().isEmpty()) {
            errorLog.append("- Map name is missing!\n");
            isValid = false;
        } else if (map.getName().length() > 32) {
            errorLog.append("- Map name is too long (max 32 characters)!\n");
            isValid = false;
        }

        // Validate world name
        if (map.getWorldName() == null || map.getWorldName().isEmpty()) {
            errorLog.append("- World name is missing!\n");
            isValid = false;
        }

        // Validate team settings
        if (map.getMaxTeams() == null || map.getMaxTeams() <= 0) {
            errorLog.append("- Invalid max teams value! Must be greater than 0.\n");
            isValid = false;
        }

        if (map.getMaxPlayersPerTeam() == null || map.getMaxPlayersPerTeam() <= 0) {
            errorLog.append("- Invalid max players per team value! Must be greater than 0.\n");
            isValid = false;
        }

        if (map.getMinPlayersPerTeam() == null || map.getMinPlayersPerTeam() <= 0) {
            errorLog.append("- Invalid min players per team value! Must be greater than 0.\n");
            isValid = false;
        }

        // Validate player count logic
        if (map.getMinPlayersPerTeam() != null && map.getMaxPlayersPerTeam() != null
                && map.getMinPlayersPerTeam() > map.getMaxPlayersPerTeam()) {
            errorLog.append("- Min players per team cannot be greater than max players per team!\n");
            isValid = false;
        }

        return isValid;
    }

    private boolean validateSpectatorSpawn(GameMap map, StringBuilder errorLog) {
        if (map.getSpectatorLocation() == null) {
            errorLog.append("- Spectator spawn location is missing!\n");
            return false;
        }
        return true;
    }

    private boolean validateTeams(GameMap map, StringBuilder errorLog) {
        if (map.getTeams() == null || map.getTeams().isEmpty()) {
            errorLog.append("- No teams configured!\n");
            return false;
        }

        if (map.getTeams().size() < map.getMaxTeams()) {
            errorLog.append("- Not enough teams configured! Expected " + map.getMaxTeams() + ", got " + map.getTeams().size() + "\n");
            return false;
        }

        boolean isValid = true;
        for (int i = 0; i < map.getTeams().size(); i++) {
            var team = map.getTeams().get(i);
            StringBuilder teamErrors = new StringBuilder();

            // Validate team name
            if (team.getName() == null || team.getName().isEmpty()) {
                teamErrors.append("  - Team name is missing\n");
                isValid = false;
            }

            // Validate team color
            if (team.getColor() == null) {
                teamErrors.append("  - Team color is missing\n");
                isValid = false;
            }

            // Validate spawn location
            if (team.getSpawnLoaction() == null) {
                teamErrors.append("  - Spawn location is missing\n");
                isValid = false;
            }

            // Validate bed locations
            if (team.getLowerBedLocation() == null || team.getUpperBedLocation() == null) {
                teamErrors.append("  - Bed locations are missing\n");
                isValid = false;
            }

            // Validate region points
            if (team.getRegion_1() == null || team.getRegion_2() == null) {
                teamErrors.append("  - Region points are missing\n");
                isValid = false;
            }

            // Validate team spawners
            if (team.getSpawners() == null || team.getSpawners().isEmpty()) {
                teamErrors.append("  - No team spawners configured\n");
                isValid = false;
            }

            if (!isValid) {
                errorLog.append("Team " + (i + 1) + " (" + (team.getName() != null ? team.getName() : "unnamed") + ") has errors:\n");
                errorLog.append(teamErrors);
            }
        }

        return isValid;
    }

    private boolean validateSpawners(GameMap map, StringBuilder errorLog) {
        if (map.getSpawners() == null || map.getSpawners().isEmpty()) {
            errorLog.append("- No spawners configured!\n");
            return false;
        }

        boolean hasIronSpawner = false;
        boolean hasGoldSpawner = false;
        boolean hasDiamondSpawner = false;

        for (Spawner spawner : map.getSpawners()) {
            if (spawner.getSpawnerType() == SpawnerType.IRON) hasIronSpawner = true;
            else if (spawner.getSpawnerType() == SpawnerType.GOLD) hasGoldSpawner = true;
            else if (spawner.getSpawnerType() == SpawnerType.DIAMOND) hasDiamondSpawner = true;
        }

        boolean isValid = true;
        if (!hasIronSpawner) {
            errorLog.append("- Missing Iron spawner\n");
            isValid = false;
        }
        if (!hasGoldSpawner) {
            errorLog.append("- Missing Gold spawner\n");
            isValid = false;
        }
        if (!hasDiamondSpawner) {
            errorLog.append("- Missing Diamond spawner\n");
            isValid = false;
        }

        return isValid;
    }

    private boolean validateVillagers(GameMap map, StringBuilder errorLog) {
        if (map.getVillagers() == null || map.getVillagers().isEmpty()) {
            errorLog.append("- No villagers configured!\n");
            return false;
        }

        boolean hasShop = false;
        boolean hasUpgrade = false;

        for (Villager villager : map.getVillagers()) {
            if (villager.getVillagerType() == VillagerType.SHOP) hasShop = true;
            else if (villager.getVillagerType() == VillagerType.UPGRADE) hasUpgrade = true;
        }

        boolean isValid = true;
        if (!hasShop) {
            errorLog.append("- Missing Shop villager\n");
            isValid = false;
        }
        if (!hasUpgrade) {
            errorLog.append("- Missing Upgrade Manager villager\n");
            isValid = false;
        }

        return isValid;
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

    public String loadConfigsByVariant() {
        // Get variant from config
        String variant = config.getString("variant");
        if (variant == null || variant.isEmpty()) {
            return "\u001B[31mError: No variant specified in config.yml! Please set 'variant' in your config.\u001B[0m";
        }

        File dirFolder = new File(this.plugin.getDataFolder() + "/configs");
        if (!dirFolder.exists()) {
            dirFolder.mkdirs();
            return "\u001B[31mNo config directory found. Created empty directory.\u001B[0m";
        }

        File[] files = dirFolder.listFiles();
        if (files == null || files.length == 0) {
            return "\u001B[31mNo map configurations found in the configs directory.\u001B[0m";
        }

        // Filter and validate maps
        List<GameMap> validMaps = new ArrayList<>();
        List<String> invalidMaps = new ArrayList<>();
        List<String> existingMaps = new ArrayList<>();
        List<String> wrongVariantMaps = new ArrayList<>();

        for (File file : files) {
            if (file.isFile() && file.getName().endsWith(".json")) {
                try {
                    String json = new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8);
                    GameMap map = new GsonBuilder()
                            .registerTypeAdapter(Location.class, new LocationTypeAdapter())
                            .create()
                            .fromJson(json, GameMap.class);

                    if (map == null) {
                        invalidMaps.add(file.getName().replace(".json", ""));
                        continue;
                    }

                    // Check if map already exists
                    if (getGameMapByName(map.getName()) != null) {
                        existingMaps.add(map.getName());
                        continue;
                    }

                    // Check if map variant matches config variant
                    String mapVariant = map.getMaxTeams() + "x" + (map.getMaxPlayersPerTeam() / map.getMaxTeams());
                    if (!mapVariant.equals(variant)) {
                        wrongVariantMaps.add(map.getName());
                        continue;
                    }

                    // Validate map
                    if (!validateMap(map)) {
                        invalidMaps.add(map.getName());
                        continue;
                    }

                    validMaps.add(map);
                    loadedMaps.add(map);
                } catch (Exception e) {
                    invalidMaps.add(file.getName().replace(".json", ""));
                    plugin.getLogger().warning("Failed to load map config: " + file.getName() + " - " + e.getMessage());
                }
            }
        }

        // Build response message
        StringBuilder response = new StringBuilder();
        response.append("\u001B[36m=== Local Map Loading Results for Variant '\u001B[33m").append(variant).append("\u001B[36m' ===\u001B[0m\n");

        if (!validMaps.isEmpty()) {
            response.append("\u001B[32m✓ Successfully loaded \u001B[33m").append(validMaps.size()).append("\u001B[32m maps:\u001B[0m\n");
            for (GameMap map : validMaps) {
                response.append("  - \u001B[33m").append(map.getName())
                        .append("\u001B[0m (\u001B[33m").append(map.getTeams().size()).append("\u001B[0m teams)\n");
            }
        }

        if (!existingMaps.isEmpty()) {
            response.append("\n\u001B[33m⚠ Skipped \u001B[31m").append(existingMaps.size())
                    .append("\u001B[33m already loaded maps:\u001B[0m\n");
            for (String mapName : existingMaps) {
                response.append("  - \u001B[33m").append(mapName).append("\u001B[0m\n");
            }
        }

        if (!wrongVariantMaps.isEmpty()) {
            response.append("\n\u001B[33m⚠ Skipped \u001B[31m").append(wrongVariantMaps.size())
                    .append("\u001B[33m maps with different variant:\u001B[0m\n");
            for (String mapName : wrongVariantMaps) {
                response.append("  - \u001B[33m").append(mapName).append("\u001B[0m\n");
            }
        }

        if (!invalidMaps.isEmpty()) {
            response.append("\n\u001B[31m✗ Failed to load \u001B[33m").append(invalidMaps.size())
                    .append("\u001B[31m maps:\u001B[0m\n");
            for (String mapName : invalidMaps) {
                response.append("  - \u001B[33m").append(mapName).append("\u001B[0m\n");
            }
        }

        if (validMaps.isEmpty() && existingMaps.isEmpty() && invalidMaps.isEmpty() && wrongVariantMaps.isEmpty()) {
            return "\u001B[31mNo valid map configurations found in the configs directory.\u001B[0m";
        }

        response.append("\n\u001B[36m=== Summary ===\u001B[0m\n");
        response.append("Total files found: \u001B[33m").append(files.length).append("\u001B[0m\n");
        response.append("Successfully loaded: \u001B[32m").append(validMaps.size()).append("\u001B[0m\n");
        response.append("Already loaded: \u001B[33m").append(existingMaps.size()).append("\u001B[0m\n");
        response.append("Wrong variant: \u001B[33m").append(wrongVariantMaps.size()).append("\u001B[0m\n");
        response.append("Failed to load: \u001B[31m").append(invalidMaps.size()).append("\u001B[0m");

        return response.toString();
    }
}
