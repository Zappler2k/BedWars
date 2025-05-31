package de.zappler2k.bedWars.setup.map;

import de.zappler2k.bedWars.map.objects.GameMap;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MapSetupManager {

    private Map<UUID, GameMap> currentSetups;

    public MapSetupManager() {
        this.currentSetups = new HashMap<>();
    }
}
