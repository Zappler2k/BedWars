package de.zappler.bedwars.module.map.setup;

import de.zappler.bedwars.module.map.MapManager;

import java.util.HashMap;

public class SetupManager {

    private HashMap<String, MapSetup> bwSetups;
    private MapManager mapManager;

    public SetupManager(MapManager mapManager) {
        this.bwSetups = new HashMap<>();
        this.mapManager = mapManager;
    }

    public MapSetup getMapSetup(String uuid) {
        return bwSetups.get(uuid);
    }

    public boolean exitsMapSetup(String uuid) {
        return getMapSetup(uuid) != null;
    }

    public void addMapSetup(String uuid, MapSetup mapSetup) {
        bwSetups.put(uuid, mapSetup);
    }

    public void finishMapSetup(String uuid) {
        if (getMapSetup(uuid).getState() == 2) {
            mapManager.addMap(getMapSetup(uuid).getBwMap());
            bwSetups.remove(uuid);
        }
    }
}
