package de.zappler2k.bedWars.map.objects;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GameMap {

    private String name;
    private int maxTeams;
    private int maxPlayersPerTeam;
    private String wordName;
    private List<Team> team;
    private List<Spawner> spawners;
}
