package de.zappler2k.bedWars.commands.teamsetup.init;

import de.zappler2k.bedWars.command.SubCommand;
import de.zappler2k.bedWars.commands.teamsetup.TeamSetup;
import de.zappler2k.bedWars.setup.map.TeamSetupManager.TeamSetupStep;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class SetTeamColorCommand extends SubCommand {

    private TeamSetup teamSetup;

    public SetTeamColorCommand(String permission, TeamSetup teamSetup) {
        super(permission);
        this.teamSetup = teamSetup;
    }

    @Override
    public boolean executeSubCommand(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cThis command can only be executed by players in-game!");
            return true;
        }

        Player player = (Player) sender;
        UUID uuid = player.getUniqueId();

        // Check if player has started a setup
        if (!teamSetup.getTeamSetupManager().hasActiveTeamSetup(uuid)) {
            player.sendMessage("§cYou need to start a team setup first! Use §e/teamsetup start §cto begin.");
            return true;
        }

        // Check if we're in the correct step
        TeamSetupStep currentStep = teamSetup.getTeamSetupManager().getCurrentStep(teamSetup.getTeamSetupManager().getCurrentTeamSetup(uuid));
        if (currentStep != TeamSetupStep.TEAM_COLOR) {
            player.sendMessage("§cYou can only set the team color in the Team Color step!");
            player.sendMessage("§7Current step: §e" + teamSetup.getTeamSetupManager().getCurrentStepInfo(uuid));
            return true;
        }

        // Validate arguments
        if (args.length != 2) {
            player.sendMessage("§cUsage: §e/teamsetup setColor <color>");
            player.sendMessage("§7Example: §e/teamsetup setColor RED");
            player.sendMessage("§7Available colors: §e" + getAvailableColors());
            return true;
        }

        String colorName = args[1].toLowerCase();
        Color color = parseColor(colorName);
        if (color == null) {
            player.sendMessage("§cInvalid color! Available colors: §e" + getAvailableColors());
            return true;
        }

        // Check if color is already used by another team
        if (isColorAlreadyUsed(uuid, color)) {
            player.sendMessage("§cThis color is already used by another team!");
            player.sendMessage("§7Please choose a different color.");
            return true;
        }

        // Set the team color
        teamSetup.getTeamSetupManager().setTeamColor(uuid, color);
        player.sendMessage("§aSuccessfully set team color to §e" + colorName.toUpperCase() + "§a!");
        player.sendMessage("§7Current step: §e" + teamSetup.getTeamSetupManager().getCurrentStepInfo(uuid));
        return true;
    }

    private boolean isColorAlreadyUsed(UUID uuid, Color color) {
        var currentMap = teamSetup.getTeamSetupManager().getMapSetupManager().getGameMapSetup(uuid);
        if (currentMap != null && currentMap.getTeams() != null) {
            return currentMap.getTeams().stream()
                    .anyMatch(team -> team.getColor() != null && team.getColor().equals(color));
        }
        return false;
    }

    private Color parseColor(String colorName) {
        try {
            ChatColor chatColor = ChatColor.valueOf(colorName.toUpperCase());
            return switch (chatColor) {
                case BLACK -> Color.BLACK;
                case DARK_BLUE -> Color.fromRGB(0, 0, 170);
                case DARK_GREEN -> Color.fromRGB(0, 170, 0);
                case DARK_AQUA -> Color.fromRGB(0, 170, 170);
                case DARK_RED -> Color.fromRGB(170, 0, 0);
                case DARK_PURPLE -> Color.fromRGB(170, 0, 170);
                case GOLD -> Color.fromRGB(255, 170, 0);
                case GRAY -> Color.fromRGB(170, 170, 170);
                case DARK_GRAY -> Color.fromRGB(85, 85, 85);
                case BLUE -> Color.fromRGB(85, 85, 255);
                case GREEN -> Color.fromRGB(85, 255, 85);
                case AQUA -> Color.fromRGB(85, 255, 255);
                case RED -> Color.fromRGB(255, 85, 85);
                case LIGHT_PURPLE -> Color.fromRGB(255, 85, 255);
                case YELLOW -> Color.fromRGB(255, 255, 85);
                case WHITE -> Color.WHITE;
                default -> null;
            };
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private String getAvailableColors() {
        return Arrays.stream(ChatColor.values())
                .filter(color -> color != ChatColor.RESET && color != ChatColor.BOLD && 
                        color != ChatColor.ITALIC && color != ChatColor.UNDERLINE && 
                        color != ChatColor.STRIKETHROUGH && color != ChatColor.MAGIC)
                .map(Enum::name)
                .collect(Collectors.joining(", "));
    }

    @Override
    public List<String> getSubCommandTabComplete(CommandSender sender, String[] args) {
        if (args.length == 2) {
            return Arrays.stream(ChatColor.values())
                    .filter(color -> color != ChatColor.RESET && color != ChatColor.BOLD && 
                            color != ChatColor.ITALIC && color != ChatColor.UNDERLINE && 
                            color != ChatColor.STRIKETHROUGH && color != ChatColor.MAGIC)
                    .map(Enum::name)
                    .map(String::toLowerCase)
                    .filter(color -> color.startsWith(args[1].toLowerCase()))
                    .collect(Collectors.toList());
        }
        return List.of();
    }
} 