package de.zappler.bedwars.api.spigot.util;

import org.bukkit.Location;

public class LocationUtil {

    public static boolean isIn(Location loc, Location locA, Location locB) {
        double maxX = Math.max(locA.getX(), locB.getX());
        double minX = Math.min(locA.getX(), locB.getX());
        double maxY = Math.max(locA.getY(), locB.getY());
        double minY = Math.min(locA.getY(), locB.getY());
        double maxZ = Math.max(locA.getZ(), locB.getZ());
        double minZ = Math.min(locA.getZ(), locB.getZ());
        if (loc.getX() <= maxX && loc.getX() >= minX && loc.getY() <= maxY && loc.getY() >= minY && loc.getZ() <= maxZ && loc.getZ() >= minZ) {
            return true;
        }
        return false;
    }
}
