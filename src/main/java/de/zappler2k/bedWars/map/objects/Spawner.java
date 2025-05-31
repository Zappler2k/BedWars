package de.zappler2k.bedWars.map.objects;

import de.zappler2k.bedWars.map.objects.init.SpawnerType;
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
