package de.zappler2k.bedWars.commands.teamsetup;

import de.zappler2k.bedWars.command.MainCommand;
import de.zappler2k.bedWars.commands.teamsetup.init.*;
import de.zappler2k.bedWars.setup.map.TeamSetupManager;
import lombok.Getter;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
public class TeamSetup extends MainCommand {

    private TeamSetupManager teamSetupManager;

    public TeamSetup(String permission, TeamSetupManager teamSetupManager) {
        super(permission);
        this.teamSetupManager = teamSetupManager;
        registerSubCommand("start", new StartTeamCommand("bedwars.admin.setup.team.start", this));
        registerSubCommand("setName", new SetTeamNameCommand("bedwars.admin.setup.team.setname", this));
        registerSubCommand("setColor", new SetTeamColorCommand("bedwars.admin.setup.team.setcolor", this));
        registerSubCommand("setSpawn", new SetTeamSpawnCommand("bedwars.admin.setup.team.setspawn", this));
        registerSubCommand("setBed", new SetTeamBedCommand("bedwars.admin.setup.team.setbed", this));
        registerSubCommand("setRegion", new SetTeamRegionCommand("bedwars.admin.setup.team.setregion", this));
        registerSubCommand("setSpawner", new SetTeamSpawnerCommand("bedwars.admin.setup.team.setspawner", this));
        registerSubCommand("finish", new FinishTeamCommand("bedwars.admin.setup.team.finish", this));
        registerSubCommand("close", new CloseTeamCommand("bedwars.admin.setup.team.close", this));
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
            if (teamSetupManager.hasActiveTeamSetup(uuid)) {
                player.sendMessage("§cYou already have an active team setup in progress!");
                player.sendMessage(teamSetupManager.getCurrentStepInfo(uuid));
                return true;
            }
            player.sendMessage("§cTo begin team setup, use §e/teamsetup start §cfirst!");
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
