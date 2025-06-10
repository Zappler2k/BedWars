package de.zappler2k.bedWars.commands.mapsetup.init;

import de.zappler2k.bedWars.command.SubCommand;
import de.zappler2k.bedWars.commands.mapsetup.MapSetup;
import de.zappler2k.bedWars.map.objects.Spawner;
import de.zappler2k.bedWars.map.objects.init.SpawnerType;
import de.zappler2k.bedWars.setup.map.MapSetupManager.SetupStep;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class SetSpawnerCommand extends SubCommand {

    private MapSetup mapSetup;

    public SetSpawnerCommand(String permission, MapSetup mapSetup) {
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
        if (currentStep != SetupStep.SPAWNER_CONFIGURATION && currentStep != SetupStep.VILLAGER_CONFIGURATION && currentStep != SetupStep.SETUP_COMPLETE) {
            player.sendMessage("§cYou can only set spawners after team configuration is complete!");
            player.sendMessage("§eCurrent step: §c" + currentStep.getDisplayName());
            player.sendMessage(mapSetup.getMapSetupManager().getCurrentStepInfo(uuid));
            return true;
        }

        // Validate arguments
        if (args.length != 2) {
            player.sendMessage("§cUsage: §e/mapsetup setSpawner <type>");
            player.sendMessage("§7Available spawner types:");
            for (SpawnerType type : SpawnerType.values()) {
                player.sendMessage("§7- §e" + type.name());
            }
            return true;
        }

        String spawnerTypeStr = args[1].trim().toUpperCase();
        if (spawnerTypeStr.isEmpty()) {
            player.sendMessage("§cThe spawner type cannot be empty! Please provide a valid spawner type.");
            return true;
        }

        // Validate spawner type
        SpawnerType spawnerType;
        try {
            spawnerType = SpawnerType.valueOf(spawnerTypeStr);
        } catch (IllegalArgumentException e) {
            player.sendMessage("§cInvalid spawner type '§e" + spawnerTypeStr + "§c'!");
            player.sendMessage("§7Available spawner types:");
            for (SpawnerType type : SpawnerType.values()) {
                player.sendMessage("§7- §e" + type.name());
            }
            return true;
        }

        // Add the spawner
        Spawner spawner = new Spawner(spawnerType, player.getLocation());
        mapSetup.getMapSetupManager().addSpawner(uuid, spawner);
        player.sendMessage("§aSuccessfully added a spawner of type: §e" + spawnerType.name());
        player.sendMessage(mapSetup.getMapSetupManager().getCurrentStepInfo(uuid));
        return true;
    }

    @Override
    public List<String> getSubCommandTabComplete(CommandSender sender, String[] args) {
        if (args.length == 2) {
            return Arrays.stream(SpawnerType.values())
                .map(Enum::name)
                .toList();
        }
        return List.of();
    }
}
