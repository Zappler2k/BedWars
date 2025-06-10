package de.zappler2k.bedWars.commands.mapsetup.init;

import de.zappler2k.bedWars.command.SubCommand;
import de.zappler2k.bedWars.commands.mapsetup.MapSetup;
import de.zappler2k.bedWars.setup.map.MapSetupManager.SetupStep;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public class SetMaxPlayerPerTeamCommand extends SubCommand {

    private MapSetup mapSetup;
    private static final int MIN_PLAYERS = 1;
    private static final int MAX_PLAYERS = 4;

    public SetMaxPlayerPerTeamCommand(String permission, MapSetup mapSetup) {
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
        if (currentStep != SetupStep.MAX_PLAYERS_PER_TEAM) {
            player.sendMessage("§cYou can only set the maximum players per team in the Maximum Players per Team step!");
            player.sendMessage("§eCurrent step: §c" + currentStep.getDisplayName());
            player.sendMessage(mapSetup.getMapSetupManager().getCurrentStepInfo(uuid));
            return true;
        }

        // Validate arguments
        if (args.length != 2) {
            player.sendMessage("§cUsage: §e/mapsetup setMaxPlayersPerTeam <number>");
            player.sendMessage("§7Example: §e/mapsetup setMaxPlayersPerTeam 4");
            player.sendMessage("§7Valid range: §e" + MIN_PLAYERS + "§7-§e" + MAX_PLAYERS + " §7players per team");
            return true;
        }

        // Parse and validate number
        int maxPlayers;
        try {
            maxPlayers = Integer.parseInt(args[1].trim());
        } catch (NumberFormatException e) {
            player.sendMessage("§cPlease enter a valid number between §e" + MIN_PLAYERS + "§c and §e" + MAX_PLAYERS + "§c!");
            player.sendMessage("§7Example: §e/mapsetup setMaxPlayersPerTeam 4");
            return true;
        }

        // Validate player count range
        if (maxPlayers < MIN_PLAYERS || maxPlayers > MAX_PLAYERS) {
            player.sendMessage("§cThe number of players must be between §e" + MIN_PLAYERS + "§c and §e" + MAX_PLAYERS + "§c!");
            return true;
        }

        // Check if min players is already set and validate against it
        Integer minPlayers = mapSetup.getMapSetupManager().getGameMapSetup(uuid).getMinPlayersPerTeam();
        if (minPlayers != null && maxPlayers < minPlayers) {
            player.sendMessage("§cThe maximum players per team (§e" + maxPlayers + "§c) cannot be less than the minimum players per team (§e" + minPlayers + "§c)!");
            return true;
        }

        // Set the max players
        mapSetup.getMapSetupManager().setMaxPlayersPerTeam(uuid, maxPlayers);
        player.sendMessage("§aSuccessfully set the maximum players per team to: §e" + maxPlayers);
        player.sendMessage(mapSetup.getMapSetupManager().getCurrentStepInfo(uuid));
        return true;
    }

    @Override
    public List<String> getSubCommandTabComplete(CommandSender sender, String[] args) {
        if (args.length == 2) {
            return List.of("1", "2", "3", "4");
        }
        return List.of("<number>");
    }
}
