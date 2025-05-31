package de.zappler2k.bedWars.command;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Getter
public class CommandManager {

    private final JavaPlugin plugin;
    private final CommandMap commandMap;
    private final Map<String, MainCommand> commands = new HashMap<>();

    public CommandManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.commandMap = fetchCommandMap();
    }

    public void registerCommand(String name, MainCommand command) {
        if (commandMap == null) {
            plugin.getLogger().warning("CommandMap konnte nicht gefunden werden. Befehls-Registrierung fehlgeschlagen.");
            return;
        }

        String lcName = name.toLowerCase();
        commands.put(lcName, command);

        WrappedCommand wrapped = new WrappedCommand(lcName, command);

        if (command.getPermission() != null && !command.getPermission().isEmpty()) {
            wrapped.setPermission(command.getPermission());
            wrapped.setPermissionMessage("§cDu hast keine Berechtigung, um diesen Befehl auszuführen!");
        }

        String namespace = plugin.getDescription().getName();
        commandMap.register(namespace, wrapped);

        plugin.getLogger().info("Befehl '/" + lcName + "' registriert.");
    }

    public String getCommandsOverview() {
        StringBuilder sb = new StringBuilder();
        for (String cmdName : commands.keySet()) {
            MainCommand mainCmd = commands.get(cmdName);
            sb.append("Command: ").append(cmdName).append("\n");
            String perm = mainCmd.getPermission();
            if (perm != null && !perm.isEmpty()) {
                sb.append("  Permission: ").append(perm).append("\n");
            } else {
                sb.append("  Permission: <keine>\n");
            }
            Map<String, SubCommand> subs = mainCmd.getSubCommands();
            if (subs != null && !subs.isEmpty()) {
                sb.append("  SubCommands:\n");
                for (String subName : subs.keySet()) {
                    SubCommand subCmd = subs.get(subName);
                    sb.append("    - ").append(subName);
                    String subPerm = subCmd.getPermission();
                    if (subPerm != null && !subPerm.isEmpty()) {
                        sb.append(": ").append(subPerm);
                    } else {
                        sb.append(": <keine>");
                    }
                    sb.append("\n");
                }
            } else {
                sb.append("  SubCommands: <keine>\n");
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    private CommandMap fetchCommandMap() {
        try {
            Field f = Bukkit.getServer().getClass().getDeclaredField("commandMap");
            f.setAccessible(true);
            return (CommandMap) f.get(Bukkit.getServer());
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    private static class WrappedCommand extends Command {
        private final MainCommand executor;

        protected WrappedCommand(String name, MainCommand executor) {
            super(name);
            this.executor = executor;
        }

        @Override
        public boolean execute(CommandSender sender, String alias, String[] args) {
            return executor.onCommand(sender, this, alias, args);
        }

        @Override
        public List<String> tabComplete(CommandSender sender, String alias, String[] args) {
            return executor.onTabComplete(sender, this, alias, args);
        }
    }
}
