package de.zappler2k.bedWars.commands.mapsetup.init;

import de.zappler2k.bedWars.command.SubCommand;
import de.zappler2k.bedWars.commands.mapsetup.MapSetup;
import de.zappler2k.bedWars.map.objects.Villager;
import de.zappler2k.bedWars.map.objects.init.VillagerType;
import de.zappler2k.bedWars.setup.map.MapSetupManager.SetupStep;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class SetVillagerCommand extends SubCommand {

    private MapSetup mapSetup;

    public SetVillagerCommand(String permission, MapSetup mapSetup) {
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
            player.sendMessage("§cYou can only set villagers after team configuration is complete!");
            player.sendMessage("§eCurrent step: §c" + currentStep.getDisplayName());
            player.sendMessage(mapSetup.getMapSetupManager().getCurrentStepInfo(uuid));
            return true;
        }

        // Validate arguments
        if (args.length != 2) {
            player.sendMessage("§cUsage: §e/mapsetup setVillager <type>");
            player.sendMessage("§7Available villager types:");
            for (VillagerType type : VillagerType.values()) {
                player.sendMessage("§7- §e" + type.name());
            }
            return true;
        }

        String villagerTypeStr = args[1].trim().toUpperCase();
        if (villagerTypeStr.isEmpty()) {
            player.sendMessage("§cThe villager type cannot be empty! Please provide a valid villager type.");
            return true;
        }

        // Validate villager type
        VillagerType villagerType;
        try {
            villagerType = VillagerType.valueOf(villagerTypeStr);
        } catch (IllegalArgumentException e) {
            player.sendMessage("§cInvalid villager type '§e" + villagerTypeStr + "§c'!");
            player.sendMessage("§7Available villager types:");
            for (VillagerType type : VillagerType.values()) {
                player.sendMessage("§7- §e" + type.name());
            }
            return true;
        }

        // Check if we already have this type of villager
        if (mapSetup.getMapSetupManager().getGameMapSetup(uuid).getVillagers() != null) {
            boolean hasShop = false;
            boolean hasUpgrade = false;
            for (Villager existingVillager : mapSetup.getMapSetupManager().getGameMapSetup(uuid).getVillagers()) {
                if (existingVillager.getVillagerType() == VillagerType.SHOP) hasShop = true;
                if (existingVillager.getVillagerType() == VillagerType.UPGRADE) hasUpgrade = true;
            }

            if (villagerType == VillagerType.SHOP && hasShop) {
                player.sendMessage("§eNote: §cYou already have a Shop villager set! You can still add more if needed.");
            } else if (villagerType == VillagerType.UPGRADE && hasUpgrade) {
                player.sendMessage("§eNote: §cYou already have an Upgrade Manager villager set! You can still add more if needed.");
            }
        }

        // Add the villager
        Villager villager = new Villager(villagerType, player.getLocation());
        mapSetup.getMapSetupManager().addVillager(uuid, villager);
        player.sendMessage("§aSuccessfully added a villager of type: §e" + villagerType.name());
        player.sendMessage(mapSetup.getMapSetupManager().getCurrentStepInfo(uuid));
        return true;
    }

    @Override
    public List<String> getSubCommandTabComplete(CommandSender sender, String[] args) {
        if (args.length == 2) {
            return Arrays.stream(VillagerType.values())
                    .map(Enum::name)
                    .toList();
        }
        return List.of();
    }
}
