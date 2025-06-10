package de.zappler2k.bedWars.commands.mapsetup;

import de.zappler2k.bedWars.command.MainCommand;
import de.zappler2k.bedWars.commands.mapsetup.init.*;
import de.zappler2k.bedWars.setup.map.MapSetupManager;
import lombok.Getter;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
public class MapSetup extends MainCommand {

    private MapSetupManager mapSetupManager;

    public MapSetup(String permission, MapSetupManager mapSetupManager) {
        super(permission);
        this.mapSetupManager = mapSetupManager;
        registerSubCommand("setName", new SetNameCommand("bedwars.admin.setup.map.setname", this));
        registerSubCommand("close", new CloseMapCommand("bedwars.admin.setup.map.close", this));
        registerSubCommand("finish", new FinishMapCommand("bedwars.admin.setup.map.finish", this));
        registerSubCommand("setWorld", new SetWorldNameCommand("bedwars.admin.setup.map.world", this));
        registerSubCommand("setMaxTeams", new SetMaxTeamsCommand("bedwars.admin.setup.map.maxteams", this));
        registerSubCommand("setMinPlayersPerTeam", new SetMinPlayerPerTeamCommand("bedwars.admin.setup.map.minplayersperteam", this));
        registerSubCommand("setMaxPlayersPerTeam", new SetMaxPlayerPerTeamCommand("bedwars.admin.setup.map.maxplayersperteam", this));
        registerSubCommand("setSpawner", new SetSpawnerCommand("bedwars.admin.setup.map.setspawner", this));
        registerSubCommand("setVillager", new SetVillagerCommand("bedwars.admin.setup.map.setvillager", this));
        registerSubCommand("setSpectatorSpawn", new SetSpectatorSpawnCommand("bedwars.admin.setup.map.setspectatorspawn", this));
    }

    @Override
    public boolean executeCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cThis command can only be executed by players in-game!");
            return true;
        }

        Player player = (Player) sender;
        UUID uuid = player.getUniqueId();

        if (args.length == 0) {
            if (mapSetupManager.getGameMapSetup(uuid) != null) {
                player.sendMessage("§cYou already have an active map setup in progress.");
                player.sendMessage(mapSetupManager.getCurrentStepInfo(uuid));
                return true;
            }
            mapSetupManager.addGameMapSetup(uuid);
            player.sendMessage("§aMap setup process started! Follow the steps to configure your map.");
            player.sendMessage(mapSetupManager.getCurrentStepInfo(uuid));
            return true;
        }

        return false;
    }

    @Override
    public List<String> getTabComplete(CommandSender sender, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            completions.addAll(getSubCommands().keySet());
        } else if (args.length == 2) {
            String subCommand = args[0].toLowerCase();
            if (getSubCommands().containsKey(subCommand)) {
                completions.addAll(getSubCommands().get(subCommand).getSubCommandTabComplete(sender, args));
            }
        }

        return completions;
    }
}
