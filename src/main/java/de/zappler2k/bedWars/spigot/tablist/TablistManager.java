package de.zappler2k.bedWars.spigot.tablist;

import de.zappler2k.bedWars.spigot.SpigotPlayer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.awt.*;

public class TablistManager {

    private Scoreboard scoreboard;
    private SpigotPlayer spigotPlayer;

    public TablistManager(SpigotPlayer spigotPlayer) {
        this.scoreboard = spigotPlayer.getScoreboard();
        this.spigotPlayer = spigotPlayer;
    }

    public void addTeamToPlayer(Player player, NamedTextColor color, Component prefix, Component suffix) {
        Team team = scoreboard.registerNewTeam(player.getUniqueId() + spigotPlayer.getUuid().toString());
        team.addEntry(player.getName());
        team.color(color);
        team.prefix(prefix);
        team.suffix(suffix);
    }
    public void updateTeamFromPlayer(Player player, NamedTextColor color, Component prefix, Component suffix) {
        Team team = scoreboard.getTeam(player.getUniqueId() + spigotPlayer.getUuid().toString());
        if(team == null) {
            addTeamToPlayer(player, color, prefix, suffix);
            return;
        }
        team.color(color);
        team.prefix(prefix);
        team.suffix(suffix);
    }
}
