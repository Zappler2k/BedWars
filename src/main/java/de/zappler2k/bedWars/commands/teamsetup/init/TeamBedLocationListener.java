package de.zappler2k.bedWars.commands.teamsetup.init;

import de.zappler2k.bedWars.commands.teamsetup.TeamSetup;
import de.zappler2k.bedWars.setup.map.TeamSetupManager.TeamSetupStep;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class TeamBedLocationListener implements Listener {

    private final TeamSetup teamSetup;

    public TeamBedLocationListener(TeamSetup teamSetup) {
        this.teamSetup = teamSetup;
    }

    @EventHandler
    public void onBedLocationClick(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        
        // Check if it's a left click
        if (event.getAction() != Action.LEFT_CLICK_BLOCK) {
            return;
        }

        // Check if player has started a setup
        if (!teamSetup.getTeamSetupManager().hasActiveTeamSetup(player.getUniqueId())) {
            return;
        }

        // Check if we're in the correct step
        TeamSetupStep currentStep = teamSetup.getTeamSetupManager().getCurrentStep(
            teamSetup.getTeamSetupManager().getCurrentTeamSetup(player.getUniqueId())
        );
        
        if (currentStep != TeamSetupStep.UPPER_BED_LOCATION && currentStep != TeamSetupStep.LOWER_BED_LOCATION) {
            return;
        }

        event.setCancelled(true);

        // Set the bed position based on current step
        if (currentStep == TeamSetupStep.UPPER_BED_LOCATION) {
            teamSetup.getTeamSetupManager().setUpperBedLocation(player.getUniqueId(), event.getClickedBlock().getLocation());
            player.sendMessage("§aYou have successfully set the upper bed location!");
        } else {
            teamSetup.getTeamSetupManager().setLowerBedLocation(player.getUniqueId(), event.getClickedBlock().getLocation());
            player.sendMessage("§aYou have successfully set the lower bed location!");
        }
        
        player.sendMessage(teamSetup.getTeamSetupManager().getCurrentStepInfo(player.getUniqueId()));
    }
} 