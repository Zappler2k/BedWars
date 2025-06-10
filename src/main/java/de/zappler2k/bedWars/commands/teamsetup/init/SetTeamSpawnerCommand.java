package de.zappler2k.bedWars.commands.teamsetup.init;

import de.zappler2k.bedWars.command.SubCommand;
import de.zappler2k.bedWars.commands.teamsetup.TeamSetup;
import de.zappler2k.bedWars.map.objects.Spawner;
import de.zappler2k.bedWars.map.objects.init.SpawnerType;
import de.zappler2k.bedWars.setup.map.TeamSetupManager.TeamSetupStep;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class SetTeamSpawnerCommand extends SubCommand {

    private TeamSetup teamSetup;

    public SetTeamSpawnerCommand(String permission, TeamSetup teamSetup) {
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
        if (currentStep != TeamSetupStep.TEAM_SPAWNERS && currentStep != TeamSetupStep.TEAM_SETUP_COMPLETE) {
            player.sendMessage("§cYou can only set team spawners in the Team Spawner step!");
            player.sendMessage("§7Current step: §e" + teamSetup.getTeamSetupManager().getCurrentStepInfo(uuid));
            return true;
        }

        // Validate arguments
        if (args.length != 2) {
            player.sendMessage("§cUsage: §e/teamsetup setSpawner <type>");
            player.sendMessage("§7Example: §e/teamsetup setSpawner TEAMSPAWNER");
            player.sendMessage("§7Available types: §e" + getAvailableTypes());
            player.sendMessage("§eNote: You must set at least one TEAMSPAWNER before finishing the setup!");
            return true;
        }

        try {
            SpawnerType type = SpawnerType.valueOf(args[1].toUpperCase());
            
            // Set the team spawner
            Spawner spawner = new Spawner(type, player.getLocation());
            teamSetup.getTeamSetupManager().addTeamSpawner(uuid, spawner);
            
            // Check if this is the first team spawner
            boolean hasTeamSpawner = teamSetup.getTeamSetupManager().getTeamSpawners(uuid).stream()
                    .anyMatch(s -> s.getSpawnerType() == SpawnerType.TEAMSPAWNER);
            
            if (type == SpawnerType.TEAMSPAWNER) {
                if (!hasTeamSpawner) {
                    player.sendMessage("§aSuccessfully set first team spawner!");
                    player.sendMessage("§7You can now finish the setup.");
                } else {
                    player.sendMessage("§aSuccessfully added another team spawner!");
                }
            } else {
                player.sendMessage("§aSuccessfully added §e" + type.name() + " §aspawner!");
                if (!hasTeamSpawner) {
                    player.sendMessage("§eNote: You still need to set at least one TEAMSPAWNER before finishing the setup!");
                }
            }
            
            player.sendMessage("§7Current step: §e" + teamSetup.getTeamSetupManager().getCurrentStepInfo(uuid));
            return true;
        } catch (IllegalArgumentException e) {
            player.sendMessage("§cInvalid spawner type! Available types: §e" + getAvailableTypes());
            player.sendMessage("§eNote: You must set at least one TEAMSPAWNER before finishing the setup!");
            return true;
        }
    }

    private String getAvailableTypes() {
        return Arrays.stream(SpawnerType.values())
                .map(Enum::name)
                .collect(Collectors.joining(", "));
    }

    @Override
    public List<String> getSubCommandTabComplete(CommandSender sender, String[] args) {
        if (args.length == 2) {
            return Arrays.stream(SpawnerType.values())
                    .map(Enum::name)
                    .map(String::toLowerCase)
                    .filter(type -> type.startsWith(args[1].toLowerCase()))
                    .collect(Collectors.toList());
        }
        return List.of();
    }
} 