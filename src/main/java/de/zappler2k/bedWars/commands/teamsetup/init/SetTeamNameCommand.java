package de.zappler2k.bedWars.commands.teamsetup.init;

import de.zappler2k.bedWars.command.SubCommand;
import de.zappler2k.bedWars.commands.teamsetup.TeamSetup;
import de.zappler2k.bedWars.objects.map.GameMap;
import de.zappler2k.bedWars.objects.map.Team;
import de.zappler2k.bedWars.setup.map.TeamSetupManager.TeamSetupStep;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public class SetTeamNameCommand extends SubCommand {

    private TeamSetup teamSetup;

    public SetTeamNameCommand(String permission, TeamSetup teamSetup) {
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
        if (currentStep != TeamSetupStep.TEAM_NAME) {
            player.sendMessage("§cYou can only set the team name in the Team Name step!");
            player.sendMessage("§7Current step: §e" + teamSetup.getTeamSetupManager().getCurrentStepInfo(uuid));
            return true;
        }

        // Validate arguments
        if (args.length != 2) {
            player.sendMessage("§cUsage: §e/teamsetup setName <name>");
            player.sendMessage("§7Example: §e/teamsetup setName Red");
            return true;
        }

        String name = args[1].trim();
        if (name.isEmpty()) {
            player.sendMessage("§cThe team name cannot be empty!");
            return true;
        }

        // Check for duplicate team names in the current map
        GameMap currentMap = teamSetup.getTeamSetupManager().getMapSetupManager().getGameMapSetup(uuid);
        if (currentMap != null && currentMap.getTeams() != null) {
            for (Team existingTeam : currentMap.getTeams()) {
                if (existingTeam.getName() != null && existingTeam.getName().equalsIgnoreCase(name)) {
                    player.sendMessage("§cA team with the name §e" + name + " §calready exists!");
                    player.sendMessage("§7Please choose a different name.");
                    return true;
                }
            }
        }

        // Try to set the team name and handle the result
        String result = teamSetup.getTeamSetupManager().setTeamName(uuid, name);
        if (result != null) {
            player.sendMessage(result);
            return true;
        }

        player.sendMessage("§aSuccessfully set team name to §e" + name + "§a!");
        player.sendMessage("§7Current step: §e" + teamSetup.getTeamSetupManager().getCurrentStepInfo(uuid));
        return true;
    }

    @Override
    public List<String> getSubCommandTabComplete(CommandSender sender, String[] args) {
        if (args.length == 2) {
            return List.of("<name>");
        }
        return List.of();
    }
} 