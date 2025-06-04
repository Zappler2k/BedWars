package de.zappler2k.bedWars.spigot.scoreboard;

import de.zappler2k.bedWars.spigot.SpigotPlayer;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.logging.Level;

public class ScoreboardManager {

    private Scoreboard scoreboard;
    private Objective objective;
    private SpigotPlayer spigotPlayer;

    public ScoreboardManager(SpigotPlayer spigotPlayer) {
        this.spigotPlayer = spigotPlayer;
        this.scoreboard = spigotPlayer.getScoreboard();
        this.objective = scoreboard.getObjective("dummy");
        if(objective == null) {
            this.objective = scoreboard.registerNewObjective("dummy", "scoreboard");
        }

        assert this.objective != null;
        this.objective.setDisplaySlot(DisplaySlot.SIDEBAR);
    }

    public void setDisplayName(Component displayName) {
        objective.displayName(displayName);
    }

    public void addLine(String score, int line) {
        objective.getScore(score).setScore(line);
    }

    public void addTeam(String name, String entry, Component prefix, Component suffix) {
        Team team = getTeam(name);
        if(team == null) {
            team = scoreboard.registerNewTeam(name);
        }
        team.addEntry(entry);
        team.prefix(prefix);
        team.suffix(suffix);
    }

    public Team getTeam(String name) {
        return scoreboard.getTeam(name);
    }

    public void updateTeam(String name, Component prefix, Component suffix) {
        Team team = getTeam(name);
        if(team == null) {
            Bukkit.getLogger().log(Level.INFO, "There was no team with the name " + name + "created");
            return;
        }
        team.prefix(prefix);
        team.suffix(suffix);
    }
    public void removeTeam(String name) {
        Team team = getTeam(name);
        if(team == null) {
            return;
        }
    }

    public void setScoreboard() {
        Player player = Bukkit.getPlayer(spigotPlayer.getUuid());
        assert player != null;
        player.setScoreboard(scoreboard);
    }

}
