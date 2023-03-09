package de.zappler.bedwars.module.map.impl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import de.zappler.bedwars.BedWars;
import de.zappler.bedwars.api.spigot.json.LocationAdapter;
import de.zappler.bedwars.api.storage.json.impl.IModule;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bukkit.Location;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Getter
@Setter
public class BwMap implements IModule {

    private transient BedWars bedWars;
    private String name;
    private Integer maxTeams;
    private Integer maxPlayersPerTeam;
    private Location spectatorLocation;
    private List<Location> bronzeSpawner;
    private List<Location> ironSpawner;
    private List<Location> goldSpawner;
    private List<Location> villagerSpawns;
    private List<BwTeam> bwTeams;

    public BwMap(Integer maxTeams, Integer maxPlayersPerTeam, Location spectatorLocation) {
        this.maxTeams = maxTeams;
        this.maxPlayersPerTeam = maxPlayersPerTeam;
        this.spectatorLocation = spectatorLocation;
        this.bronzeSpawner = new ArrayList<>();
        this.ironSpawner = new ArrayList<>();
        this.goldSpawner = new ArrayList<>();
        this.villagerSpawns = new ArrayList<>();
        this.bwTeams = new ArrayList<>();
    }

    @Override
    public File getFile() {
        return new File(bedWars.getDataFolder() + "//maps//", name + ".json");

    }

    @Override
    public String toJson() {
        return new GsonBuilder().setPrettyPrinting().registerTypeAdapter(Location.class, new LocationAdapter()).create().toJson(this);
    }

    @Override
    public IModule fromJson(String data) {
        return new Gson().fromJson(data, this.getClass());
    }

    @Override
    public String getDefaultConfig() {
        return new BwMap().toJson();
    }
}
