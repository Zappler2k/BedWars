package de.zappler2k.bedWars.command;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.command.CommandSender;

import java.util.*;
import java.util.stream.Collectors;

@Getter
@Setter
public abstract class SubCommand {

    protected String permission;
    protected final Map<String, SubCommand> subCommands = new HashMap<>();

    public SubCommand(String permission) {
        this.permission = permission;
    }

    public boolean execute(CommandSender sender, String[] args) {
        if (!hasPermission(sender)) {
            sender.sendMessage("§cDu hast keine Berechtigung für diesen Befehl.");
            return true;
        }

        if (args.length >= 1) {
            SubCommand nested = subCommands.get(args[0].toLowerCase());
            if (nested != null) {
                String[] newArgs = Arrays.copyOfRange(args, 1, args.length);
                return nested.execute(sender, newArgs);
            }
        }

        return executeSubCommand(sender, args);
    }

    public List<String> getTabComplete(CommandSender sender, String[] args) {
        if (!hasPermission(sender)) {
            return Collections.emptyList();
        }

        if (args.length == 1) {
            return subCommands.keySet().stream()
                    .filter(name -> {
                        SubCommand sc = subCommands.get(name);
                        return sc != null && sc.hasPermission(sender);
                    })
                    .filter(name -> name.toLowerCase().startsWith(args[0].toLowerCase()))
                    .collect(Collectors.toList());
        }

        if (args.length >= 2) {
            SubCommand nested = subCommands.get(args[0].toLowerCase());
            if (nested != null && nested.hasPermission(sender)) {
                String[] newArgs = Arrays.copyOfRange(args, 1, args.length);
                return nested.getTabComplete(sender, newArgs);
            }
        }

        return getSubCommandTabComplete(sender, args);
    }

    public boolean hasPermission(CommandSender sender) {
        return permission == null || permission.isEmpty() || sender.hasPermission(permission);
    }

    public void registerSubCommand(String name, SubCommand subCommand) {
        subCommands.put(name.toLowerCase(), subCommand);
    }

    public abstract boolean executeSubCommand(CommandSender sender, String[] args);

    public abstract List<String> getSubCommandTabComplete(CommandSender sender, String[] args);
}
