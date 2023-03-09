package de.zappler.bedwars.module.map;

import de.zappler.bedwars.BedWars;
import de.zappler.bedwars.api.storage.json.ModuleManager;
import de.zappler.bedwars.module.config.ConfigModule;
import de.zappler.bedwars.module.map.impl.BwMap;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MapManager {

    private List<BwMap> bwMaps;
    private ModuleManager moduleManager;
    private BedWars bedWars;

    private ConfigModule configModule;

    public MapManager(BedWars bedWars, ModuleManager moduleManager, ConfigModule configModule) {
        this.bedWars = bedWars;
        this.bwMaps = new ArrayList<>();
        this.moduleManager = moduleManager;
        this.configModule = configModule;
    }

    public void addMap(BwMap bwMap) {
        moduleManager.addModule(bwMap, true);
        bwMaps.add(bwMap);
    }

    public void removeMap(BwMap bwMap) {
        moduleManager.removeIModule(bwMap);
        bwMaps.remove(bwMap);
    }

    public void removeMapWithoutConfig(BwMap bwMap) {
        bwMaps.remove(bwMap);
    }

    public void removeMapsWithInvalidPlayerAmount() {
        File maps = new File(bedWars.getDataFolder() + "//maps//");
        if (!maps.exists()) return;
        for (BwMap bwMap : bwMaps) {
            if (!bwMap.getMaxTeams().equals(configModule.getMaxTeams()) || !bwMap.getMaxPlayersPerTeam().equals(configModule.getMaxPlayersPerTeam())) {
                removeMapWithoutConfig(bwMap);
            }
        }
    }

    public void addAllMaps() {
        File maps = new File(bedWars.getDataFolder() + "//maps//");
        if (!maps.exists()) return;
        for (File file : maps.listFiles()) {
            bwMaps.add((BwMap) new BwMap().fromJson(moduleManager.getContent(file)));
        }
    }

}
