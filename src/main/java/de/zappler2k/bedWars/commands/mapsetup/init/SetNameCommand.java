package de.zappler2k.bedWars.commands.mapsetup.init;

import de.zappler2k.bedWars.command.SubCommand;
import de.zappler2k.bedWars.commands.mapsetup.MapSetup;
import de.zappler2k.bedWars.setup.map.MapSetupManager.SetupStep;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class SetNameCommand extends SubCommand {

    private MapSetup mapSetup;

    public SetNameCommand(String permission, MapSetup mapSetup) {
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
        if (currentStep != SetupStep.MAP_NAME) {
            player.sendMessage("§cYou can only set the map name in the Map Name step!");
            player.sendMessage("§eCurrent step: §c" + currentStep.getDisplayName());
            player.sendMessage(mapSetup.getMapSetupManager().getCurrentStepInfo(uuid));
            return true;
        }

        // Validate arguments
        if (args.length != 2) {
            player.sendMessage("§cUsage: §e/mapsetup setName <name>");
            player.sendMessage("§7Example: §e/mapsetup setName Castle");
            return true;
        }

        String name = args[1].trim();
        if (name.isEmpty()) {
            player.sendMessage("§cThe map name cannot be empty! Please provide a valid name.");
            return true;
        }

        if (name.length() > 32) {
            player.sendMessage("§cThe map name cannot be longer than 32 characters!");
            return true;
        }

        // Set the name
        mapSetup.getMapSetupManager().setName(uuid, name);
        player.sendMessage("§aSuccessfully set the map name to: §e" + name);
        player.sendMessage(mapSetup.getMapSetupManager().getCurrentStepInfo(uuid));
        return true;
    }

    @Override
    public List<String> getSubCommandTabComplete(CommandSender sender, String[] args) {
        return List.of("<name>");
    }
}
