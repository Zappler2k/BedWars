package de.zappler2k.bedWars.map.objects;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class GameMap {

    private String name;
    private Integer maxTeams;
    private Integer maxPlayersPerTeam;
    private Integer minPlayersPerTeam;
    private String wordName;
    private List<Villager> villagers;
    private List<Team> teams;
    private List<Spawner> spawners;

    public GameMap() {
        this.teams = new ArrayList<>();
        this.spawners = new ArrayList<>();
    }
}
