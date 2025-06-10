package de.zappler2k.bedWars.game.configs;

import lombok.Getter;
import org.bukkit.Location;

@Getter
public class LobbyConfig {

    private Location lobbyLocation;
    private Location lifetimeStatsBoardLocation;
    private Location limitedStatsBoardLocation;

    public LobbyConfig(Location lobbyLocation, Location lifetimeStatsBoardLocation, Location limitedStatsBoardLocation) {
        this.lobbyLocation = lobbyLocation;
        this.lifetimeStatsBoardLocation = lifetimeStatsBoardLocation;
        this.limitedStatsBoardLocation = limitedStatsBoardLocation;
    }
}
