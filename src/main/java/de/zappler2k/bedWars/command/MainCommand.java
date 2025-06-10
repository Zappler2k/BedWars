package de.zappler2k.bedWars.command;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.*;
import java.util.stream.Collectors;

@Getter
@Setter
public abstract class MainCommand implements CommandExecutor, TabCompleter {

    protected final String permission;
    protected final Map<String, SubCommand> subCommands = new HashMap<>();

    protected MainCommand(String permission) {
        this.permission = (permission != null && !permission.isEmpty()) ? permission : null;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!hasMainPermission(sender)) {
            sender.sendMessage("§cDu hast keine Berechtigung, um diesen Befehl auszuführen!");
            return true;
        }
        if (args.length > 0) {
            String subName = args[0].toLowerCase(Locale.ROOT);
            SubCommand sub = subCommands.get(subName);
            if (sub != null) {
                return sub.execute(sender, args);
            } else {
                sender.sendMessage("§cUnbekannter Unterbefehl: §7" + args[0]);
                sender.sendMessage("§7Verfügbare Unterbefehle: §e" + String.join(", ",
                        getAvailableSubCommandNames(sender)));
                return true;
            }
        }
        return executeCommand(sender, command, label, args);
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        // Permission-Prüfung
        if (permission != null && !permission.isEmpty() && !sender.hasPermission(permission)) {
            return Collections.emptyList();
        }

        if (args.length == 1) {
            String prefix = args[0].toLowerCase(Locale.ROOT);
            return subCommands.keySet().stream()
                    .filter(name -> {
                        SubCommand sc = subCommands.get(name);
                        return (sc.getPermission() == null || sender.hasPermission(sc.getPermission()));
                    })
                    .filter(name -> name.startsWith(prefix))
                    .collect(Collectors.toList());
        } else if (args.length > 1) {
            String subName = args[0].toLowerCase(Locale.ROOT);
            SubCommand sub = subCommands.get(subName);
            if (sub != null && (sub.getPermission() == null || sender.hasPermission(sub.getPermission()))) {
                // Hier wandeln wir ein mögliches null in eine leere Liste um:
                List<String> subResult = sub.getTabComplete(sender, args);
                return (subResult != null) ? subResult : Collections.emptyList();
            }
        }

        // Fallback: Haupt-Command-TabCompletion
        List<String> mainResult = getTabComplete(sender, args);
        return (mainResult != null) ? mainResult : Collections.emptyList();
    }

    public void registerSubCommand(String name, SubCommand subCommand) {
        if (name == null || name.isEmpty() || subCommand == null) {
            throw new IllegalArgumentException("Name und SubCommand dürfen nicht null oder leer sein!");
        }
        subCommands.put(name.toLowerCase(Locale.ROOT), subCommand);
    }

    private boolean hasMainPermission(CommandSender sender) {
        return (permission == null || permission.isEmpty()) || sender.hasPermission(permission);
    }

    private List<String> getAvailableSubCommandNames(CommandSender sender) {
        return subCommands.entrySet().stream()
                .filter(e -> e.getValue().hasPermission(sender))
                .map(Map.Entry::getKey)
                .sorted()
                .collect(Collectors.toList());
    }

    public abstract boolean executeCommand(CommandSender sender, Command command, String label, String[] args);

    public abstract List<String> getTabComplete(CommandSender sender, String[] args);

    protected boolean hasSubCommand(String name) {
        return subCommands.containsKey(name.toLowerCase());
    }

    protected boolean executeSubCommand(String name, CommandSender sender, String[] args) {
        SubCommand subCommand = subCommands.get(name.toLowerCase());
        if (subCommand != null) {
            return subCommand.executeSubCommand(sender, args);
        }
        return false;
    }

    protected List<String> getSubCommandNames() {
        return new ArrayList<>(subCommands.keySet());
    }
}