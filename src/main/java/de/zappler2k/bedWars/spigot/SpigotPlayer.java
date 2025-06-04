package de.zappler2k.bedWars.spigot;

import de.zappler2k.bedWars.spigot.scoreboard.ScoreboardManager;
import de.zappler2k.bedWars.spigot.tablist.TablistManager;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.scoreboard.Scoreboard;

import java.util.UUID;

@Getter
@Setter
public class SpigotPlayer {

    private UUID uuid;
    private Scoreboard scoreboard;
    private ScoreboardManager scoreboardManager;
    private TablistManager tablistManager;

    public SpigotPlayer(UUID uuid, Scoreboard scoreboard) {
        this.uuid = uuid;
        this.scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        this.scoreboardManager = new ScoreboardManager(this);
        this.tablistManager = new TablistManager(this);
    }
}
