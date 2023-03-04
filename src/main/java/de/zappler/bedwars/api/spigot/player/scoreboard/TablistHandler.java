package de.zappler.bedwars.api.spigot.player.scoreboard;


import de.zappler.bedwars.api.spigot.player.PlayerHandler;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.UUID;

public class TablistHandler {

    private Player player;
    private UUID uuid;
    private Scoreboard scoreboard;

    public TablistHandler(Player player, PlayerHandler playerHandler) {
        this.player = player;
        this.uuid = player.getUniqueId();
        if (!playerHandler.isPlayerInScoreboard(uuid)) {
            playerHandler.addPlayerToScoreboard(uuid, Bukkit.getScoreboardManager().getNewScoreboard());
        }
        scoreboard = playerHandler.getPlayerInScoreboard(uuid);
    }

    public void addTeam(String teamName, ChatColor color, String prefix, String suffix) {
        Team team = scoreboard.getTeam(teamName) == null ? scoreboard.registerNewTeam(teamName) : scoreboard.getTeam(teamName);
        team.setPrefix(prefix);
        team.setSuffix(suffix);
        team.setColor(color);
    }

    public Team getTeam(String teamName) {
        return scoreboard.getTeam(teamName);
    }
}
