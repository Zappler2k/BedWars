package de.zappler2k.bedWars.commands.teamsetup.init;

import de.zappler2k.bedWars.command.SubCommand;
import de.zappler2k.bedWars.commands.teamsetup.TeamSetup;
import de.zappler2k.bedWars.setup.map.TeamSetupManager.TeamSetupStep;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public class SetTeamSpawnCommand extends SubCommand {

    private TeamSetup teamSetup;

    public SetTeamSpawnCommand(String permission, TeamSetup teamSetup) {
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
        if (currentStep != TeamSetupStep.SPAWN_LOCATION) {
            player.sendMessage("§cYou can only set the team spawn in the Spawn Location step!");
            player.sendMessage("§7Current step: §e" + teamSetup.getTeamSetupManager().getCurrentStepInfo(uuid));
            return true;
        }

        // Set the team spawn
        teamSetup.getTeamSetupManager().setSpawnLocation(uuid, player.getLocation());
        player.sendMessage("§aSuccessfully set team spawn location!");
        player.sendMessage("§7Current step: §e" + teamSetup.getTeamSetupManager().getCurrentStepInfo(uuid));
        return true;
    }

    @Override
    public List<String> getSubCommandTabComplete(CommandSender sender, String[] args) {
        return List.of();
    }
} 