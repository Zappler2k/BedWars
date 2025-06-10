package de.zappler2k.bedWars.commands.mapsetup.init;

import de.zappler2k.bedWars.command.SubCommand;
import de.zappler2k.bedWars.commands.mapsetup.MapSetup;
import de.zappler2k.bedWars.setup.map.MapSetupManager.SetupStep;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public class SetSpectatorSpawnCommand extends SubCommand {

    private MapSetup mapSetup;

    public SetSpectatorSpawnCommand(String permission, MapSetup mapSetup) {
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
        if (currentStep != SetupStep.SPECTATOR_SPAWN_CONFIGURATION) {
            player.sendMessage("§cYou can only set the spectator spawn in the Spectator Spawn Configuration step!");
            player.sendMessage("§eCurrent step: §c" + currentStep.getDisplayName());
            player.sendMessage(mapSetup.getMapSetupManager().getCurrentStepInfo(uuid));
            return true;
        }

        mapSetup.getMapSetupManager().setSpectatorLocation(uuid, player.getLocation());
        player.sendMessage("§aSuccessfully set the spectator spawn location!");
        player.sendMessage(mapSetup.getMapSetupManager().getCurrentStepInfo(uuid));
        return true;
    }

    @Override
    public List<String> getSubCommandTabComplete(CommandSender sender, String[] args) {
        return List.of();
    }
} 