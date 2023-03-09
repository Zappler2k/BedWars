package de.zappler.bedwars.module.map.commands;

import de.zappler.bedwars.module.config.ConfigModule;
import de.zappler.bedwars.module.map.setup.SetupManager;
import de.zappler.bedwars.module.map.setup.TeamSetup;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TeamSetupCommand implements CommandExecutor, TabExecutor {

    private TeamSetup teamSetup;
    private SetupManager setupManager;
    private ConfigModule configModule;
    private Plugin plugin;

    public TeamSetupCommand(SetupManager setupManager, ConfigModule configModule, Plugin plugin) {
        this.setupManager = setupManager;
        this.configModule = configModule;
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("bedwars.mapsetup.teamsetup")) {
            return true;
        }
        Player player = (Player) sender;
        if (args.length == 0 || args.length != 1) {
            if (setupManager.exitsMapSetup(player.getUniqueId().toString())) {
                setupManager.getMapSetup(player.getUniqueId().toString()).sendState();
            } else {
                player.sendMessage(configModule.getPrefix() + "§cYou have to start the MapSetup first. Then you can add the teams.");
                return true;
            }
        }
        if (args.length == 1) {
            if (teamSetup != null) {
                player.sendMessage(configModule.getPrefix() + "§7For the team, the setup has already started.");
                return true;
            }
            Arrays.stream(ChatColor.values()).filter(color -> {
                if (args[0].equalsIgnoreCase(color.toString())) {
                    teamSetup = new TeamSetup(ChatColor.valueOf(args[0]), player, setupManager.getMapSetup(player.getUniqueId().toString()), configModule, plugin);
                }
                return false;
            }).findAny().orElseThrow(() -> {
                        player.sendMessage(configModule.getPrefix() + "§7Please enter a valid color.");
                        return null;
                    }
            );


            if (args[0].equalsIgnoreCase("setSpawnLocation")) {
                if (teamSetup != null) {
                    player.sendMessage(configModule.getPrefix() + "§7For the team, the setup has already started.");
                    return true;
                }
                teamSetup.setSpawnLocation(player.getLocation());
            }
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> commands = new ArrayList<>();
        if (sender.hasPermission("bedwars.mapsetup.teamsetup")) {
            if (args.length == 1) {
                commands.add("setSpawnLocation");
                for (ChatColor colors : ChatColor.values()) {
                    commands.add(colors.name());
                }
            }
        }
        return commands;
    }
}
