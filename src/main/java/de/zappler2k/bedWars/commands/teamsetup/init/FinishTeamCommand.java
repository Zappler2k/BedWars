package de.zappler2k.bedWars.commands.teamsetup.init;

import de.zappler2k.bedWars.command.SubCommand;
import de.zappler2k.bedWars.commands.teamsetup.TeamSetup;
import de.zappler2k.bedWars.map.objects.init.SpawnerType;
import de.zappler2k.bedWars.setup.map.TeamSetupManager.TeamSetupStep;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public class FinishTeamCommand extends SubCommand {

    private TeamSetup teamSetup;

    public FinishTeamCommand(String permission, TeamSetup teamSetup) {
        super(permission);
        this.teamSetup = teamSetup;
    }

    @Override
    public boolean executeSubCommand(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cThis command can only be executed by players in-game!");
            return true;
        }

        Player player = (Player) sender;
        UUID uuid = player.getUniqueId();

        // Check if player has started a setup
        if (!teamSetup.getTeamSetupManager().hasActiveTeamSetup(uuid)) {
            player.sendMessage("§cYou need to start a team setup first! Use §e/teamsetup start §cto begin.");
            return true;
        }

        // Check if we're in the correct step
        TeamSetupStep currentStep = teamSetup.getTeamSetupManager().getCurrentStep(teamSetup.getTeamSetupManager().getCurrentTeamSetup(uuid));
        if (currentStep != TeamSetupStep.TEAM_SETUP_COMPLETE) {
            player.sendMessage("§cYou can only finish the team setup when all steps are complete!");
            player.sendMessage("§7Current step: §e" + teamSetup.getTeamSetupManager().getCurrentStepInfo(uuid));
            return true;
        }

        // Validate team setup requirements
        StringBuilder validationErrors = new StringBuilder();
        var currentTeam = teamSetup.getTeamSetupManager().getCurrentTeamSetup(uuid);
        
        // Check if team has a name
        if (currentTeam.getName() == null || currentTeam.getName().isEmpty()) {
            validationErrors.append("§c- Missing team name (Use §e/teamsetup setName <name>§c)\n");
        }
        
        // Check if team has a color
        if (currentTeam.getColor() == null) {
            validationErrors.append("§c- Missing team color (Use §e/teamsetup setColor <color>§c)\n");
        }
        
        // Check if team has a spawn location
        if (currentTeam.getSpawnLoaction() == null) {
            validationErrors.append("§c- Missing team spawn location (Use §e/teamsetup setSpawn§c)\n");
        }
        
        // Check if team has bed locations
        if (currentTeam.getUpperBedLocation() == null || currentTeam.getLowerBedLocation() == null) {
            validationErrors.append("§c- Missing team bed locations (Use §e/teamsetup setBed§c)\n");
        }
        
        // Check if team has region points
        if (currentTeam.getRegion_1() == null || currentTeam.getRegion_2() == null) {
            validationErrors.append("§c- Missing team region points (Use §e/teamsetup setRegion <1|2>§c)\n");
        }
        
        // Check if team has at least one team spawner
        boolean hasTeamSpawner = currentTeam.getSpawners() != null && 
            currentTeam.getSpawners().stream()
                .anyMatch(s -> s.getSpawnerType() == SpawnerType.TEAMSPAWNER);
        if (!hasTeamSpawner) {
            validationErrors.append("§c- Missing team spawner (Use §e/teamsetup setSpawner TEAMSPAWNER§c)\n");
        }

        // If there are validation errors, show them and don't finish
        if (validationErrors.length() > 0) {
            player.sendMessage("§cCannot finish team setup. The following requirements are missing:");
            player.sendMessage(validationErrors.toString());
            player.sendMessage("§7Please add the missing requirements and try again.");
            return true;
        }

        // Finish the team setup
        String result = teamSetup.getTeamSetupManager().finishTeamSetup(uuid);
        if (result.startsWith("Error:")) {
            player.sendMessage("§c" + result);
            player.sendMessage("§7Please fix the errors and try again.");
        } else {
            player.sendMessage("§aSuccessfully finished team setup!");
            player.sendMessage("§7Team Information:");
            player.sendMessage("§7- Name: §e" + currentTeam.getName());
            player.sendMessage("§7- Color: §e" + currentTeam.getColor().toString());
            player.sendMessage("§7- Spawners: §e" + (currentTeam.getSpawners() != null ? currentTeam.getSpawners().size() : 0) + " §7placed");
            
            // Add overall progress information
            String progress = teamSetup.getTeamSetupManager().getOverallProgress(uuid);
            player.sendMessage("\n§7" + progress);
            
            player.sendMessage("\n§7You can now start setting up another team or finish the map setup.");
        }
        return true;
    }

    @Override
    public List<String> getSubCommandTabComplete(CommandSender sender, String[] args) {
        return List.of();
    }
} 