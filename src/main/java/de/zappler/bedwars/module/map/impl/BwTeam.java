package de.zappler.bedwars.module.map.impl;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bukkit.ChatColor;
import org.bukkit.Location;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BwTeam {

    private String name;
    private Location spawnLocation;
    private Location bedUp;
    private Location beddown;
    private ChatColor color;
}
