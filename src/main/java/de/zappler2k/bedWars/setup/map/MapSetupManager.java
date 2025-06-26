package de.zappler2k.bedWars.setup.map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import de.zappler2k.bedWars.json.init.LocationTypeAdapter;
import de.zappler2k.bedWars.objects.map.GameMap;
import de.zappler2k.bedWars.objects.map.Spawner;
import de.zappler2k.bedWars.objects.map.Team;
import de.zappler2k.bedWars.objects.map.Villager;
import de.zappler2k.bedWars.objects.map.init.SpawnerType;
import de.zappler2k.bedWars.objects.map.init.VillagerType;
import lombok.Getter;
import lombok.SneakyThrows;
import org.bukkit.Location;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.FileWriter;
import java.util.*;

@Getter
public class MapSetupManager {

    private Map<UUID, GameMap> currentSetups;
    private Map<UUID, SetupStep> currentStep;
    private Plugin plugin;

    public MapSetupManager(Plugin plugin) {
        this.currentSetups = new HashMap<>();
        this.currentStep = new HashMap<>();
        this.plugin = plugin;
    }

    public void addGameMapSetup(UUID uuid) {
        currentSetups.put(uuid, new GameMap());
        currentStep.put(uuid, getCurrentStep(getGameMapSetup(uuid)));
    }

    public boolean GameMapSetup(UUID uuid) {
        return currentSetups.containsValue(uuid);
    }

    public GameMap getGameMapSetup(UUID uuid) {
        return currentSetups.get(uuid);
    }

    public void setName(UUID uuid, String name) {
        getGameMapSetup(uuid).setName(name);
    }

    public void setMaxTeams(UUID uuid, int amount) {
        getGameMapSetup(uuid).setMaxTeams(amount);
    }

    public void setMaxPlayersPerTeam(UUID uuid, int amount) {
        getGameMapSetup(uuid).setMaxPlayersPerTeam(amount);
    }

    public void setMinPlayersPerTeam(UUID uuid, int amount) {
        getGameMapSetup(uuid).setMinPlayersPerTeam(amount);
    }

    public void setWorldName(UUID uuid, String name) {
        getGameMapSetup(uuid).setWorldName(name);
    }

    public void setSpectatorLocation(UUID uuid, Location location) {
        getGameMapSetup(uuid).setSpectatorLocation(location);
    }

    // Spawner management methods
    public void addSpawner(UUID uuid, Spawner spawner) {
        GameMap gameMap = getGameMapSetup(uuid);
        if (gameMap != null) {
            if (gameMap.getSpawners() == null) {
                gameMap.setSpawners(new ArrayList<>());
            }
            gameMap.getSpawners().add(spawner);
        }
    }

    public void removeSpawner(UUID uuid, int index) {
        GameMap gameMap = getGameMapSetup(uuid);
        if (gameMap != null && gameMap.getSpawners() != null && index >= 0 && index < gameMap.getSpawners().size()) {
            gameMap.getSpawners().remove(index);
        }
    }

    public void clearSpawners(UUID uuid) {
        GameMap gameMap = getGameMapSetup(uuid);
        if (gameMap != null && gameMap.getSpawners() != null) {
            gameMap.getSpawners().clear();
        }
    }

    public List<Spawner> getSpawners(UUID uuid) {
        GameMap gameMap = getGameMapSetup(uuid);
        return gameMap != null ? gameMap.getSpawners() : new ArrayList<>();
    }

    // Villager management methods
    public void addVillager(UUID uuid, Villager villager) {
        GameMap gameMap = getGameMapSetup(uuid);
        if (gameMap != null) {
            if (gameMap.getVillagers() == null) {
                gameMap.setVillagers(new ArrayList<>());
            }
            gameMap.getVillagers().add(villager);
        }
    }

    public void removeVillager(UUID uuid, int index) {
        GameMap gameMap = getGameMapSetup(uuid);
        if (gameMap != null && gameMap.getVillagers() != null && index >= 0 && index < gameMap.getVillagers().size()) {
            gameMap.getVillagers().remove(index);
        }
    }

    public void clearVillagers(UUID uuid) {
        GameMap gameMap = getGameMapSetup(uuid);
        if (gameMap != null && gameMap.getVillagers() != null) {
            gameMap.getVillagers().clear();
        }
    }

    public List<Villager> getVillagers(UUID uuid) {
        GameMap gameMap = getGameMapSetup(uuid);
        return gameMap != null ? gameMap.getVillagers() : new ArrayList<>();
    }

    @SneakyThrows
    public String finishMapSetup(UUID uuid) {
        GameMap gameMap = getGameMapSetup(uuid);

        // Check if gameMap exists
        if (gameMap == null) {
            return "Game map not found for the provided UUID.";
        }

        StringBuilder stringBuilder = new StringBuilder();
        boolean hasErrors = false;

        // Validate all required components
        hasErrors |= validateMapName(gameMap, stringBuilder);
        hasErrors |= validateMaxPlayersPerTeam(gameMap, stringBuilder);
        hasErrors |= validateMaxTeams(gameMap, stringBuilder);
        hasErrors |= validateMinPlayersPerTeam(gameMap, stringBuilder);
        hasErrors |= validateWorldName(gameMap, stringBuilder);
        hasErrors |= validateTeamCount(gameMap, stringBuilder);
        hasErrors |= validatePlayersPerTeamLogic(gameMap, stringBuilder);
        hasErrors |= validatePositiveValues(gameMap, stringBuilder);
        hasErrors |= validateSpawners(gameMap, stringBuilder);
        hasErrors |= validateVillagers(gameMap, stringBuilder);
        hasErrors |= validateTeams(gameMap, stringBuilder);
        hasErrors |= validateSpectatorSpawn(gameMap, stringBuilder);

        if (hasErrors) {
            return "There are some errors preventing the map setup from being completed:\n" + stringBuilder.toString();
        }

        try {
            // Create config directory if it doesn't exist
            File configDir = new File(plugin.getDataFolder() + "/configs/maps");
            if (!configDir.exists()) {
                configDir.mkdirs();
            }

            // Save the map configuration
            File file = new File(configDir, gameMap.getName() + ".json");
            Gson gson = new GsonBuilder()
                    .setPrettyPrinting()
                    .registerTypeAdapter(Location.class, new LocationTypeAdapter())
                    .create();

            try (FileWriter fileWriter = new FileWriter(file)) {
                fileWriter.write(gson.toJson(gameMap));
                fileWriter.flush();
            }

            // Clean up the setup
            currentSetups.remove(uuid);
            currentStep.remove(uuid);

            return "Map setup completed successfully! The map has been saved to: " + file.getPath();
        } catch (Exception e) {
            return "Error saving map configuration: " + e.getMessage();
        }
    }

    private boolean validateMapName(GameMap gameMap, StringBuilder stringBuilder) {
        if (gameMap.getName() == null) {
            stringBuilder.append("§cThe map name has not been set. Use §e/mapsetup setName <name> §cto set it.\n");
            return true;
        }
        return false;
    }

    private boolean validateMaxPlayersPerTeam(GameMap gameMap, StringBuilder stringBuilder) {
        if (gameMap.getMaxPlayersPerTeam() == null) {
            stringBuilder.append("§cThe maximum players per team has not been set. Use §e/mapsetup setMaxPlayersPerTeam <amount> §cto set it.\n");
            return true;
        }
        return false;
    }

    private boolean validateMaxTeams(GameMap gameMap, StringBuilder stringBuilder) {
        if (gameMap.getMaxTeams() == null) {
            stringBuilder.append("§cThe maximum number of teams has not been set. Use §e/mapsetup setMaxTeams <amount> §cto set it.\n");
            return true;
        }
        return false;
    }

    private boolean validateMinPlayersPerTeam(GameMap gameMap, StringBuilder stringBuilder) {
        if (gameMap.getMinPlayersPerTeam() == null) {
            stringBuilder.append("§cThe minimum players per team has not been set. Use §e/mapsetup setMinPlayersPerTeam <amount> §cto set it.\n");
            return true;
        }
        return false;
    }

    private boolean validateWorldName(GameMap gameMap, StringBuilder stringBuilder) {
        if (gameMap.getWorldName() == null) {
            stringBuilder.append("§cThe world name has not been set. Use §e/mapsetup setWorld <name> §cto set it.\n");
            return true;
        }
        return false;
    }

    private boolean validateTeamCount(GameMap gameMap, StringBuilder stringBuilder) {
        if (gameMap.getTeams() != null && gameMap.getMaxTeams() != null
                && gameMap.getTeams().size() < gameMap.getMaxTeams()) {
            stringBuilder.append("§cNot enough teams configured. Current teams: §e" + gameMap.getTeams().size()
                    + "§c, Required: §e" + gameMap.getMaxTeams() + "§c. Use §e/teamsetup §cto configure more teams.\n");
            return true;
        }
        return false;
    }

    private boolean validatePlayersPerTeamLogic(GameMap gameMap, StringBuilder stringBuilder) {
        if (gameMap.getMinPlayersPerTeam() != null && gameMap.getMaxPlayersPerTeam() != null
                && gameMap.getMinPlayersPerTeam() > gameMap.getMaxPlayersPerTeam()) {
            stringBuilder.append("§cThe minimum players per team (§e" + gameMap.getMinPlayersPerTeam() + "§c) cannot be greater than the maximum players per team (§e" + gameMap.getMaxPlayersPerTeam() + "§c).\n");
            return true;
        }
        return false;
    }

    private boolean validatePositiveValues(GameMap gameMap, StringBuilder stringBuilder) {
        boolean hasError = false;

        if (gameMap.getMaxTeams() != null && gameMap.getMaxTeams() <= 0) {
            stringBuilder.append("§cThe maximum number of teams must be greater than zero. Current value: §e" + gameMap.getMaxTeams() + "§c\n");
            hasError = true;
        }

        if (gameMap.getMinPlayersPerTeam() != null && gameMap.getMinPlayersPerTeam() <= 0) {
            stringBuilder.append("§cThe minimum players per team must be greater than zero. Current value: §e" + gameMap.getMinPlayersPerTeam() + "§c\n");
            hasError = true;
        }

        return hasError;
    }

    private boolean validateSpawners(GameMap gameMap, StringBuilder stringBuilder) {
        boolean hasError = false;

        if (gameMap.getSpawners() == null || gameMap.getSpawners().isEmpty()) {
            stringBuilder.append("§cAt least one spawner must be configured for the map. Use §e/mapsetup setSpawner §cto add spawners.\n");
            hasError = true;
        } else {
            // Validate each spawner
            for (int i = 0; i < gameMap.getSpawners().size(); i++) {
                Spawner spawner = gameMap.getSpawners().get(i);
                if (spawner.getSpawnerType() == null) {
                    stringBuilder.append("§cSpawner #" + (i + 1) + " has no type set. Use §e/mapsetup setSpawner §cto configure it properly.\n");
                    hasError = true;
                }
            }
        }
        return hasError;
    }

    private boolean validateVillagers(GameMap gameMap, StringBuilder stringBuilder) {
        boolean hasError = false;

        // Villagers are optional, but if they exist, they should be valid
        if (gameMap.getVillagers() != null && !gameMap.getVillagers().isEmpty()) {
            for (int i = 0; i < gameMap.getVillagers().size(); i++) {
                Villager villager = gameMap.getVillagers().get(i);
                if (villager == null) {
                    stringBuilder.append("Villager #" + (i + 1) + " is null.\n");
                    hasError = true;
                }

            }
        }

        return hasError;
    }

    private boolean validateTeams(GameMap gameMap, StringBuilder stringBuilder) {
        if (gameMap.getTeams() == null || gameMap.getTeams().isEmpty()) {
            stringBuilder.append("§cNo teams have been configured. Use §e/teamsetup §cto configure teams.\n");
            return true;
        }

        boolean hasErrors = false;
        for (int i = 0; i < gameMap.getTeams().size(); i++) {
            Team team = gameMap.getTeams().get(i);
            if (team.getName() == null || team.getName().isEmpty()) {
                stringBuilder.append("§cTeam " + (i + 1) + " is missing a name.\n");
                hasErrors = true;
            }
            if (team.getColor() == null) {
                stringBuilder.append("§cTeam " + (i + 1) + " is missing a color.\n");
                hasErrors = true;
            }
            if (team.getSpawnLoaction() == null) {
                stringBuilder.append("§cTeam " + (i + 1) + " is missing a spawn location.\n");
                hasErrors = true;
            }
            if (team.getUpperBedLocation() == null || team.getLowerBedLocation() == null) {
                stringBuilder.append("§cTeam " + (i + 1) + " is missing bed locations.\n");
                hasErrors = true;
            }
            if (team.getRegion_1() == null || team.getRegion_2() == null) {
                stringBuilder.append("§cTeam " + (i + 1) + " is missing region points.\n");
                hasErrors = true;
            }
            if (team.getSpawners() == null || team.getSpawners().isEmpty()) {
                stringBuilder.append("§cTeam " + (i + 1) + " has no spawners configured.\n");
                hasErrors = true;
            }
        }
        return hasErrors;
    }

    private boolean validateSpectatorSpawn(GameMap gameMap, StringBuilder stringBuilder) {
        if (gameMap.getSpectatorLocation() == null) {
            stringBuilder.append("§cThe spectator spawn location has not been set. Use §e/mapsetup setSpectatorSpawn §cto set it.\n");
            return true;
        }
        return false;
    }

    private boolean checkMapNameAlreadySet(GameMap gameMap, StringBuilder stringBuilder) {
        if (gameMap.getName() != null) {
            stringBuilder.append("The map name is already set and cannot be changed.\n");
            return true;
        }
        return false;
    }

    private boolean checkMaxPlayersAlreadyConfigured(GameMap gameMap, StringBuilder stringBuilder) {
        if (gameMap.getMaxPlayersPerTeam() != null) {
            stringBuilder.append("The maximum players per team is already set and cannot be changed.\n");
            return true;
        }
        return false;
    }

    private boolean checkMaxTeamsAlreadyDefined(GameMap gameMap, StringBuilder stringBuilder) {
        if (gameMap.getMaxTeams() != null) {
            stringBuilder.append("The maximum number of teams is already set and cannot be changed.\n");
            return true;
        }
        return false;
    }

    private boolean checkMinPlayersAlreadyConfigured(GameMap gameMap, StringBuilder stringBuilder) {
        if (gameMap.getMinPlayersPerTeam() != null) {
            stringBuilder.append("The minimum players per team is already set and cannot be changed.\n");
            return true;
        }
        return false;
    }

    private boolean checkWorldNameAlreadySet(GameMap gameMap, StringBuilder stringBuilder) {
        if (gameMap.getWorldName() != null) {
            stringBuilder.append("The world name is already set and cannot be changed.\n");
            return true;
        }
        return false;
    }

    private boolean checkTeamSetupCompleted(GameMap gameMap, StringBuilder stringBuilder) {
        if (gameMap.getTeams() != null && gameMap.getMaxTeams() != null
                && gameMap.getTeams().size() >= gameMap.getMaxTeams()) {
            stringBuilder.append("All required teams are already configured. Current teams: " + gameMap.getTeams().size()
                    + ", Maximum: " + gameMap.getMaxTeams() + "\n");
            return true;
        }
        return false;
    }

    private boolean checkSpawnersAlreadyConfigured(GameMap gameMap, StringBuilder stringBuilder) {
        if (gameMap.getSpawners() != null && !gameMap.getSpawners().isEmpty()) {
            stringBuilder.append("Spawners are already configured for this map.\n");
            return true;
        }
        return false;
    }

    private boolean checkVillagersAlreadyConfigured(GameMap gameMap, StringBuilder stringBuilder) {
        if (gameMap.getVillagers() != null && !gameMap.getVillagers().isEmpty()) {
            stringBuilder.append("Villagers are already configured for this map.\n");
            return true;
        }
        return false;
    }

    private boolean checkPlayerLogicAlreadyValid(GameMap gameMap, StringBuilder stringBuilder) {
        if (gameMap.getMinPlayersPerTeam() != null && gameMap.getMaxPlayersPerTeam() != null
                && gameMap.getMinPlayersPerTeam() <= gameMap.getMaxPlayersPerTeam()) {
            stringBuilder.append("Player per team settings are already correctly configured and cannot be changed.\n");
            return true;
        }
        return false;
    }

    private boolean checkValuesAlreadyValid(GameMap gameMap, StringBuilder stringBuilder) {
        boolean hasError = false;

        if (gameMap.getMaxTeams() != null && gameMap.getMaxTeams() > 0) {
            stringBuilder.append("The maximum number of teams is already set to a valid value and cannot be changed.\n");
            hasError = true;
        }

        if (gameMap.getMinPlayersPerTeam() != null && gameMap.getMinPlayersPerTeam() > 0) {
            stringBuilder.append("The minimum players per team is already set to a valid value and cannot be changed.\n");
            hasError = true;
        }

        return hasError;
    }

    public enum SetupStep {
        MAP_NAME(1, "Map Name"),
        WORLD_NAME(2, "World Name"),
        MAX_TEAMS(3, "Maximum Teams"),
        MIN_PLAYERS_PER_TEAM(4, "Minimum Players per Team"),
        MAX_PLAYERS_PER_TEAM(5, "Maximum Players per Team"),
        SPECTATOR_SPAWN_CONFIGURATION(6, "Spectator Spawn Configuration"),
        TEAM_CONFIGURATION(7, "Team Configuration"),
        SPAWNER_CONFIGURATION(8, "Spawner Configuration"),
        VILLAGER_CONFIGURATION(9, "Villager Configuration"),
        SETUP_COMPLETE(10, "Setup Complete");

        private final int order;
        private final String displayName;

        SetupStep(int order, String displayName) {
            this.order = order;
            this.displayName = displayName;
        }

        public int getOrder() {
            return order;
        }

        public String getDisplayName() {
            return displayName;
        }

        public SetupStep getNext() {
            SetupStep[] steps = values();
            return order < steps.length ? steps[order] : this;
        }
    }

    public SetupStep getCurrentStep(GameMap gameMap) {
        if (gameMap == null) {
            return SetupStep.MAP_NAME;
        }

        if (gameMap.getName() == null) {
            return SetupStep.MAP_NAME;
        }

        if (gameMap.getWorldName() == null) {
            return SetupStep.WORLD_NAME;
        }

        if (gameMap.getMaxTeams() == null) {
            return SetupStep.MAX_TEAMS;
        }

        if (gameMap.getMaxPlayersPerTeam() == null) {
            return SetupStep.MAX_PLAYERS_PER_TEAM;
        }

        if (gameMap.getMinPlayersPerTeam() == null) {
            return SetupStep.MIN_PLAYERS_PER_TEAM;
        }

        if (gameMap.getMinPlayersPerTeam() > gameMap.getMaxPlayersPerTeam()) {
            return SetupStep.MIN_PLAYERS_PER_TEAM;
        }

        if (gameMap.getSpectatorLocation() == null) {
            return SetupStep.SPECTATOR_SPAWN_CONFIGURATION;
        }

        if (gameMap.getTeams() == null || gameMap.getTeams().size() < gameMap.getMaxTeams()) {
            return SetupStep.TEAM_CONFIGURATION;
        }

        // Check if at least one spawner of each resource type is set
        boolean hasIronSpawner = false;
        boolean hasGoldSpawner = false;
        boolean hasDiamondSpawner = false;

        if (gameMap.getSpawners() != null) {
            for (Spawner spawner : gameMap.getSpawners()) {
                if (spawner.getSpawnerType() == SpawnerType.IRON) {
                    hasIronSpawner = true;
                } else if (spawner.getSpawnerType() == SpawnerType.GOLD) {
                    hasGoldSpawner = true;
                } else if (spawner.getSpawnerType() == SpawnerType.DIAMOND) {
                    hasDiamondSpawner = true;
                }
            }
        }

        // Check if at least one shop and upgrade manager is set in the map
        boolean hasShop = false;
        boolean hasUpgradeManager = false;
        if (gameMap.getVillagers() != null) {
            for (Villager villager : gameMap.getVillagers()) {
                if (villager.getVillagerType() == VillagerType.SHOP) {
                    hasShop = true;
                } else if (villager.getVillagerType() == VillagerType.UPGRADE) {
                    hasUpgradeManager = true;
                }
            }
        }

        // Return the appropriate step based on what's missing
        if (!hasIronSpawner || !hasGoldSpawner || !hasDiamondSpawner) {
            return SetupStep.SPAWNER_CONFIGURATION;
        }

        if (!hasShop || !hasUpgradeManager) {
            return SetupStep.VILLAGER_CONFIGURATION;
        }

        return SetupStep.SETUP_COMPLETE;
    }

    public String getCurrentStepInfo(UUID uuid) {
        GameMap gameMap = getGameMapSetup(uuid);
        if (gameMap == null) {
            return "§cNo active map setup found!";
        }

        SetupStep currentStep = getCurrentStep(gameMap);
        switch (currentStep) {
            case MAP_NAME:
                return "§eCurrent step: §aSet Map Name\n§eUse: §a/mapsetup setName <name>";
            case WORLD_NAME:
                return "§eCurrent step: §aSet World Name\n§eUse: §a/mapsetup setWorldName <name>";
            case MAX_TEAMS:
                return "§eCurrent step: §aSet Maximum Teams\n§eUse: §a/mapsetup setMaxTeams <number>";
            case MAX_PLAYERS_PER_TEAM:
                return "§eCurrent step: §aSet Maximum Players per Team\n§eUse: §a/mapsetup setMaxPlayersPerTeam <number>";
            case MIN_PLAYERS_PER_TEAM:
                if (gameMap.getMaxPlayersPerTeam() == null) {
                    return "§cError: You must set the maximum players per team first!";
                }
                if (gameMap.getMinPlayersPerTeam() != null && gameMap.getMinPlayersPerTeam() > gameMap.getMaxPlayersPerTeam()) {
                    return "§cError: Minimum players per team cannot be greater than maximum players per team!";
                }
                return "§eCurrent step: §aSet Minimum Players per Team\n§eUse: §a/mapsetup setMinPlayersPerTeam <number>";
            case TEAM_CONFIGURATION:
                return "§eCurrent step: §aConfigure Teams\n§eUse: §a/mapsetup team";
            case SPECTATOR_SPAWN_CONFIGURATION:
                return "§eCurrent step: §aSet Spectator Spawn Location\n§eUse: §a/mapsetup setSpectatorSpawn";
            case SPAWNER_CONFIGURATION:
                StringBuilder spawnerMessage = new StringBuilder();
                spawnerMessage.append("§eCurrent step: §aConfigure Resource Spawners\n");
                spawnerMessage.append("§cYou must set at least one spawner of each type:\n");

                // Check which spawners are missing
                boolean hasIronSpawner = false;
                boolean hasGoldSpawner = false;
                boolean hasDiamondSpawner = false;

                if (gameMap.getSpawners() != null) {
                    for (Spawner spawner : gameMap.getSpawners()) {
                        if (spawner.getSpawnerType() == SpawnerType.IRON) hasIronSpawner = true;
                        else if (spawner.getSpawnerType() == SpawnerType.GOLD) hasGoldSpawner = true;
                        else if (spawner.getSpawnerType() == SpawnerType.DIAMOND) hasDiamondSpawner = true;
                    }
                }

                if (!hasIronSpawner) spawnerMessage.append("§c- Iron Spawner\n");
                if (!hasGoldSpawner) spawnerMessage.append("§c- Gold Spawner\n");
                if (!hasDiamondSpawner) spawnerMessage.append("§c- Diamond Spawner\n");

                spawnerMessage.append("\n§eUse: §a/mapsetup setSpawner\n");
                spawnerMessage.append("§eNote: You can set both spawners and villagers at any time!");
                return spawnerMessage.toString();
            case VILLAGER_CONFIGURATION:
                StringBuilder villagerMessage = new StringBuilder();
                villagerMessage.append("§eCurrent step: §aConfigure Shops and Upgrade Managers\n");

                // Check which villagers are missing
                boolean hasShop = false;
                boolean hasUpgrade = false;
                if (gameMap.getVillagers() != null) {
                    for (Villager villager : gameMap.getVillagers()) {
                        if (villager.getVillagerType() == VillagerType.SHOP) hasShop = true;
                        if (villager.getVillagerType() == VillagerType.UPGRADE) hasUpgrade = true;
                    }
                }

                if (!hasShop && !hasUpgrade) {
                    villagerMessage.append("§cYou need to set both a Shop and an Upgrade Manager!\n");
                } else {
                    if (!hasShop) villagerMessage.append("§cYou still need to set a Shop!\n");
                    if (!hasUpgrade) villagerMessage.append("§cYou still need to set an Upgrade Manager!\n");
                }

                villagerMessage.append("\n§eUse: §a/mapsetup setVillager\n");
                villagerMessage.append("§eNote: You can set both spawners and villagers at any time!");
                return villagerMessage.toString();
            case SETUP_COMPLETE:
                StringBuilder completeMessage = new StringBuilder();
                completeMessage.append("§aMap setup is complete!\n");
                completeMessage.append("§eYou can still add more spawners and villagers if needed.\n");
                completeMessage.append("§eUse: §a/mapsetup finish §eto complete the setup.");
                return completeMessage.toString();
            default:
                return "§cUnknown setup step!";
        }
    }

    public boolean canFinishSetup(GameMap gameMap) {
        if (gameMap == null) return false;

        // Check if all required steps are completed
        if (gameMap.getName() == null || gameMap.getWorldName() == null ||
                gameMap.getMaxTeams() == null || gameMap.getMaxPlayersPerTeam() == null ||
                gameMap.getMinPlayersPerTeam() == null || gameMap.getSpectatorLocation() == null) {
            return false;
        }

        // Check if all teams are configured
        if (gameMap.getTeams() == null || gameMap.getTeams().size() < gameMap.getMaxTeams()) {
            return false;
        }

        // Check if at least one spawner of each resource type is set
        boolean hasIronSpawner = false;
        boolean hasGoldSpawner = false;
        boolean hasDiamondSpawner = false;

        if (gameMap.getSpawners() != null) {
            for (Spawner spawner : gameMap.getSpawners()) {
                if (spawner.getSpawnerType() == SpawnerType.IRON) {
                    hasIronSpawner = true;
                } else if (spawner.getSpawnerType() == SpawnerType.GOLD) {
                    hasGoldSpawner = true;
                } else if (spawner.getSpawnerType() == SpawnerType.DIAMOND) {
                    hasDiamondSpawner = true;
                }
            }
        }

        if (!hasIronSpawner || !hasGoldSpawner || !hasDiamondSpawner) {
            return false;
        }

        // Check if at least one shop and upgrade manager is set
        boolean hasShop = false;
        boolean hasUpgradeManager = false;
        if (gameMap.getVillagers() != null) {
            for (Villager villager : gameMap.getVillagers()) {
                if (villager.getVillagerType() == VillagerType.SHOP) {
                    hasShop = true;
                } else if (villager.getVillagerType() == VillagerType.UPGRADE) {
                    hasUpgradeManager = true;
                }
            }
        }

        return hasShop && hasUpgradeManager;
    }
}

