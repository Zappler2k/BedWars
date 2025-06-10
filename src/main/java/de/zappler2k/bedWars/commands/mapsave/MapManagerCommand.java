package de.zappler2k.bedWars.commands.mapsave;

import de.zappler2k.bedWars.BedWars;
import de.zappler2k.bedWars.command.MainCommand;
import de.zappler2k.bedWars.command.SubCommand;
import de.zappler2k.bedWars.managers.MapManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MapManagerCommand extends MainCommand {
    private static final String PREFIX = "§8[§bMapManager§8] §7";
    private final MapManager mapManager;
    private final BedWars plugin;

    public MapManagerCommand(String permission, MapManager mapManager, BedWars plugin) {
        super(permission);
        this.mapManager = mapManager;
        this.plugin = plugin;
        registerSubCommand("list", new ListCommand("bedwars.admin.map-manager.list", mapManager));
        registerSubCommand("save", new SaveMapCommand("bedwars.admin.map-manager.save", mapManager));
        registerSubCommand("import", new ImportAndSaveMapCommand("bedwars.admin.map-manager.import", mapManager, plugin));
        registerSubCommand("importandsavebyvariant", new ImportAndSaveByVariantCommand("bedwars.admin.map-manager.importandsavebyvariant", mapManager, plugin));
    }

    @Override
    public boolean executeCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sendHelp(sender);
            return true;
        }
        return false;
    }

    private void sendHelp(CommandSender sender) {
        sender.sendMessage(PREFIX + "§7=== Map Manager Help ===");
        sender.sendMessage(PREFIX + "§7/list §8- §7List all loaded maps");
        sender.sendMessage(PREFIX + "§7/save <map> §8- §7Save a map to SQL");
        sender.sendMessage(PREFIX + "§7/import <map> §8- §7Import a map from SQL");
        sender.sendMessage(PREFIX + "§7/importvariant §8- §7Import all maps with current variant");
    }

    @Override
    public List<String> getTabComplete(CommandSender sender, String[] args) {
        if (args.length == 1) {
            String input = args[0].toLowerCase();
            return getSubCommands().keySet().stream()
                .filter(cmd -> cmd.toLowerCase().startsWith(input))
                .collect(Collectors.toList());
        }
        return new ArrayList<>();
    }

    private static class ListCommand extends SubCommand {
        private final MapManager mapManager;

        public ListCommand(String permission, MapManager mapManager) {
            super(permission);
            this.mapManager = mapManager;
        }

        @Override
        public boolean executeSubCommand(CommandSender sender, String[] args) {
            sender.sendMessage(mapManager.getConfigsInSave());
            return true;
        }

        @Override
        public List<String> getSubCommandTabComplete(CommandSender sender, String[] args) {
            return new ArrayList<>();
        }
    }

    private static class SaveMapCommand extends SubCommand {
        private final MapManager mapManager;

        public SaveMapCommand(String permission, MapManager mapManager) {
            super(permission);
            this.mapManager = mapManager;
        }

        @Override
        public boolean executeSubCommand(CommandSender sender, String[] args) {
            if (args.length < 2) {
                sender.sendMessage(PREFIX + "§cPlease specify a map name to save!");
                return true;
            }
            String mapName = args[1];
            sender.sendMessage(mapManager.saveConfigToSQL(mapName));
            return true;
        }

        @Override
        public List<String> getSubCommandTabComplete(CommandSender sender, String[] args) {
            if (args.length == 2) {
                String input = args[1].toLowerCase();
                return mapManager.getLoadedMaps().stream()
                    .map(map -> map.getName())
                    .filter(name -> name.toLowerCase().startsWith(input))
                    .collect(Collectors.toList());
            }
            return new ArrayList<>();
        }
    }

    private static class ImportAndSaveMapCommand extends SubCommand {
        private final MapManager mapManager;
        private final BedWars plugin;

        public ImportAndSaveMapCommand(String permission, MapManager mapManager, BedWars plugin) {
            super(permission);
            this.mapManager = mapManager;
            this.plugin = plugin;
        }

        @Override
        public boolean executeSubCommand(CommandSender sender, String[] args) {
            if (args.length < 2) {
                sender.sendMessage(PREFIX + "§cPlease specify a map name to import!");
                return true;
            }
            String mapName = args[1];
            sender.sendMessage(mapManager.importConfigFromSQL(mapName));
            return true;
        }

        @Override
        public List<String> getSubCommandTabComplete(CommandSender sender, String[] args) {
            if (args.length == 2) {
                String input = args[1].toLowerCase();
                return mapManager.getMapEntityManager().getGameMapSaveByVariant(plugin.getConfig().getString("variant"))
                    .stream()
                    .map(map -> map.getName())
                    .filter(name -> name.toLowerCase().startsWith(input))
                    .collect(Collectors.toList());
            }
            return new ArrayList<>();
        }
    }
    private static class ImportAndSaveByVariantCommand extends SubCommand {
        private final MapManager mapManager;
        private final BedWars plugin;

        public ImportAndSaveByVariantCommand(String permission, MapManager mapManager, BedWars plugin) {
            super(permission);
            this.mapManager = mapManager;
            this.plugin = plugin;
        }

        @Override
        public boolean executeSubCommand(CommandSender sender, String[] args) {
            String variant = args.length > 1 ? args[1] : plugin.getConfig().getString("variant");
            if (variant == null || variant.isEmpty()) {
                sender.sendMessage(PREFIX + "§cNo variant specified in config.yml!");
                return true;
            }
            sender.sendMessage(mapManager.importAndSaveConfigsByVariantFromSQL(variant));
            return true;
        }

        @Override
        public List<String> getSubCommandTabComplete(CommandSender sender, String[] args) {
            if (args.length == 2) {
                String input = args[1].toLowerCase();
                return mapManager.getMapEntityManager().getAllVariants()
                    .stream()
                    .filter(variant -> variant.toLowerCase().startsWith(input))
                    .collect(Collectors.toList());
            }
            return new ArrayList<>();
        }
    }
}
