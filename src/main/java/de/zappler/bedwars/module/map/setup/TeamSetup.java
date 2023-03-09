package de.zappler.bedwars.module.map.setup;

import de.zappler.bedwars.module.config.ConfigModule;
import de.zappler.bedwars.module.map.impl.BwTeam;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.plugin.Plugin;

public class TeamSetup implements Listener {

    private BwTeam bwTeam;
    private Integer state;
    private MapSetup mapSetup;
    private Player player;
    private ConfigModule configModule;

    public TeamSetup(ChatColor color, Player player, MapSetup mapSetup, ConfigModule configModule, Plugin plugin) {
        if (mapSetup.getRegisteredTeams().equals(mapSetup.getBwMap().getMaxTeams())) {
            player.sendMessage(configModule.getPrefix() + "§cAll teams have already been created.");
            player.sendMessage(configModule.getPrefix() + "§7Add all the spawners and villagers and finish the setup with /mapsetup finish.");
            return;
        }
        if (state == 0) {
            sendState();
            return;
        }
        this.bwTeam = new BwTeam();
        bwTeam.setName(mapSetup.getRegisteredTeams() + 1 + "");
        bwTeam.setColor(color);
        this.state = 0;
        this.mapSetup = mapSetup;
        this.player = player;
        this.configModule = configModule;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    public void setSpawnLocation(Location location) {
        if (state != null) {
            sendState();
            return;
        }
        bwTeam.setSpawnLocation(location);
        state = 1;
        sendState();
    }

    @EventHandler
    public void BlockBreak(BlockBreakEvent event) {
        if (state == 1) {
            bwTeam.setBeddown(event.getBlock().getLocation());
            state = 2;
            sendState();
        } else if (state == 2) {
            bwTeam.setBedUp(event.getBlock().getLocation());
            state = 3;
            sendState();
        }
    }

    public void sendState() {
        if (state == 0) {
            player.sendMessage(configModule.getPrefix() + "§7You have started the TemaSetup for team §e" + bwTeam.getName() + " §7numbers with the " + bwTeam.getColor() + "color.");
            player.sendMessage(configModule.getPrefix() + "§7Now please set the SpawnLocarion.");
        } else if (state == 1) {
            player.sendMessage(configModule.getPrefix() + "§7You have set the SpawnLocarion.");
            player.sendMessage(configModule.getPrefix() + "§7Now hit the bottom side of the bed.");
        } else if (state == 2) {
            player.sendMessage(configModule.getPrefix() + "§7You have set the bottom side of the bed.");
            player.sendMessage(configModule.getPrefix() + "§7Now hit the upper side of the bed.");
        } else if (state == 3) {
            player.sendMessage(configModule.getPrefix() + "§7You have set the upper side of the bed.");
            player.sendMessage(configModule.getPrefix() + "§7The TeamSetup for the team is finished.");
            HandlerList.unregisterAll(this);
            mapSetup.addTeam(bwTeam);
            if (mapSetup.getRegisteredTeams().equals(mapSetup.getBwMap().getMaxTeams())) {
                player.sendMessage(configModule.getPrefix() + "§aAll teams have been created.");
                player.sendMessage(configModule.getPrefix() + "§7Add all the spawners and villagers and finish the setup with /mapsetup finish.");
                mapSetup.setState(2);
            } else {
                player.sendMessage(configModule.getPrefix() + "§7You still need to create" + (configModule.getMaxTeams() - mapSetup.getRegisteredTeams()) + " teams.");
            }
        }
    }
}
