package de.zappler2k.bedWars.spigot;

import de.zappler2k.bedWars.spigot.scoreboard.ScoreboardManager;
import de.zappler2k.bedWars.spigot.tablist.TablistManager;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.*;

@Getter
public class SpigotManager {

    private List<SpigotPlayer> spigotPlayers;

    public SpigotManager() {
        this.spigotPlayers = new ArrayList<>();
    }

    public SpigotPlayer getSpigotPlayer(UUID uuid) {
        return spigotPlayers.stream().filter(spigotPlayer -> spigotPlayer.getUuid().equals(uuid)).findAny().orElse(null);
    }

    public void addScoreboardToPlayer(UUID uuid) {
        if(getSpigotPlayer(uuid) != null) {
            return;
        }
        spigotPlayers.add(new SpigotPlayer(uuid, Bukkit.getScoreboardManager().getNewScoreboard()));
    }

    public void removeScoreboardFromPlayer(UUID uuid) {
        if(getSpigotPlayer(uuid) == null) {
            return;
        }
        spigotPlayers.remove(getSpigotPlayer(uuid));
    }

    public void addTeamToAllPlayers(Player player, NamedTextColor color, Component prefix, Component suffix) {
        spigotPlayers.stream().forEach(spigotPlayer -> {
            spigotPlayer.getTablistManager().addTeamToPlayer(player, color, prefix, suffix);
        });
    }

    public void updateTeamToAllPlayers(Player player, NamedTextColor color, Component prefix, Component suffix) {
        spigotPlayers.stream().forEach(spigotPlayer -> {
            spigotPlayer.getTablistManager().updateTeamFromPlayer(player, color, prefix, suffix);
        });
    }

    public void playerJoinEvent(PlayerJoinEvent event) {
        addScoreboardToPlayer(event.getPlayer().getUniqueId());
    }
    public void playerQuitEvent(PlayerQuitEvent event) {
        removeScoreboardFromPlayer(event.getPlayer().getUniqueId());
    }
}
