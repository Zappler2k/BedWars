package de.zappler2k.bedWars.setup.map;

import de.zappler2k.bedWars.map.objects.GameMap;
import de.zappler2k.bedWars.map.objects.Spawner;
import de.zappler2k.bedWars.map.objects.Team;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.plugin.Plugin;

import java.util.*;

public class TeamSetupManager {

private Map<UUID, Team> currentTeamSetups;
private Map<UUID, TeamSetupStep> currentStep;
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
    return mapSetupManager.GameMapSetup(uuid);
}

// Get the current map being set up
private GameMap getCurrentMap(UUID uuid) {
    return mapSetupManager.getGameMapSetup(uuid);
}

public String startTeamSetup(UUID uuid) {
    if (!hasActiveMapSetup(uuid)) {
        return "Error: You must have an active map setup before configuring teams. Start a map setup first!";
    }

    GameMap currentMap = getCurrentMap(uuid);
    if (currentMap.getMaxTeams() == null) {
        return "Error: Please set the maximum number of teams in your map setup first!";
    }

    if (currentMap.getTeams() != null && currentMap.getTeams().size() >= currentMap.getMaxTeams()) {
        return "Error: All teams for this map are already configured! (" +
                currentMap.getTeams().size() + "/" + currentMap.getMaxTeams() + ")";
    }

    currentTeamSetups.put(uuid, new Team());
    currentStep.put(uuid, TeamSetupStep.TEAM_NAME);

    int currentTeams = currentMap.getTeams() != null ? currentMap.getTeams().size() : 0;
    return "Team setup started! Configuring team " + (currentTeams + 1) + "/" + currentMap.getMaxTeams() +
            "\nNext step: Set a name for this team.";
}

public boolean hasActiveTeamSetup(UUID uuid) {
    return currentTeamSetups.containsKey(uuid);
}

public Team getCurrentTeamSetup(UUID uuid) {
    return currentTeamSetups.get(uuid);
}

public void setTeamName(UUID uuid, String name) {
    Team team = getCurrentTeamSetup(uuid);
    if (team != null) {
        team.setName(name);
    }
}

public void setTeamColor(UUID uuid, Color color) {
    Team team = getCurrentTeamSetup(uuid);
    if (team != null) {
        team.setColor(color);
    }
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

public void setDownerBedLocation(UUID uuid, Location location) {
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

public String finishTeamSetup(UUID uuid) {
    if (!hasActiveMapSetup(uuid)) {
        return "Error: No active map setup found!";
    }

    if (!hasActiveTeamSetup(uuid)) {
        return "Error: No active team setup found!";
    }

    Team team = getCurrentTeamSetup(uuid);
    GameMap currentMap = getCurrentMap(uuid);

    StringBuilder stringBuilder = new StringBuilder();
    boolean hasErrors = false;

    hasErrors |= validateTeamName(team, stringBuilder);
    hasErrors |= validateTeamColor(team, stringBuilder);
    hasErrors |= validateSpawnLocation(team, stringBuilder);
    hasErrors |= validateBedLocations(team, stringBuilder);
    hasErrors |= validateRegionLocations(team, stringBuilder);
    hasErrors |= validateTeamSpawners(team, stringBuilder);
    hasErrors |= validateUniqueTeamData(team, currentMap, stringBuilder);

    if (hasErrors) {
        return "There are some errors preventing the team setup from being completed:\n" + stringBuilder.toString();
    } else {
        // Add team to map
        if (currentMap.getTeams() == null) {
            currentMap.setTeams(new ArrayList<>());
        }
        currentMap.getTeams().add(team);

        // Clean up current team setup
        currentTeamSetups.remove(uuid);
        currentStep.remove(uuid);

        int currentTeams = currentMap.getTeams().size();
        int maxTeams = currentMap.getMaxTeams();

        String result = "Team '" + team.getName() + "' has been successfully configured!\n" +
                "Teams configured: " + currentTeams + "/" + maxTeams;

        if (currentTeams < maxTeams) {
            result += "\nYou can configure the next team by starting a new team setup.";
        } else {
            result += "\nAll teams for this map are now configured! You can proceed with the map setup.";
        }

        return result;
    }
}

private boolean validateTeamName(Team team, StringBuilder stringBuilder) {
    if (isNullOrEmpty(team.getName())) {
        stringBuilder.append("The team name was not set.\n");
        return true;
    }
    return false;
}

private boolean validateTeamColor(Team team, StringBuilder stringBuilder) {
    if (team.getColor() == null) {
        stringBuilder.append("The team color was not set.\n");
        return true;
    }
    return false;
}

private boolean validateSpawnLocation(Team team, StringBuilder stringBuilder) {
    if (team.getSpawnLoaction() == null) {
        stringBuilder.append("The team spawn location was not set.\n");
        return true;
    }
    return false;
}

private boolean validateBedLocations(Team team, StringBuilder stringBuilder) {
    boolean hasError = false;
    if (team.getUpperBedLocation() == null) {
        stringBuilder.append("The upper bed location was not set.\n");
        hasError = true;
    }
    if (team.getLowerBedLocation() == null) {
        stringBuilder.append("The lower bed location was not set.\n");
        hasError = true;
    }
    return hasError;
}

private boolean validateRegionLocations(Team team, StringBuilder stringBuilder) {
    boolean hasError = false;
    if (team.getRegion_1() == null) {
        stringBuilder.append("Region location 1 was not set.\n");
        hasError = true;
    }
    if (team.getRegion_2() == null) {
        stringBuilder.append("Region location 2 was not set.\n");
        hasError = true;
    }
    return hasError;
}

private boolean validateTeamSpawners(Team team, StringBuilder stringBuilder) {
    // Team spawners are optional, but if they exist, they should be valid
    if (team.getSpawners() != null && !team.getSpawners().isEmpty()) {
        for (int i = 0; i < team.getSpawners().size(); i++) {
            Spawner spawner = team.getSpawners().get(i);
            if (spawner.getSpawnerType() == null) {
                stringBuilder.append("Team spawner #" + (i + 1) + " has no spawner type set.\n");
                return true;
            }
            if (spawner.getLocation() == null) {
                stringBuilder.append("Team spawner #" + (i + 1) + " has no location set.\n");
                return true;
            }
        }
    }
    return false;
}

private boolean validateUniqueTeamData(Team team, GameMap gameMap, StringBuilder stringBuilder) {
    boolean hasError = false;

    if (gameMap.getTeams() != null) {
        for (Team existingTeam : gameMap.getTeams()) {
            if (existingTeam.getName() != null && existingTeam.getName().equalsIgnoreCase(team.getName())) {
                stringBuilder.append("A team with the name '" + team.getName() + "' already exists.\n");
                hasError = true;
            }
            if (existingTeam.getColor() != null && existingTeam.getColor().equals(team.getColor())) {
                stringBuilder.append("A team with the color '" + team.getColor() + "' already exists.\n");
                hasError = true;
            }
        }
    }

    return hasError;
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

    public int getOrder() { return order; }
    public String getDisplayName() { return displayName; }

    public TeamSetupStep getNext() {
        TeamSetupStep[] steps = values();
        return order < steps.length ? steps[order] : this;
    }
}

public TeamSetupStep getCurrentStep(Team team) {
    if (team == null) {
        throw new IllegalArgumentException("Team darf nicht null sein");
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

public String getCurrentStepInfo(Team team) {
    if (team == null) {
        return "Error: Team is null";
    }

    TeamSetupStep currentStep = getCurrentStep(team);
    return getStepDescription(currentStep, team);
}

public String getCurrentStepInfo(UUID uuid) {
    if (!hasActiveTeamSetup(uuid)) {
        return "Error: No active team setup found. Start a team setup first!";
    }

    Team team = getCurrentTeamSetup(uuid);
    return getCurrentStepInfo(team);
}

private String getStepDescription(TeamSetupStep step, Team team) {
    switch (step) {
        case TEAM_NAME:
            return "Current Step: " + step.getDisplayName() + "\n" +
                    "Action Required: Set a name for this team.";

        case TEAM_COLOR:
            return "Current Step: " + step.getDisplayName() + "\n" +
                    "Action Required: Set a color for this team.\n" +
                    "Current team name: " + (team.getName() != null ? team.getName() : "Not set");

        case SPAWN_LOCATION:
            return "Current Step: " + step.getDisplayName() + "\n" +
                    "Action Required: Set the spawn location for this team.\n" +
                    "Team: " + (team.getName() != null ? team.getName() : "Unknown") +
                    " (" + (team.getColor() != null ? team.getColor() : "No color") + ")";

        case UPPER_BED_LOCATION:
            return "Current Step: " + step.getDisplayName() + "\n" +
                    "Action Required: Set the upper bed location.\n" +
                    "Team: " + team.getName();

        case LOWER_BED_LOCATION:
            return "Current Step: " + step.getDisplayName() + "\n" +
                    "Action Required: Set the lower bed location.\n" +
                    "Team: " + team.getName();

        case REGION_1:
            return "Current Step: " + step.getDisplayName() + "\n" +
                    "Action Required: Set the first region corner.\n" +
                    "Team: " + team.getName();

        case REGION_2:
            return "Current Step: " + step.getDisplayName() + "\n" +
                    "Action Required: Set the second region corner.\n" +
                    "Team: " + team.getName();

        case TEAM_SPAWNERS:
            int currentSpawners = team.getSpawners() != null ? team.getSpawners().size() : 0;
            return "Current Step: " + step.getDisplayName() + "\n" +
                    "Action Required: Configure team-specific spawners.\n" +
                    "Current spawners: " + currentSpawners + "\n" +
                    "Team: " + team.getName() + "\n" +
                    "You can skip this step if no team-specific spawners are needed.";

        case TEAM_SETUP_COMPLETE:
            return "Current Step: " + step.getDisplayName() + "\n" +
                    "Action Required: Your team setup is complete! Type /teamsetup finish.\n" +
                    "Team: " + team.getName() + " (" + team.getColor() + ")";

        default:
            return "Current Step: Unknown\n" +
                    "Action Required: Please check your team configuration.";
    }
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
