package de.zappler.bedwars.api.spigot.player.scoreboard;


import de.zappler.bedwars.api.spigot.player.PlayerHandler;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.UUID;

public class ScoreboardHandler {

    private Scoreboard scoreboard;
    private Objective objective;
    private Player player;
    private UUID uuid;

    public ScoreboardHandler(Player player, PlayerHandler playerHandler) {
        this.player = player;
        this.uuid = player.getUniqueId();
        if (!playerHandler.isPlayerInScoreboard(uuid)) {
            playerHandler.addPlayerToScoreboard(uuid, Bukkit.getScoreboardManager().getNewScoreboard());
        }
        scoreboard = playerHandler.getPlayerInScoreboard(uuid);
        objective = scoreboard.registerNewObjective(player.getName(), "dummy");
    }

    public void setDisplayName(String displayName, DisplaySlot displaySlot) {
        objective.setDisplayName(displayName);
        objective.setDisplaySlot(displaySlot);
    }

    public void addScore(int socre, Object name) {
        objective.getScore(String.valueOf(name)).setScore(socre);
    }

    public void addTeam(String teamName, String entry, int score, Object prefix, Object suffix) {
        Team team = this.scoreboard.getTeam(teamName);
        if (team == null) {
            team = this.scoreboard.registerNewTeam(teamName);
        }
        team.addEntry(entry);
        team.setPrefix(String.valueOf(prefix));
        team.setSuffix(String.valueOf(suffix));
        addScore(score, entry);
    }

    public void updateTeam(String teamName, Object prefix, Object suffix) {
        if (scoreboard.getTeam(teamName) != null) {
            Team team = scoreboard.getTeam(teamName);
            assert team != null;
            team.setPrefix(String.valueOf(prefix));
            team.setSuffix(String.valueOf(suffix));
        }
    }

    public void setScoreboard() {
        player.setScoreboard(scoreboard);
    }
}