package de.zappler.bedwars.module.map.setup;

import de.zappler.bedwars.module.config.ConfigModule;
import de.zappler.bedwars.module.map.impl.BwMap;
import de.zappler.bedwars.module.map.impl.BwTeam;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.entity.Player;

@Getter

public class MapSetup {

    private BwMap bwMap;
    @Setter
    private Integer state;
    private Integer registeredTeams;
    private Player player;
    private ConfigModule configModule;

    public MapSetup(String name, Integer maxTeams, Integer maxPlayersPerTeam, Player player, ConfigModule configModule) {
        if (state == 0) {
            sendState();
            return;
        }
        this.bwMap = new BwMap();
        this.player = player;
        this.configModule = configModule;
        bwMap.setName(name);
        bwMap.setMaxTeams(maxTeams);
        bwMap.setMaxPlayersPerTeam(maxPlayersPerTeam);
        this.state = 0;
        this.registeredTeams = 0;
        sendState();
    }

    public void setSpectatorSpawn(Location location) {
        if (state != 0) {
            player.sendMessage(configModule.getPrefix() + "§7First you have to start the setup /mapsetup.");
            return;
        }
        bwMap.setSpectatorLocation(location);
        state = 1;
        sendState();
    }

    public void addVillager(Location location) {
        if (state != 2) {
            sendState();
            return;
        }
        bwMap.getVillagerSpawns().add(location);
        player.sendMessage("§7You have added a Villager.");
    }

    public void addBronze(Location location) {
        if (state != 2) {
            sendState();
            return;
        }
        bwMap.getBronzeSpawner().add(location);
        player.sendMessage("§7You have added a Bronze-Spawner.");
    }

    public void addIron(Location location) {
        if (state != 2) {
            sendState();
            return;
        }
        bwMap.getIronSpawner().add(location);
        player.sendMessage("§7You have added a Iron-Spawner.");
    }

    public void addGold(Location location) {
        if (state != 2) {
            sendState();
            return;
        }
        bwMap.getGoldSpawner().add(location);
        player.sendMessage("§7You have added a Gold-Spawner.");
    }

    public void addTeam(BwTeam bwTeam) {
        bwMap.getBwTeams().add(bwTeam);
    }

    public void sendState() {
        if (state == 0) {
            player.sendMessage(configModule.getPrefix() + "§7Das MapSetup für die Map §e" + bwMap.getName() + "§7 (MaxTeams §8▪ §e" + bwMap.getMaxTeams() + ", §7MaxPlayersPerTeam §8▪ §e" + bwMap.getMaxPlayersPerTeam() + "§7) wurde gestartet.");
            player.sendMessage("§7Now please set the SpectatorLocation.");
        } else if (state == 1) {
            player.sendMessage(configModule.getPrefix() + "§7You have set the SpectatorLocation.");
            player.sendMessage(configModule.getPrefix() + "§7Now you need to create all the teams.");
        }
    }
}
