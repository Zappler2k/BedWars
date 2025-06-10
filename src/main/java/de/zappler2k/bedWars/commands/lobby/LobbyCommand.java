package de.zappler2k.bedWars.commands.lobby;

import de.zappler2k.bedWars.BedWars;
import de.zappler2k.bedWars.command.MainCommand;
import de.zappler2k.bedWars.commands.lobby.init.LobbyCheckSubCommand;
import de.zappler2k.bedWars.commands.lobby.init.LobbyFinishSubCommand;
import de.zappler2k.bedWars.commands.lobby.init.LobbySetLimitedStatsSubCommand;
import de.zappler2k.bedWars.commands.lobby.init.LobbySetLifetimeStatsSubCommand;
import de.zappler2k.bedWars.commands.lobby.init.LobbySetLobbySubCommand;
import de.zappler2k.bedWars.setup.map.LobbySetupManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class LobbyCommand extends MainCommand {

    private final BedWars plugin;
    private final LobbySetupManager lobbySetupManager;

    public LobbyCommand(BedWars plugin, LobbySetupManager lobbySetupManager) {
        super("bedwars.admin.setup.lobby");
        this.plugin = plugin;
        this.lobbySetupManager = lobbySetupManager;

        // Register subcommands
        registerSubCommand("setlobby", new LobbySetLobbySubCommand(plugin, lobbySetupManager));
        registerSubCommand("setlifetimestats", new LobbySetLifetimeStatsSubCommand(plugin, lobbySetupManager));
        registerSubCommand("setlimitedstats", new LobbySetLimitedStatsSubCommand(plugin, lobbySetupManager));
        registerSubCommand("check", new LobbyCheckSubCommand(plugin, lobbySetupManager));
        registerSubCommand("finish", new LobbyFinishSubCommand(plugin, lobbySetupManager));
    }

    @Override
    public boolean executeCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cThis command can only be executed by players!");
            return true;
        }

        if (args.length == 0) {
            sendHelp(player);
            return true;
        }

        String subCommand = args[0].toLowerCase();
        if (hasSubCommand(subCommand)) {
            return executeSubCommand(subCommand, sender, args);
        }

        sendHelp(player);
        return true;
    }

    @Override
    public List<String> getTabComplete(CommandSender sender, String[] args) {
        if (args.length == 1) {
            return getSubCommandNames();
        }
        return List.of();
    }

    private void sendHelp(Player player) {
        player.sendMessage("§8=== Lobby Setup Help ===");
        player.sendMessage("§e/lobbymanager setlobby §7- Set the lobby spawn location");
        player.sendMessage("§e/lobbymanager setlifetimestats §7- Set the lifetime stats board location");
        player.sendMessage("§e/lobbymanager setlimitedstats §7- Set the limited stats board location");
        player.sendMessage("§e/lobbymanager check §7- Check if all locations are set");
        player.sendMessage("§e/lobbymanager finish §7- Save the lobby configuration");
    }
}
