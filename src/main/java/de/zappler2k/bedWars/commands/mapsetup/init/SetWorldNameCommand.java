package de.zappler2k.bedWars.commands.mapsetup.init;

import de.zappler2k.bedWars.command.SubCommand;
import de.zappler2k.bedWars.commands.mapsetup.MapSetup;
import de.zappler2k.bedWars.setup.map.MapSetupManager.SetupStep;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SetWorldNameCommand extends SubCommand {

    private MapSetup mapSetup;

    public SetWorldNameCommand(String permission, MapSetup mapSetup) {
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
        if (currentStep != SetupStep.WORLD_NAME) {
            player.sendMessage("§cYou can only set the world name in the World Name step!");
            player.sendMessage("§eCurrent step: §c" + currentStep.getDisplayName());
            player.sendMessage(mapSetup.getMapSetupManager().getCurrentStepInfo(uuid));
            return true;
        }

        // Validate arguments
        if (args.length != 2) {
            player.sendMessage("§cUsage: §e/mapsetup setWorld <worldname>");
            player.sendMessage("§7Available worlds:");
            for (World world : Bukkit.getWorlds()) {
                player.sendMessage("§7- §e" + world.getName());
            }
            return true;
        }

        String worldName = args[1].trim();
        if (worldName.isEmpty()) {
            player.sendMessage("§cThe world name cannot be empty! Please provide a valid world name.");
            return true;
        }

        // Check if world exists
        World world = Bukkit.getWorld(worldName);
        if (world == null) {
            player.sendMessage("§cWorld '§e" + worldName + "§c' does not exist!");
            player.sendMessage("§7Available worlds:");
            for (World availableWorld : Bukkit.getWorlds()) {
                player.sendMessage("§7- §e" + availableWorld.getName());
            }
            return true;
        }

        // Set the world name
        mapSetup.getMapSetupManager().setWorldName(uuid, worldName);
        player.sendMessage("§aSuccessfully set the world name to: §e" + worldName);
        player.sendMessage(mapSetup.getMapSetupManager().getCurrentStepInfo(uuid));
        return true;
    }

    @Override
    public List<String> getSubCommandTabComplete(CommandSender sender, String[] args) {
        if (args.length == 2) {
            List<String> worldNames = new ArrayList<>();
            for (World world : Bukkit.getWorlds()) {
                worldNames.add(world.getName());
            }
            return worldNames;
        }
        return List.of("<worldname>");
    }
}
