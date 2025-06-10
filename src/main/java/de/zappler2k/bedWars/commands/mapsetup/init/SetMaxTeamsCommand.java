package de.zappler2k.bedWars.commands.mapsetup.init;

import de.zappler2k.bedWars.command.SubCommand;
import de.zappler2k.bedWars.commands.mapsetup.MapSetup;
import de.zappler2k.bedWars.setup.map.MapSetupManager.SetupStep;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public class SetMaxTeamsCommand extends SubCommand {

    private MapSetup mapSetup;

    public SetMaxTeamsCommand(String permission, MapSetup mapSetup) {
        super(permission);
        this.mapSetup = mapSetup;
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
        if (mapSetup.getMapSetupManager().getGameMapSetup(uuid) == null) {
            player.sendMessage("§cYou need to start a map setup first! Use §e/mapsetup §cto begin.");
            return true;
        }

        // Check if we're in the correct step
        SetupStep currentStep = mapSetup.getMapSetupManager().getCurrentStep(mapSetup.getMapSetupManager().getGameMapSetup(uuid));
        if (currentStep != SetupStep.MAX_TEAMS) {
            player.sendMessage("§cYou can only set the maximum teams in the Maximum Teams step!");
            player.sendMessage("§eCurrent step: §c" + currentStep.getDisplayName());
            player.sendMessage(mapSetup.getMapSetupManager().getCurrentStepInfo(uuid));
            return true;
        }

        // Validate arguments
        if (args.length != 2) {
            player.sendMessage("§cUsage: §e/mapsetup setMaxTeams <amount>");
            player.sendMessage("§7Example: §e/mapsetup setMaxTeams 4");
            return true;
        }

        String amountStr = args[1].trim();
        if (amountStr.isEmpty()) {
            player.sendMessage("§cPlease provide a valid number for the maximum teams!");
            return true;
        }

        try {
            int amount = Integer.parseInt(amountStr);
            if (amount <= 0) {
                player.sendMessage("§cThe maximum number of teams must be greater than zero!");
                return true;
            }
            if (amount > 16) {
                player.sendMessage("§cThe maximum number of teams cannot exceed 16!");
                return true;
            }

            mapSetup.getMapSetupManager().setMaxTeams(uuid, amount);
            player.sendMessage("§aSuccessfully set the maximum teams to: §e" + amount);
            player.sendMessage(mapSetup.getMapSetupManager().getCurrentStepInfo(uuid));
        } catch (NumberFormatException e) {
            player.sendMessage("§cPlease enter a valid number for the maximum teams!");
            player.sendMessage("§7Example: §e/mapsetup setMaxTeams 4");
        }
        return true;
    }

    @Override
    public List<String> getSubCommandTabComplete(CommandSender sender, String[] args) {
        if (args.length == 2) {
            return List.of("2", "4", "8", "12", "16");
        }
        return List.of("<amount>");
    }
}
