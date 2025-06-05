package de.zappler2k.bedWars.commands.teamsetup;

import de.zappler2k.bedWars.command.MainCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.List;

public class TeamSetup extends MainCommand {


    public TeamSetup(String permission) {
        super(permission);
    }

    @Override
    public boolean executeCommand(CommandSender sender, Command command, String label, String[] args) {
        return false;
    }

    @Override
    public List<String> getTabComplete(CommandSender sender, String[] args) {
        return List.of();
    }
}
