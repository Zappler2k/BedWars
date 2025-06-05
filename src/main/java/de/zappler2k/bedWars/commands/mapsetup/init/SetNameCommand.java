package de.zappler2k.bedWars.commands.mapsetup.init;

import de.zappler2k.bedWars.command.SubCommand;
import de.zappler2k.bedWars.commands.mapsetup.MapSetup;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class SetNameCommand extends SubCommand {

    private MapSetup mapSetup;

    public SetNameCommand(String permission, MapSetup mapSetup) {
        super(permission);
        this.mapSetup = mapSetup;
    }

    @Override
    public boolean executeSubCommand(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        UUID uuid = player.getUniqueId();
        String currentSateMessage = mapSetup.getMapSetupManager().getCurrentStepInfo(uuid);
        if(args.length == 2 && !Objects.equals(args[1], "") && !Objects.equals(args[1], " ")) {
            if(mapSetup.getMapSetupManager().getCurrentStep(mapSetup.getMapSetupManager().getGameMapSetup(uuid)).getOrder() != 1) {
                player.sendMessage(mapSetup.getMapSetupManager().getCurrentStepInfo(uuid));
                return true;
            }
            mapSetup.getMapSetupManager().getGameMapSetup(uuid).setName(args[1]);
            player.sendMessage("You have successfully set the Name for the Map. (§a" + args[1] + "§7)");
        }
        return false;
    }

    @Override
    public List<String> getSubCommandTabComplete(CommandSender sender, String[] args) {
        return List.of();
    }
}
