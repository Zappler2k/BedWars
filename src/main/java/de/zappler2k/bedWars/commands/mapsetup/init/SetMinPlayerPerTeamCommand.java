package de.zappler2k.bedWars.commands.mapsetup.init;

import de.zappler2k.bedWars.command.SubCommand;
import de.zappler2k.bedWars.commands.mapsetup.MapSetup;
import de.zappler2k.bedWars.map.objects.GameMap;
import de.zappler2k.bedWars.setup.map.MapSetupManager.SetupStep;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SetMinPlayerPerTeamCommand extends SubCommand {

    private MapSetup mapSetup;
    private static final int MIN_PLAYERS = 1;

    public SetMinPlayerPerTeamCommand(String permission, MapSetup mapSetup) {
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
        if (currentStep != SetupStep.MIN_PLAYERS_PER_TEAM) {
            player.sendMessage("§cYou can only set the minimum players per team in the Minimum Players per Team step!");
            player.sendMessage("§eCurrent step: §c" + currentStep.getDisplayName());
            player.sendMessage(mapSetup.getMapSetupManager().getCurrentStepInfo(uuid));
            return true;
        }

        // Validate arguments
        if (args.length != 2) {
            player.sendMessage("§cUsage: §e/mapsetup setMinPlayersPerTeam <number>");
            player.sendMessage("§7Example: §e/mapsetup setMinPlayersPerTeam 1");
            player.sendMessage("§7Minimum value: §e" + MIN_PLAYERS + " §7player per team");
            return true;
        }

        // Parse and validate number
        int minPlayers;
        try {
            minPlayers = Integer.parseInt(args[1].trim());
        } catch (NumberFormatException e) {
            player.sendMessage("§cPlease enter a valid number. Minimum is §e" + MIN_PLAYERS + "§c!");
            player.sendMessage("§7Example: §e/mapsetup setMinPlayersPerTeam 1");
            return true;
        }

        // Validate minimum player count
        if (minPlayers < MIN_PLAYERS) {
            player.sendMessage("§cThe minimum number of players must be at least §e" + MIN_PLAYERS + "§c!");
            return true;
        }

        // Check if max players is already set and validate against it
        Integer maxPlayers = mapSetup.getMapSetupManager().getGameMapSetup(uuid).getMaxPlayersPerTeam();
        if (maxPlayers != null && minPlayers > maxPlayers) {
            player.sendMessage("§cThe minimum players per team (§e" + minPlayers + "§c) cannot be greater than the maximum players per team (§e" + maxPlayers + "§c)!");
            return true;
        }

        // Set the min players
        mapSetup.getMapSetupManager().setMinPlayersPerTeam(uuid, minPlayers);
        player.sendMessage("§aSuccessfully set the minimum players per team to: §e" + minPlayers);
        player.sendMessage(mapSetup.getMapSetupManager().getCurrentStepInfo(uuid));
        return true;
    }

    @Override
    public List<String> getSubCommandTabComplete(CommandSender sender, String[] args) {
        if (args.length == 2 && sender instanceof Player) {
            Player player = (Player) sender;
            UUID uuid = player.getUniqueId();

            // Get the current map setup
            GameMap gameMap = mapSetup.getMapSetupManager().getGameMapSetup(uuid);
            if (gameMap == null) {
                return List.of("<number>");
            }

            // Get max players per team
            Integer maxPlayers = gameMap.getMaxPlayersPerTeam();
            if (maxPlayers == null) {
                return List.of("<number>");
            }

            // Generate list of valid minimum player counts
            List<String> validCounts = new ArrayList<>();
            for (int i = MIN_PLAYERS; i <= maxPlayers; i++) {
                validCounts.add(String.valueOf(i));
            }
            return validCounts;
        }
        return List.of("<number>");
    }
}
