package de.zappler2k.bedWars.commands.teamsetup.init;

import de.zappler2k.bedWars.command.SubCommand;
import de.zappler2k.bedWars.commands.teamsetup.TeamSetup;
import de.zappler2k.bedWars.setup.map.TeamSetupManager.TeamSetupStep;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public class SetTeamRegionCommand extends SubCommand {

    private TeamSetup teamSetup;

    public SetTeamRegionCommand(String permission, TeamSetup teamSetup) {
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
        if (currentStep != TeamSetupStep.REGION_1 && currentStep != TeamSetupStep.REGION_2) {
            player.sendMessage("§cYou can only set the team region in the Region Location step!");
            player.sendMessage("§7Current step: §e" + teamSetup.getTeamSetupManager().getCurrentStepInfo(uuid));
            return true;
        }

        // Validate arguments
        if (args.length != 2) {
            player.sendMessage("§cUsage: §e/teamsetup setRegion <1|2>");
            player.sendMessage("§7Example: §e/teamsetup setRegion 1");
            return true;
        }

        String point = args[1];
        if (!point.equals("1") && !point.equals("2")) {
            player.sendMessage("§cInvalid point! Use §e1 §cor §e2§c.");
            return true;
        }

        // Validate step order
        if (point.equals("2") && currentStep == TeamSetupStep.REGION_1) {
            player.sendMessage("§cYou must set region point 1 before setting point 2!");
            player.sendMessage("§7Use §e/teamsetup setRegion 1 §7first.");
            return true;
        }

        // Set the region point
        if (point.equals("1")) {
            teamSetup.getTeamSetupManager().setRegion1(uuid, player.getLocation());
            player.sendMessage("§aSuccessfully set first region corner!");
        } else {
            teamSetup.getTeamSetupManager().setRegion2(uuid, player.getLocation());
            player.sendMessage("§aSuccessfully set second region corner!");
        }
        player.sendMessage("§7Current step: §e" + teamSetup.getTeamSetupManager().getCurrentStepInfo(uuid));
        return true;
    }

    @Override
    public List<String> getSubCommandTabComplete(CommandSender sender, String[] args) {
        if (args.length == 2) {
            return List.of("1", "2");
        }
        return List.of();
    }
} 