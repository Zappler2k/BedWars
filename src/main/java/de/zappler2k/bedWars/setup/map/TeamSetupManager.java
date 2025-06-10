package de.zappler2k.bedWars.setup.map;

import de.zappler2k.bedWars.map.objects.GameMap;
import de.zappler2k.bedWars.map.objects.Spawner;
import de.zappler2k.bedWars.map.objects.Team;
import de.zappler2k.bedWars.map.objects.init.SpawnerType;
import lombok.Getter;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.plugin.Plugin;

import java.util.*;
import java.util.stream.Collectors;

public class TeamSetupManager {

    @Getter
    private Map<UUID, Team> currentTeamSetups;
    @Getter
    private Map<UUID, TeamSetupStep> currentStep;
    @Getter
    private MapSetupManager mapSetupManager;
    private Plugin plugin;

    public TeamSetupManager(Plugin plugin, MapSetupManager mapSetupManager) {
        this.currentTeamSetups = new HashMap<>();
        this.currentStep = new HashMap<>();
        this.plugin = plugin;
        this.mapSetupManager = mapSetupManager;
    }


    // Check if user has an active map setup
    private boolean hasActiveMapSetup(UUID uuid) {
        return getCurrentMap(uuid) != null;
    }

    // Get the current map being set up
    private GameMap getCurrentMap(UUID uuid) {
        return mapSetupManager.getGameMapSetup(uuid);
    }

    public String startTeamSetup(UUID uuid) {
        if (!hasActiveMapSetup(uuid)) {
            return "§cError: You must start a map setup first using §e/mapsetup§c!";
        }

        GameMap currentMap = getCurrentMap(uuid);
        if (currentMap.getMaxTeams() == null) {
            return "§cError: Please set the maximum number of teams using §e/mapsetup setMaxTeams <amount> §cfirst!";
        }

        if (currentMap.getTeams() != null && currentMap.getTeams().size() >= currentMap.getMaxTeams()) {
            return "§cError: All teams for this map are already configured! (" +
                    "§e" + currentMap.getTeams().size() + "§c/§e" + currentMap.getMaxTeams() + "§c)";
        }

        currentTeamSetups.put(uuid, new Team());
        currentStep.put(uuid, TeamSetupStep.TEAM_NAME);

        int currentTeams = currentMap.getTeams() != null ? currentMap.getTeams().size() : 0;
        return "§aTeam setup started! Configuring team §e" + (currentTeams + 1) + "§a/§e" + currentMap.getMaxTeams() +
                "\n§eNext step: Set a name for this team using §a/teamsetup setName <name>§e.";
    }

    public boolean hasActiveTeamSetup(UUID uuid) {
        return currentTeamSetups.containsKey(uuid);
    }

    public Team getCurrentTeamSetup(UUID uuid) {
        return currentTeamSetups.get(uuid);
    }

    public String setTeamName(UUID uuid, String name) {
        if (name == null || name.trim().isEmpty()) {
            return "§cError: The team name cannot be empty! Use §e/teamsetup setName <name>§c.";
        }

        // Check if name is already taken
        GameMap currentMap = getCurrentMap(uuid);
        if (currentMap != null && currentMap.getTeams() != null) {
            for (Team existingTeam : currentMap.getTeams()) {
                if (existingTeam.getName() != null && existingTeam.getName().equalsIgnoreCase(name)) {
                    return "§cError: A team with the name '§e" + name + "§c' already exists! Please choose a different name.";
                }
            }
        }

        Team team = getCurrentTeamSetup(uuid);
        if (team != null) {
            // Check if the name is already taken by the current team
            if (team.getName() != null && team.getName().equalsIgnoreCase(name)) {
                return "§cError: This team already has the name '§e" + name + "§c'!";
            }
            team.setName(name);
            return "§aTeam name set to: §e" + name;
        }
        return "§cError: No active team setup found! Start one using §e/teamsetup start§c.";
    }

    public String setTeamColor(UUID uuid, Color color) {
        if (color == null) {
            return "§cError: Invalid color! Use §e/teamsetup setColor <color>§c.";
        }

        // Check if color is already taken
        GameMap currentMap = getCurrentMap(uuid);
        if (currentMap != null && currentMap.getTeams() != null) {
            for (Team existingTeam : currentMap.getTeams()) {
                if (existingTeam.getColor() != null && existingTeam.getColor().equals(color)) {
                    return "§cError: A team with this color already exists! Please choose a different color.";
                }
            }
        }

        Team team = getCurrentTeamSetup(uuid);
        if (team != null) {
            team.setColor(color);
            return "§aTeam color set successfully!";
        }
        return "§cError: No active team setup found! Start one using §e/teamsetup start§c.";
    }

    public void setSpawnLocation(UUID uuid, Location location) {
        Team team = getCurrentTeamSetup(uuid);
        if (team != null) {
            team.setSpawnLoaction(location);
        }
    }

    public void setUpperBedLocation(UUID uuid, Location location) {
        Team team = getCurrentTeamSetup(uuid);
        if (team != null) {
            team.setUpperBedLocation(location);
        }
    }

    public void setLowerBedLocation(UUID uuid, Location location) {
        Team team = getCurrentTeamSetup(uuid);
        if (team != null) {
            team.setLowerBedLocation(location);
        }
    }

    public void setRegion1(UUID uuid, Location location) {
        Team team = getCurrentTeamSetup(uuid);
        if (team != null) {
            team.setRegion_1(location);
        }
    }

    public void setRegion2(UUID uuid, Location location) {
        Team team = getCurrentTeamSetup(uuid);
        if (team != null) {
            team.setRegion_2(location);
        }
    }

    // Team spawner methods
    public void addTeamSpawner(UUID uuid, Spawner spawner) {
        Team team = getCurrentTeamSetup(uuid);
        if (team != null) {
            if (team.getSpawners() == null) {
                team.setSpawners(new ArrayList<>());
            }
            team.getSpawners().add(spawner);
        }
    }

    public void removeTeamSpawner(UUID uuid, int index) {
        Team team = getCurrentTeamSetup(uuid);
        if (team != null && team.getSpawners() != null &&
                index >= 0 && index < team.getSpawners().size()) {
            team.getSpawners().remove(index);
        }
    }

    public void clearTeamSpawners(UUID uuid) {
        Team team = getCurrentTeamSetup(uuid);
        if (team != null && team.getSpawners() != null) {
            team.getSpawners().clear();
        }
    }

    public List<Spawner> getTeamSpawners(UUID uuid) {
        Team team = getCurrentTeamSetup(uuid);
        return team != null && team.getSpawners() != null ? team.getSpawners() : new ArrayList<>();
    }

    private boolean validateTeamSetup(Team team, StringBuilder stringBuilder) {
        boolean hasErrors = false;

        hasErrors |= validateTeamName(team, stringBuilder);
        hasErrors |= validateTeamColor(team, stringBuilder);
        hasErrors |= validateSpawnLocation(team, stringBuilder);
        hasErrors |= validateBedLocations(team, stringBuilder);
        hasErrors |= validateRegionLocations(team, stringBuilder);
        hasErrors |= validateTeamSpawners(team, stringBuilder);

        if (hasErrors) {
            stringBuilder.insert(0, "§cThere are some errors preventing the team setup from being completed:\n");
        }

        return !hasErrors;
    }

    public String finishTeamSetup(UUID uuid) {
        Team team = getCurrentTeamSetup(uuid);
        if (team == null) {
            return "§cError: No active team setup found!";
        }

        GameMap gameMap = getCurrentMap(uuid);
        if (gameMap == null) {
            return "§cError: No active map setup found!";
        }

        StringBuilder stringBuilder = new StringBuilder();
        if (!validateTeamSetup(team, stringBuilder)) {
            return stringBuilder.toString();
        }

        // Check for duplicate team names and colors
        if (gameMap.getTeams() != null) {
            for (Team existingTeam : gameMap.getTeams()) {
                if (existingTeam.getName() != null && existingTeam.getName().equalsIgnoreCase(team.getName())) {
                    return "§cError: A team with the name '§e" + team.getName() + "§c' already exists! Please choose a different name.";
                }
                if (existingTeam.getColor() != null && existingTeam.getColor().equals(team.getColor())) {
                    return "§cError: A team with this color already exists! Please choose a different color.";
                }
            }
        }

        // Add team to map
        if (gameMap.getTeams() == null) {
            gameMap.setTeams(new ArrayList<>());
        }
        gameMap.getTeams().add(team);

        // Clean up the setup
        currentTeamSetups.remove(uuid);
        currentStep.remove(uuid);

        int currentTeams = gameMap.getTeams().size();
        int maxTeams = gameMap.getMaxTeams();

        StringBuilder result = new StringBuilder();
        result.append("§aTeam '§e").append(team.getName()).append("§a' has been successfully configured!\n");
        result.append("§7Team Information:\n");
        result.append("§7- Name: §e").append(team.getName()).append("\n");
        result.append("§7- Color: §e").append(team.getColor().toString()).append("\n");
        result.append("§7- Spawners: §e").append(team.getSpawners() != null ? team.getSpawners().size() : 0).append(" §7placed\n");
        result.append("\n§7Progress: §e").append(currentTeams).append("§7/§e").append(maxTeams).append(" §7teams configured");

        if (currentTeams < maxTeams) {
            result.append("\n§7You can configure the next team by starting a new team setup.");
        } else {
            result.append("\n§aAll teams for this map are now configured! You can proceed with the map setup.");
        }

        return result.toString();
    }

    private boolean validateTeamName(Team team, StringBuilder stringBuilder) {
        if (team.getName() == null || team.getName().trim().isEmpty()) {
            stringBuilder.append("§cThe team name has not been set. Use §e/teamsetup setName <name> §cto set it.\n");
            return true;
        }
        return false;
    }

    private boolean validateTeamColor(Team team, StringBuilder stringBuilder) {
        if (team.getColor() == null) {
            stringBuilder.append("§cThe team color has not been set. Use §e/teamsetup setColor <color> §cto set it.\n");
            return true;
        }
        return false;
    }

    private boolean validateSpawnLocation(Team team, StringBuilder stringBuilder) {
        if (team.getSpawnLoaction() == null) {
            stringBuilder.append("§cThe team spawn location has not been set. Use §e/teamsetup setSpawn §cto set it.\n");
            return true;
        }
        return false;
    }

    private boolean validateBedLocations(Team team, StringBuilder stringBuilder) {
        if (team.getUpperBedLocation() == null || team.getLowerBedLocation() == null) {
            stringBuilder.append("§cThe team bed locations have not been set. Use §e/teamsetup setBed §cto set them.\n");
            return true;
        }
        return false;
    }

    private boolean validateRegionLocations(Team team, StringBuilder stringBuilder) {
        if (team.getRegion_1() == null || team.getRegion_2() == null) {
            stringBuilder.append("§cThe team region locations have not been set. Use §e/teamsetup setRegion §cto set them.\n");
            return true;
        }
        return false;
    }

    private boolean validateTeamSpawners(Team team, StringBuilder stringBuilder) {
        if (team.getSpawners() == null || team.getSpawners().isEmpty()) {
            stringBuilder.append("§cAt least one team spawner must be configured. Use §e/teamsetup setSpawner §cto add spawners.\n");
            return true;
        }
        return false;
    }

    public enum TeamSetupStep {
        TEAM_NAME(1, "Team Name"),
        TEAM_COLOR(2, "Team Color"),
        SPAWN_LOCATION(3, "Spawn Location"),
        UPPER_BED_LOCATION(4, "Upper Bed Location"),
        LOWER_BED_LOCATION(5, "Lower Bed Location"),
        REGION_1(6, "Region Location 1"),
        REGION_2(7, "Region Location 2"),
        TEAM_SPAWNERS(8, "Team Spawners"),
        TEAM_SETUP_COMPLETE(9, "Team Setup Complete");

        private final int order;
        private final String displayName;

        TeamSetupStep(int order, String displayName) {
            this.order = order;
            this.displayName = displayName;
        }

        public int getOrder() {
            return order;
        }

        public String getDisplayName() {
            return displayName;
        }

        public TeamSetupStep getNext() {
            TeamSetupStep[] steps = values();
            return order < steps.length ? steps[order] : this;
        }
    }

    public TeamSetupStep getCurrentStep(Team team) {
        if (team == null) {
            throw new IllegalArgumentException("Team cannot be null");
        }

        if (isNullOrEmpty(team.getName())) {
            return TeamSetupStep.TEAM_NAME;
        }

        if (team.getColor() == null) {
            return TeamSetupStep.TEAM_COLOR;
        }

        if (team.getSpawnLoaction() == null) {
            return TeamSetupStep.SPAWN_LOCATION;
        }

        if (team.getUpperBedLocation() == null) {
            return TeamSetupStep.UPPER_BED_LOCATION;
        }

        if (team.getLowerBedLocation() == null) {
            return TeamSetupStep.LOWER_BED_LOCATION;
        }

        if (team.getRegion_1() == null) {
            return TeamSetupStep.REGION_1;
        }

        if (team.getRegion_2() == null) {
            return TeamSetupStep.REGION_2;
        }

        // Team spawners are optional
        return TeamSetupStep.TEAM_SETUP_COMPLETE;
    }

    public String getCurrentStepInfo(UUID uuid) {
        Team team = getCurrentTeamSetup(uuid);
        if (team == null) {
            return "Error: No active team setup found!";
        }

        TeamSetupStep currentStep = getCurrentStep(team);
        StringBuilder info = new StringBuilder();
        info.append("Current Step: ").append(currentStep.getDisplayName()).append("\n");

        switch (currentStep) {
            case TEAM_NAME:
                info.append("Action Required: Set a name for this team.\n");
                info.append("Usage: /teamsetup setName <name>");
                break;

            case TEAM_COLOR:
                info.append("Action Required: Set a color for this team.\n");
                info.append("Usage: /teamsetup setColor <color>");
                break;

            case SPAWN_LOCATION:
                info.append("Action Required: Set the spawn location for this team.\n");
                info.append("Usage: /teamsetup setSpawn");
                break;

            case UPPER_BED_LOCATION:
                info.append("Action Required: Set the upper bed location for this team.\n");
                info.append("Usage: /teamsetup setBed upper");
                break;

            case LOWER_BED_LOCATION:
                info.append("Action Required: Set the lower bed location for this team.\n");
                info.append("Usage: /teamsetup setBed lower");
                break;

            case REGION_1:
                info.append("Action Required: Set the first region location for this team.\n");
                info.append("Usage: /teamsetup setRegion1");
                break;

            case REGION_2:
                info.append("Action Required: Set the second region location for this team.\n");
                info.append("Usage: /teamsetup setRegion2");
                break;

            case TEAM_SPAWNERS:
                info.append("Action Required: Set team spawners for this team.\n");
                info.append("Usage: /teamsetup setSpawner <type>\n");
                info.append("Available types: ").append(Arrays.stream(SpawnerType.values())
                        .map(Enum::name)
                        .collect(Collectors.joining(", "))).append("\n");
                info.append("§eNote: You must set at least one TEAMSPAWNER before finishing the setup!");
                break;

            case TEAM_SETUP_COMPLETE:
                info.append("First you need to add at least 1 team spawner to the Team.");
                info.append("Action Required: You can now finish the team setup.\n");
                info.append("Usage: /teamsetup finish");
                break;

            default:
                info.append("Action Required: Unknown step, please check your team configuration.");
        }

        return info.toString();
    }

    public String getOverallProgress(UUID uuid) {
        if (!hasActiveMapSetup(uuid)) {
            return "No active map setup found.";
        }

        GameMap currentMap = getCurrentMap(uuid);
        int currentTeams = currentMap.getTeams() != null ? currentMap.getTeams().size() : 0;
        int maxTeams = currentMap.getMaxTeams() != null ? currentMap.getMaxTeams() : 0;

        StringBuilder progress = new StringBuilder();
        progress.append("=== Team Configuration Progress ===\n");
        progress.append("Map: ").append(currentMap.getName() != null ? currentMap.getName() : "Unknown").append("\n");
        progress.append("Teams configured: ").append(currentTeams).append("/").append(maxTeams).append("\n");

        if (currentMap.getTeams() != null) {
            progress.append("\nConfigured teams:\n");
            for (int i = 0; i < currentMap.getTeams().size(); i++) {
                Team team = currentMap.getTeams().get(i);
                progress.append("  ").append(i + 1).append(". ")
                        .append(team.getName()).append(" (")
                        .append(team.getColor()).append(")\n");
            }
        }

        if (hasActiveTeamSetup(uuid)) {
            progress.append("\nCurrently setting up:\n");
            progress.append(getCurrentStepInfo(uuid));
        } else if (currentTeams < maxTeams) {
            progress.append("\nNext: Start setup for team ").append(currentTeams + 1);
        } else {
            progress.append("\nAll teams configured! You can proceed with map setup.");
        }

        return progress.toString();
    }

    private boolean isNullOrEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }
}
