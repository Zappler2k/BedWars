package de.zappler.bedwars.module.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import de.zappler.bedwars.BedWars;
import de.zappler.bedwars.api.storage.json.ModuleManager;
import de.zappler.bedwars.api.storage.json.impl.IModule;
import lombok.Getter;

import java.io.File;
@Getter
public class ConfigModule implements IModule {

    private transient BedWars bedWars;
    private Integer maxPlayers;
    private Integer maxPlayersPerTeam;
    private String lobbyName;


    public ConfigModule(BedWars bedWars, ModuleManager moduleManager) {
        this.bedWars = bedWars;
        moduleManager.addModule(this, true);
    }

    public ConfigModule(Integer maxPlayers, Integer maxPlayersPerTeam, String lobbyName) {
        this.maxPlayers = maxPlayers;
        this.maxPlayersPerTeam = maxPlayersPerTeam;
        this.lobbyName = lobbyName;
    }


    @Override
    public File getFile() {
        return new File(bedWars.getDataFolder(), "config.json");
    }

    @Override
    public String toJson() {
        return  new GsonBuilder().setPrettyPrinting().create().toJson(this);
    }

    @Override
    public IModule fromJson(String data) {
        return new Gson().fromJson(data, this.getClass());
    }

    @Override
    public String getDefaultConfig() {
        return new ConfigModule(0, 0, "lobby").toJson();
    }
}
