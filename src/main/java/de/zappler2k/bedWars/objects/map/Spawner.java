package de.zappler2k.bedWars.objects.map;

import de.zappler2k.bedWars.objects.map.init.SpawnerType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;

@Getter
@Setter
@AllArgsConstructor
public class Spawner {

    private SpawnerType spawnerType;
    private Location location;
}
