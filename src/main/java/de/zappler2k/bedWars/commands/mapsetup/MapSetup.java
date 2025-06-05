package de.zappler2k.bedWars.commands.mapsetup;

import de.zappler2k.bedWars.command.MainCommand;
import de.zappler2k.bedWars.commands.mapsetup.init.SetNameCommand;
import de.zappler2k.bedWars.setup.map.MapSetupManager;
import lombok.Getter;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

@Getter
public class MapSetup extends MainCommand {

    private MapSetupManager mapSetupManager;

    public MapSetup(String permission, MapSetupManager mapSetupManager) {
        super(permission);
        this.mapSetupManager = mapSetupManager;
        registerSubCommand("setName", new SetNameCommand("bedwars.admin.setup.map.setname", this));
        registerSubCommand("close", new SetNameCommand("bedwars.admin.setup.map.close", this));
        registerSubCommand("finish", new SetNameCommand("bedwars.admin.setup.map.finish", this));
    }

    @Override
    public boolean executeCommand(CommandSender sender, Command command, String label, String[] args) {
        Player player = (Player) sender;
        UUID uuid = player.getUniqueId();

        switch (args.length) {
            case 0 -> {
                if(mapSetupManager.getGameMapSetup(uuid) != null) {
                    player.sendMessage("You already started a Map setup. \n" + mapSetupManager.getCurrentStepInfo(uuid));
                }
                mapSetupManager.addGameMapSetup(uuid);
                player.sendMessage(mapSetupManager.getCurrentStepInfo(uuid));
                return true;
            }
            default -> player.sendMessage("There is an error in the Game Setup!");
        }
        return false;
    }

    @Override
    public List<String> getTabComplete(CommandSender sender, String[] args) {
        return List.of();
    }
}
