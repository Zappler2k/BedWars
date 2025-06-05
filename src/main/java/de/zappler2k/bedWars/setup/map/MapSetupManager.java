package de.zappler2k.bedWars.setup.map;

import com.google.gson.Gson;
import de.zappler2k.bedWars.map.objects.GameMap;
import de.zappler2k.bedWars.map.objects.Spawner;
import de.zappler2k.bedWars.map.objects.Villager;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
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
        getGameMapSetup(uuid).setWordName(name);
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

        if (hasErrors) {
            return "There are some errors preventing the map setup from being completed:\n" + stringBuilder.toString();
        } else {
            File file = new File(plugin.getDataFolder() + "/configs/" + gameMap.getName() + ".json");
            if(!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write(new Gson().toJson(getGameMapSetup(uuid)));
            fileWriter.flush();
            fileWriter.close();
            file.createNewFile();
            return "Map setup completed successfully! \n" +
                    "The map can also uploaded to the database!";
        }
    }

    private boolean validateMapName(GameMap gameMap, StringBuilder stringBuilder) {
        if (gameMap.getName() == null) {
            stringBuilder.append("The name for the map was not set.\n");
            return true;
        }
        return false;
    }

    private boolean validateMaxPlayersPerTeam(GameMap gameMap, StringBuilder stringBuilder) {
        if (gameMap.getMaxPlayersPerTeam() == null) {
            stringBuilder.append("The maximum players per team was not set.\n");
            return true;
        }
        return false;
    }

    private boolean validateMaxTeams(GameMap gameMap, StringBuilder stringBuilder) {
        if (gameMap.getMaxTeams() == null) {
            stringBuilder.append("The maximum number of teams was not set.\n");
            return true;
        }
        return false;
    }

    private boolean validateMinPlayersPerTeam(GameMap gameMap, StringBuilder stringBuilder) {
        if (gameMap.getMinPlayersPerTeam() == null) {
            stringBuilder.append("The minimum players per team was not set.\n");
            return true;
        }
        return false;
    }

    private boolean validateWorldName(GameMap gameMap, StringBuilder stringBuilder) {
        if (gameMap.getWordName() == null) {
            stringBuilder.append("The world name for the map was not set.\n");
            return true;
        }
        return false;
    }

    private boolean validateTeamCount(GameMap gameMap, StringBuilder stringBuilder) {
        if (gameMap.getTeams() != null && gameMap.getMaxTeams() != null
                && gameMap.getTeams().size() < gameMap.getMaxTeams()) {
            stringBuilder.append("Not enough teams configured. Current teams: " + gameMap.getTeams().size()
                    + ", Required: " + gameMap.getMaxTeams() + "\n");
            return true;
        }
        return false;
    }

    private boolean validatePlayersPerTeamLogic(GameMap gameMap, StringBuilder stringBuilder) {
        if (gameMap.getMinPlayersPerTeam() != null && gameMap.getMaxPlayersPerTeam() != null
                && gameMap.getMinPlayersPerTeam() > gameMap.getMaxPlayersPerTeam()) {
            stringBuilder.append("The minimum players per team cannot be greater than the maximum players per team.\n");
            return true;
        }
        return false;
    }

    private boolean validatePositiveValues(GameMap gameMap, StringBuilder stringBuilder) {
        boolean hasError = false;

        if (gameMap.getMaxTeams() != null && gameMap.getMaxTeams() <= 0) {
            stringBuilder.append("The maximum number of teams must be greater than zero.\n");
            hasError = true;
        }

        if (gameMap.getMinPlayersPerTeam() != null && gameMap.getMinPlayersPerTeam() <= 0) {
            stringBuilder.append("The minimum players per team must be greater than zero.\n");
            hasError = true;
        }

        return hasError;
    }

    private boolean validateSpawners(GameMap gameMap, StringBuilder stringBuilder) {
        boolean hasError = false;

        if (gameMap.getSpawners() == null || gameMap.getSpawners().isEmpty()) {
            stringBuilder.append("At least one spawner must be configured for the map.\n");
            hasError = true;
        } else {
            // Validate each spawner
            for (int i = 0; i < gameMap.getSpawners().size(); i++) {
                Spawner spawner = gameMap.getSpawners().get(i);
                if (spawner.getSpawnerType() == null) {
                    stringBuilder.append("Spawner #" + (i + 1) + " has no spawner type set.\n");
                    hasError = true;
                }
                if (spawner.getLocation() == null) {
                    stringBuilder.append("Spawner #" + (i + 1) + " has no location set.\n");
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
        if (gameMap.getWordName() != null) {
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
        TEAM_CONFIGURATION(6, "Team Configuration"),
        SPAWNER_CONFIGURATION(7, "Spawner Configuration"),
        VILLAGER_CONFIGURATION(8, "Villager Configuration"),
        SETUP_COMPLETE(9, "Setup Complete");

        private final int order;
        private final String displayName;

        SetupStep(int order, String displayName) {
            this.order = order;
            this.displayName = displayName;
        }

        public int getOrder() { return order; }
        public String getDisplayName() { return displayName; }

        public SetupStep getNext() {
            SetupStep[] steps = values();
            return order < steps.length ? steps[order] : this;
        }
    }

    public SetupStep getCurrentStep(GameMap gameMap) {
        if (gameMap == null) {
            throw new IllegalArgumentException("GameMap darf nicht null sein");
        }

        // Prüfung in der Reihenfolge der Setup-Schritte
        if (isNullOrEmpty(gameMap.getName())) {
            return SetupStep.MAP_NAME;
        }

        if (isNullOrEmpty(gameMap.getWordName())) {
            return SetupStep.WORLD_NAME;
        }

        if (gameMap.getMaxTeams() == null || gameMap.getMaxTeams() <= 0) {
            return SetupStep.MAX_TEAMS;
        }

        if (gameMap.getMinPlayersPerTeam() == null || gameMap.getMinPlayersPerTeam() <= 0) {
            return SetupStep.MIN_PLAYERS_PER_TEAM;
        }

        if (gameMap.getMaxPlayersPerTeam() == null || gameMap.getMaxPlayersPerTeam() <= 0) {
            return SetupStep.MAX_PLAYERS_PER_TEAM;
        }

        if (gameMap.getMinPlayersPerTeam() > gameMap.getMaxPlayersPerTeam()) {
            return SetupStep.MIN_PLAYERS_PER_TEAM;
        }

        if (!isTeamConfigurationComplete(gameMap)) {
            return SetupStep.TEAM_CONFIGURATION;
        }

        if (!isSpawnerConfigurationComplete(gameMap)) {
            return SetupStep.SPAWNER_CONFIGURATION;
        }

         if (!isVillagerConfigurationComplete(gameMap)) {
             return SetupStep.VILLAGER_CONFIGURATION;
         }

        return SetupStep.SETUP_COMPLETE;
    }

    public String getCurrentStepInfo(GameMap gameMap) {
        if (gameMap == null) {
            return "Error: GameMap is null";
        }

        SetupStep currentStep = getCurrentStep(gameMap);
        return getStepDescription(currentStep, gameMap);
    }

    public String getCurrentStepInfo(UUID uuid) {
        GameMap gameMap = getGameMapSetup(uuid);
        return getCurrentStepInfo(gameMap);
    }

    private String getStepDescription(SetupStep step, GameMap gameMap) {
        switch (step) {
            case MAP_NAME:
                return "Current Step: " + step.getDisplayName() + "\n" +
                        "Action Required: Set a name for your map.";

            case WORLD_NAME:
                return "Current Step: " + step.getDisplayName() + "\n" +
                        "Action Required: Set the world name for your map.";

            case MAX_TEAMS:
                return "Current Step: " + step.getDisplayName() + "\n" +
                        "Action Required: Set the maximum number of teams.";

            case MIN_PLAYERS_PER_TEAM:
                return "Current Step: " + step.getDisplayName() + "\n" +
                        "Action Required: Set the minimum players per team.";

            case MAX_PLAYERS_PER_TEAM:
                return "Current Step: " + step.getDisplayName() + "\n" +
                        "Action Required: Set the maximum players per team.";

            case TEAM_CONFIGURATION:
                int currentTeams = gameMap.getTeams() != null ? gameMap.getTeams().size() : 0;
                int requiredTeams = gameMap.getMaxTeams() != null ? gameMap.getMaxTeams() : 0;
                return "Current Step: " + step.getDisplayName() + "\n" +
                        "Action Required: Configure teams for your map.\n" +
                        "Current Teams: " + currentTeams + "/" + requiredTeams + "\n" +
                        "You need to add " + (requiredTeams - currentTeams) + " more team(s).";

            case SPAWNER_CONFIGURATION:
                int currentSpawners = gameMap.getSpawners() != null ? gameMap.getSpawners().size() : 0;
                return "Current Step: " + step.getDisplayName() + "\n" +
                        "Action Required: Configure spawners for your map.\n" +
                        "Current Spawners: " + currentSpawners + "\n" +
                        "You need at least 1 spawner with valid type and location.";

            case VILLAGER_CONFIGURATION:
                int currentVillagers = gameMap.getVillagers() != null ? gameMap.getVillagers().size() : 0;
                return "Current Step: " + step.getDisplayName() + "\n" +
                        "Action Required: Configure villagers for your map.\n" +
                        "Current Villagers: " + currentVillagers + "\n" +
                        "Add villagers as needed for your map.";

            case SETUP_COMPLETE:
                return "Current Step: " + step.getDisplayName() + "\n" +
                        "Action Required: Your map setup is complete! /mapsetup finish.";

            default:
                return "Current Step: Unknown\n" +
                        "Action Required: Please check your map configuration.";
        }
    }

    private boolean isTeamConfigurationComplete(GameMap gameMap) {
        return gameMap.getTeams() != null &&
                !gameMap.getTeams().isEmpty() &&
                gameMap.getTeams().size() >= gameMap.getMaxTeams();
    }

    private boolean isSpawnerConfigurationComplete(GameMap gameMap) {
        return gameMap.getSpawners() != null &&
                !gameMap.getSpawners().isEmpty() &&
                gameMap.getSpawners().stream().allMatch(spawner ->
                        spawner.getSpawnerType() != null && spawner.getLocation() != null);
    }

    private boolean isVillagerConfigurationComplete(GameMap gameMap) {
        // Since villagers are optional, this method can be used if you want to make them mandatory
        return gameMap.getVillagers() != null && !gameMap.getVillagers().isEmpty();
    }

    private boolean isNullOrEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }
}
