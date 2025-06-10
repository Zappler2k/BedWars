package de.zappler2k.bedWars.setup.map;

import de.zappler2k.bedWars.BedWars;
import de.zappler2k.bedWars.game.configs.LobbyConfig;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class LobbySetupManager {

    private final BedWars plugin;
    private final Map<UUID, Location> lobbyLocations;
    private final Map<UUID, Location> lifetimeStatsBoardLocations;
    private final Map<UUID, Location> limitedStatsBoardLocations;

    public LobbySetupManager(BedWars plugin) {
        this.plugin = plugin;
        this.lobbyLocations = new HashMap<>();
        this.lifetimeStatsBoardLocations = new HashMap<>();
        this.limitedStatsBoardLocations = new HashMap<>();
    }

    public void setLobbyLocation(Player player, Location location) {
        lobbyLocations.put(player.getUniqueId(), location);
    }

    public void setLifetimeStatsBoardLocation(Player player, Location location) {
        lifetimeStatsBoardLocations.put(player.getUniqueId(), location);
    }

    public void setLimitedStatsBoardLocation(Player player, Location location) {
        limitedStatsBoardLocations.put(player.getUniqueId(), location);
    }

    public boolean isSetupComplete(UUID uuid) {
        return lobbyLocations.containsKey(uuid) &&
                lifetimeStatsBoardLocations.containsKey(uuid) &&
                limitedStatsBoardLocations.containsKey(uuid);
    }

    public Location getLobbyLocation(UUID uuid) {
        return lobbyLocations.get(uuid);
    }

    public Location getLifetimeStatsBoardLocation(UUID uuid) {
        return lifetimeStatsBoardLocations.get(uuid);
    }

    public Location getLimitedStatsBoardLocation(UUID uuid) {
        return limitedStatsBoardLocations.get(uuid);
    }

    public void removeSetup(UUID uuid) {
        lobbyLocations.remove(uuid);
        lifetimeStatsBoardLocations.remove(uuid);
        limitedStatsBoardLocations.remove(uuid);
    }
}
