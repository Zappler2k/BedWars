package de.zappler.bedwars.api.spigot.player;

import de.zappler.bedwars.api.spigot.player.inventory.GuiManager;
import de.zappler.bedwars.api.spigot.player.scoreboard.ScoreboardHandler;
import de.zappler.bedwars.api.spigot.player.scoreboard.TablistHandler;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.scoreboard.Scoreboard;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerHandler implements Listener {

    private Map<UUID, ScoreboardHandler> scoreboardHandlers;
    private Map<UUID, TablistHandler> tablistHandlers;
    private Map<UUID, Scoreboard> scoreboards;
    private Map<UUID, GuiManager> guiManagers;
    private Plugin plugin;

    public PlayerHandler(Plugin plugin) {
        this.scoreboardHandlers = new HashMap<>();
        this.tablistHandlers = new HashMap<>();
        this.scoreboards = new HashMap<>();
        this.guiManagers = new HashMap<>();
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    public void setAll(UUID uuid) {
        addPlayerToGuiManager(uuid, new GuiManager(Bukkit.getPlayer(uuid), plugin));
        addPlayerToScoreboardHandler(uuid, new ScoreboardHandler(Bukkit.getPlayer(uuid), this));
        addPlayerToTablistHandler(uuid, new TablistHandler(Bukkit.getPlayer(uuid), this));
    }

    public void removeAll(UUID uuid) {
        removePlayerFromGuiManager(uuid);
        removePlayerFromScoreboardHandler(uuid);
        removePlayerFromTablistHandler(uuid);
        removePlayerFromScoreboard(uuid);
    }

    public boolean isPlayerInGuiManager(UUID uuid) {
        return guiManagers.get(uuid) != null;
    }

    public GuiManager getPlayerInGuiManager(UUID uuid) {
        if (isPlayerInGuiManager(uuid)) {
            return guiManagers.get(uuid);
        }
        return null;
    }

    public void addPlayerToGuiManager(UUID uuid, GuiManager guiManager) {
        if (!isPlayerInGuiManager(uuid)) {
            guiManagers.put(uuid, guiManager);
        }
    }

    public void removePlayerFromGuiManager(UUID uuid) {
        if (isPlayerInGuiManager(uuid)) {
            guiManagers.remove(uuid);
        }
    }

    public boolean isPlayerInScoreboardHandler(UUID uuid) {
        return scoreboardHandlers.get(uuid) != null;
    }

    public ScoreboardHandler getPlayerInScoreboardHandler(UUID uuid) {
        if (isPlayerInScoreboardHandler(uuid)) {
            return scoreboardHandlers.get(uuid);
        }
        return null;
    }

    public void addPlayerToScoreboardHandler(UUID uuid, ScoreboardHandler scoreboardHandler) {
        if (!isPlayerInScoreboardHandler(uuid)) {
            scoreboardHandlers.put(uuid, scoreboardHandler);
        }
    }

    public void removePlayerFromScoreboardHandler(UUID uuid) {
        if (isPlayerInScoreboardHandler(uuid)) {
            scoreboards.remove(uuid);
            scoreboardHandlers.remove(uuid);
        }
    }

    public boolean isPlayerInTablistHandler(UUID uuid) {
        return tablistHandlers.get(uuid) != null;
    }

    public TablistHandler getPlayerInTablistHandler(UUID uuid) {
        if (isPlayerInTablistHandler(uuid)) {
            return tablistHandlers.get(uuid);
        }
        return null;
    }

    public void addPlayerToTablistHandler(UUID uuid, TablistHandler tablistHandler) {
        if (!isPlayerInTablistHandler(uuid)) {
            tablistHandlers.put(uuid, tablistHandler);
        }
    }

    public void removePlayerFromTablistHandler(UUID uuid) {
        if (isPlayerInTablistHandler(uuid)) {
            tablistHandlers.remove(uuid);
        }
    }

    public boolean isPlayerInScoreboard(UUID uuid) {
        return scoreboards.get(uuid) != null;
    }

    public Scoreboard getPlayerInScoreboard(UUID uuid) {
        if (isPlayerInScoreboard(uuid)) {
            return scoreboards.get(uuid);
        }
        return null;
    }

    public void addPlayerToScoreboard(UUID uuid, Scoreboard scoreboard) {
        if (!isPlayerInScoreboard(uuid)) {
            scoreboards.put(uuid, scoreboard);
        }
    }

    public void removePlayerFromScoreboard(UUID uuid) {
        if (isPlayerInScoreboard(uuid)) {
            scoreboards.remove(uuid);
        }
    }
}
