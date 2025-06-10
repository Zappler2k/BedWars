package de.zappler2k.bedWars.commands.lobby.init;

import de.zappler2k.bedWars.BedWars;
import de.zappler2k.bedWars.command.SubCommand;
import de.zappler2k.bedWars.setup.map.LobbySetupManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class LobbySetLimitedStatsSubCommand extends SubCommand {

    private final BedWars plugin;
    private final LobbySetupManager lobbySetupManager;

    public LobbySetLimitedStatsSubCommand(BedWars plugin, LobbySetupManager lobbySetupManager) {
        super("bedwars.admin.setup.lobby.setlimitedstats");
        this.plugin = plugin;
        this.lobbySetupManager = lobbySetupManager;
    }

    @Override
    public boolean executeSubCommand(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cThis command can only be executed by players!");
            return true;
        }

        lobbySetupManager.setLimitedStatsBoardLocation(player, player.getLocation());
        player.sendMessage("§aLimited stats board location has been set!");
        return true;
    }

    @Override
    public List<String> getSubCommandTabComplete(CommandSender sender, String[] args) {
        return List.of();
    }
} 