package de.zappler2k.bedWars.commands.mapsetup.init;

import de.zappler2k.bedWars.command.SubCommand;
import de.zappler2k.bedWars.commands.mapsetup.MapSetup;
import de.zappler2k.bedWars.objects.map.GameMap;
import de.zappler2k.bedWars.objects.map.Spawner;
import de.zappler2k.bedWars.objects.map.Villager;
import de.zappler2k.bedWars.objects.map.init.SpawnerType;
import de.zappler2k.bedWars.objects.map.init.VillagerType;
import de.zappler2k.bedWars.setup.map.MapSetupManager.SetupStep;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public class FinishMapCommand extends SubCommand {

    private MapSetup mapSetup;

    public FinishMapCommand(String permission, MapSetup mapSetup) {
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
        GameMap gameMap = mapSetup.getMapSetupManager().getGameMapSetup(uuid);
        if (gameMap == null) {
            player.sendMessage("§cYou need to start a map setup first! Use §e/mapsetup §cto begin.");
            return true;
        }

        // Check if we're in the correct step
        SetupStep currentStep = mapSetup.getMapSetupManager().getCurrentStep(gameMap);
        if (currentStep != SetupStep.SETUP_COMPLETE) {
            player.sendMessage("§cYou can only finish the map setup when all steps are complete!");
            player.sendMessage("§eCurrent step: §c" + currentStep.getDisplayName());
            player.sendMessage(mapSetup.getMapSetupManager().getCurrentStepInfo(uuid));
            return true;
        }

        // Validate all requirements before finishing
        StringBuilder validationErrors = new StringBuilder();

        // Check map name and world
        if (gameMap.getName() == null || gameMap.getName().isEmpty()) {
            validationErrors.append("§c- Missing map name (Use §e/mapsetup setName <name>§c)\n");
        }
        if (gameMap.getWorldName() == null || gameMap.getWorldName().isEmpty()) {
            validationErrors.append("§c- Missing world name (Use §e/mapsetup setWorldName <world>§c)\n");
        }

        // Check team requirements
        if (gameMap.getTeams() == null || gameMap.getTeams().isEmpty()) {
            validationErrors.append("§c- No teams have been set up yet (Use §e/teamsetup start§c)\n");
        } else {
            // Validate each team
            for (int i = 0; i < gameMap.getTeams().size(); i++) {
                var team = gameMap.getTeams().get(i);
                if (team.getName() == null || team.getName().isEmpty()) {
                    validationErrors.append("§c- Team " + (i + 1) + " is missing a name\n");
                }
                if (team.getColor() == null) {
                    validationErrors.append("§c- Team " + (i + 1) + " is missing a color\n");
                }
                if (team.getSpawnLoaction() == null) {
                    validationErrors.append("§c- Team " + (i + 1) + " is missing a spawn location\n");
                }
                if (team.getUpperBedLocation() == null || team.getLowerBedLocation() == null) {
                    validationErrors.append("§c- Team " + (i + 1) + " is missing bed locations\n");
                }
                if (team.getRegion_1() == null || team.getRegion_2() == null) {
                    validationErrors.append("§c- Team " + (i + 1) + " is missing region points\n");
                }
                if (team.getSpawners() == null || team.getSpawners().isEmpty() ||
                        !team.getSpawners().stream().anyMatch(s -> s.getSpawnerType() == SpawnerType.TEAMSPAWNER)) {
                    validationErrors.append("§c- Team " + (i + 1) + " is missing a team spawner\n");
                }
            }
        }

        // Check spawners
        boolean hasIronSpawner = false;
        boolean hasGoldSpawner = false;
        boolean hasDiamondSpawner = false;

        if (gameMap.getSpawners() != null) {
            for (Spawner spawner : gameMap.getSpawners()) {
                if (spawner.getSpawnerType() == SpawnerType.IRON) hasIronSpawner = true;
                else if (spawner.getSpawnerType() == SpawnerType.GOLD) hasGoldSpawner = true;
                else if (spawner.getSpawnerType() == SpawnerType.DIAMOND) hasDiamondSpawner = true;
            }
        }

        if (!hasIronSpawner) validationErrors.append("§c- Missing Iron Spawner (Use §e/mapsetup setSpawner IRON§c)\n");
        if (!hasGoldSpawner) validationErrors.append("§c- Missing Gold Spawner (Use §e/mapsetup setSpawner GOLD§c)\n");
        if (!hasDiamondSpawner)
            validationErrors.append("§c- Missing Diamond Spawner (Use §e/mapsetup setSpawner DIAMOND§c)\n");

        // Check villagers
        boolean hasShop = false;
        boolean hasUpgrade = false;
        if (gameMap.getVillagers() != null) {
            for (Villager villager : gameMap.getVillagers()) {
                if (villager.getVillagerType() == VillagerType.SHOP) hasShop = true;
                if (villager.getVillagerType() == VillagerType.UPGRADE) hasUpgrade = true;
            }
        }

        if (!hasShop) validationErrors.append("§c- Missing Shop Villager (Use §e/mapsetup setVillager SHOP§c)\n");
        if (!hasUpgrade)
            validationErrors.append("§c- Missing Upgrade Manager Villager (Use §e/mapsetup setVillager UPGRADE§c)\n");

        // If there are validation errors, show them and don't finish
        if (validationErrors.length() > 0) {
            player.sendMessage("§cCannot finish map setup. The following requirements are missing:");
            player.sendMessage(validationErrors.toString());
            player.sendMessage("§7Please add the missing requirements and try again.");
            return true;
        }

        // Finish the map setup
        String result = mapSetup.getMapSetupManager().finishMapSetup(uuid);
        if (result.startsWith("There are some errors")) {
            player.sendMessage("§c" + result);
            player.sendMessage("§7Please fix the errors and try again.");
        } else {
            player.sendMessage("§aSuccessfully finished map setup!");
            player.sendMessage("§7Map Information:");
            player.sendMessage("§7- Name: §e" + gameMap.getName());
            player.sendMessage("§7- World: §e" + gameMap.getWorldName());
            player.sendMessage("§7- Teams: §e" + gameMap.getTeams().size() + " §7teams configured");
            player.sendMessage("§7- Spawners: §e" + (gameMap.getSpawners() != null ? gameMap.getSpawners().size() : 0) + " §7placed");
            player.sendMessage("§7- Villagers: §e" + (gameMap.getVillagers() != null ? gameMap.getVillagers().size() : 0) + " §7placed");

            // Add detailed team information
            player.sendMessage("\n§7Team Details:");
            for (int i = 0; i < gameMap.getTeams().size(); i++) {
                var team = gameMap.getTeams().get(i);
                player.sendMessage("§7Team " + (i + 1) + ":");
                player.sendMessage("§7  - Name: §e" + team.getName());
                player.sendMessage("§7  - Color: §e" + team.getColor().toString());
                player.sendMessage("§7  - Spawners: §e" + (team.getSpawners() != null ? team.getSpawners().size() : 0) + " §7placed");
            }

            player.sendMessage("\n§7The map has been saved and is ready to use!");
            player.sendMessage("§7You can now upload it to the database or start a new map setup.");
        }
        return true;
    }

    @Override
    public List<String> getSubCommandTabComplete(CommandSender sender, String[] args) {
        return List.of();
    }
}
