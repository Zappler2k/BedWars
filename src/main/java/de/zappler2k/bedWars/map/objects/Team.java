package de.zappler2k.bedWars.map.objects;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Color;
import org.bukkit.Location;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class Team {

    private String name;
    private Color color;
    private Location spawnLoaction;
    private Location upperBedLocation;
    private Location downerBedLocation;
    private List<Spawner> spawners;
    private List<Villager> villagers;
    private Location region_1;
    private Location region_2;

}
