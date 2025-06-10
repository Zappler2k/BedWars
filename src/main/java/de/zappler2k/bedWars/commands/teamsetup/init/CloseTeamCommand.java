package de.zappler2k.bedWars.commands.teamsetup.init;

import de.zappler2k.bedWars.command.SubCommand;
import de.zappler2k.bedWars.commands.teamsetup.TeamSetup;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public class CloseTeamCommand extends SubCommand {

    private TeamSetup teamSetup;

    public CloseTeamCommand(String permission, TeamSetup teamSetup) {
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

        // Close the team setup
        teamSetup.getTeamSetupManager().getCurrentTeamSetups().remove(uuid);
        teamSetup.getTeamSetupManager().getCurrentStep().remove(uuid);
        player.sendMessage("§aSuccessfully closed team setup!");
        player.sendMessage("§7You can start a new team setup using §e/teamsetup start§7.");
        return true;
    }

    @Override
    public List<String> getSubCommandTabComplete(CommandSender sender, String[] args) {
        return List.of();
    }
} 