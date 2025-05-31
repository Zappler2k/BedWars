package de.zappler2k.bedWars.map.objects;

import de.zappler2k.bedWars.map.objects.init.VillagerType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;

@Getter
@Setter
@AllArgsConstructor
public class Villager {

    private VillagerType villagerType;
    private Location location;
}
