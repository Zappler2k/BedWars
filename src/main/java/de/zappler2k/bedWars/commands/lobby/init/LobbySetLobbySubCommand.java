package de.zappler2k.bedWars.commands.lobby.init;

import de.zappler2k.bedWars.BedWars;
import de.zappler2k.bedWars.command.SubCommand;
import de.zappler2k.bedWars.setup.map.LobbySetupManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class LobbySetLobbySubCommand extends SubCommand {

    private final BedWars plugin;
    private final LobbySetupManager lobbySetupManager;

    public LobbySetLobbySubCommand(BedWars plugin, LobbySetupManager lobbySetupManager) {
        super("bedwars.admin.setup.lobby.setlobby");
        this.plugin = plugin;
        this.lobbySetupManager = lobbySetupManager;
    }

    @Override
    public boolean executeSubCommand(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cThis command can only be executed by players!");
            return true;
        }

        lobbySetupManager.setLobbyLocation(player, player.getLocation());
        player.sendMessage("§aLobby location has been set!");
        return true;
    }

    @Override
    public List<String> getSubCommandTabComplete(CommandSender sender, String[] args) {
        return List.of();
    }
} 