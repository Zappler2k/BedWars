package de.zappler.bedwars.module.map.commands;

import de.zappler.bedwars.module.config.ConfigModule;
import de.zappler.bedwars.module.map.setup.MapSetup;
import de.zappler.bedwars.module.map.setup.SetupManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class MapSetupCommand implements CommandExecutor, TabExecutor {

    private SetupManager setupManager;
    private ConfigModule configModule;

    public MapSetupCommand(SetupManager setupManager, ConfigModule configModule) {
        this.setupManager = setupManager;
        this.configModule = configModule;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("bedwars.mapsetup")) {
            return true;
        }
        Player player = (Player) sender;
        if (args.length == 0 || args.length != 1 || args.length != 3) {
            if (setupManager.exitsMapSetup(player.getUniqueId().toString())) {
                setupManager.getMapSetup(player.getUniqueId().toString()).sendState();
                return true;
            } else {
                player.sendMessage(configModule.getPrefix() + "§cYou have to start the MapSetup first.");
                return true;
            }
        }
        if (args.length == 3) {
            if (setupManager.exitsMapSetup(player.getUniqueId().toString())) {
                setupManager.getMapSetup(player.getUniqueId().toString()).sendState();
                return true;
            }
            setupManager.addMapSetup(player.getUniqueId().toString(), new MapSetup(args[0], Integer.valueOf(args[1]), Integer.valueOf(args[2]), player, configModule));
        }
        if (args.length == 1) {
            if (!setupManager.exitsMapSetup(player.getUniqueId().toString())) {
                player.sendMessage(configModule.getPrefix() + "§cYou have to start the MapSetup first.");
                return true;
            }
            MapSetup mapSetup = setupManager.getMapSetup(player.getUniqueId().toString());
            if (args[0].equalsIgnoreCase("setSpectatorSpawn")) {
                mapSetup.setSpectatorSpawn(player.getLocation());
            } else if (args[0].equalsIgnoreCase("setBronzeSpawner")) {
                mapSetup.addBronze(player.getLocation());
            } else if (args[0].equalsIgnoreCase("setIronSpawner")) {
                mapSetup.addIron(player.getLocation());
            } else if (args[0].equalsIgnoreCase("setGoldSpawner")) {
                mapSetup.addGold(player.getLocation());
            } else if (args[0].equalsIgnoreCase("finish")) {
                setupManager.finishMapSetup(player.getUniqueId().toString());
            }
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> commands = new ArrayList<>();
        if (sender.hasPermission("bedwars.mapsetup")) {
            if (args.length == 1) {
                commands.add("<name>");
                commands.add("setSpectatorSpawn");
                commands.add("setBronzeSpawner");
                commands.add("setIronSpawner");
                commands.add("setGoldSpawner");
                commands.add("finish");
            } else if (args.length == 2) {
                commands.add("<maxTeams>");
            } else if (args.length == 3) {
                commands.add("<maxPlayerPerTeam>");
            }
        }
        return commands;
    }
}
