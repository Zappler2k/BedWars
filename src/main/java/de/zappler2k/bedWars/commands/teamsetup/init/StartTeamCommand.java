package de.zappler2k.bedWars.commands.teamsetup.init;

import de.zappler2k.bedWars.command.SubCommand;
import de.zappler2k.bedWars.commands.teamsetup.TeamSetup;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public class StartTeamCommand extends SubCommand {

    private TeamSetup teamSetup;

    public StartTeamCommand(String permission, TeamSetup teamSetup) {
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

        // Check if player already has an active team setup
        if (teamSetup.getTeamSetupManager().hasActiveTeamSetup(uuid)) {
            player.sendMessage("§cYou already have an active team setup!");
            player.sendMessage("§7Current step: §e" + teamSetup.getTeamSetupManager().getCurrentStepInfo(uuid));
            return true;
        }

        // Start the team setup
        String result = teamSetup.getTeamSetupManager().startTeamSetup(uuid);
        if (result.startsWith("Error:")) {
            player.sendMessage("§c" + result);
        } else {
            player.sendMessage("§aSuccessfully started team setup!");
            player.sendMessage("§7Current step: §e" + teamSetup.getTeamSetupManager().getCurrentStepInfo(uuid));
        }
        return true;
    }

    @Override
    public List<String> getSubCommandTabComplete(CommandSender sender, String[] args) {
        return List.of();
    }
} 