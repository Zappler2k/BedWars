package de.zappler2k.bedWars.commands.mapsetup.init;

import de.zappler2k.bedWars.command.SubCommand;
import de.zappler2k.bedWars.commands.mapsetup.MapSetup;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public class CloseMapCommand extends SubCommand {

    private MapSetup mapSetup;

    public CloseMapCommand(String permission, MapSetup mapSetup) {
        super(permission);
        this.mapSetup = mapSetup;
    }

    @Override
    public boolean executeSubCommand(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        UUID uuid = player.getUniqueId();

        if(mapSetup.getMapSetupManager().getGameMapSetup(uuid) == null) {
            player.sendMessage("You have to start a Setup first!");
            return true;
        }

        mapSetup.getMapSetupManager().getCurrentSetups().remove(uuid);
        mapSetup.getMapSetupManager().getCurrentStep().remove(uuid);
        player.sendMessage("You have successfully closed the Setup!");
        return true;
    }

    @Override
    public List<String> getSubCommandTabComplete(CommandSender sender, String[] args) {
        return List.of();
    }
}
