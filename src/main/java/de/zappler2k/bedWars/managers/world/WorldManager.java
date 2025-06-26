package de.zappler2k.bedWars.managers.world;

import de.zappler2k.bedWars.hibernate.entities.WorldEntity;
import de.zappler2k.bedWars.hibernate.managers.WorldEntityManager;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.hibernate.SessionFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class WorldManager {

    private final WorldEntityManager worldEntityManager;
    private final String serverPath = "/home/server";

    public WorldManager(SessionFactory sessionFactory) {
        this.worldEntityManager = new WorldEntityManager(sessionFactory);
    }

    /**
     * Prüft ob eine Welt existiert und gültig ist
     * @param worldName Der Name der zu prüfenden Welt
     * @return true wenn die Welt existiert und gültig ist, false wenn nicht
     */
    public boolean isWorldValid(String worldName) {
        File worldFolder = new File(serverPath, worldName);
        if (!worldFolder.exists() || !worldFolder.isDirectory()) {
            return false;
        }

        // Prüfe ob die wichtigsten Weltdateien existieren
        File levelDat = new File(worldFolder, "level.dat");
        File regionFolder = new File(worldFolder, "region");
        
        return levelDat.exists() && regionFolder.exists() && regionFolder.isDirectory();
    }

    public World loadWorld(String worldName) {
        World world = Bukkit.getWorld(worldName);
        if (world != null) {
            return world;
        }

        WorldCreator worldCreator = new WorldCreator(worldName);
        worldCreator.type(WorldType.NORMAL);
        worldCreator.generateStructures(true);
        world = worldCreator.createWorld();

        if (world != null) {
            return world;
        }
        return null;
    }

    public boolean unloadWorld(String worldName) {
        World world = Bukkit.getWorld(worldName);
        if (world == null) {
            return false;
        }
        return Bukkit.unloadWorld(world, true);
    }

    public boolean saveWorldToDatabase(String worldName) {
        if (!isWorldValid(worldName)) {
            return false;
        }

        try {
            // Erstelle temporäres ZIP-Archiv der Welt
            String tempZipPath = serverPath + "/temp_" + worldName + ".zip";
            File worldFolder = new File(serverPath, worldName);
            createWorldZip(worldFolder, tempZipPath);

            // Lese die ZIP-Datei als Byte-Array
            byte[] worldData = Files.readAllBytes(Paths.get(tempZipPath));

            // Speichere in der Datenbank
            WorldEntity worldEntity = new WorldEntity();
            worldEntity.setMapName(worldName);
            worldEntity.setWorldData(worldData);
            
            boolean success = worldEntityManager.saveOrUpdate(worldEntity);
            
            // Lösche temporäre ZIP-Datei
            Files.deleteIfExists(Paths.get(tempZipPath));
            return success;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean loadWorldFromDatabase(String worldName) {
        try {
            var worldOpt = worldEntityManager.findByMapName(worldName);
            if (worldOpt.isEmpty() || worldOpt.get().getWorldData() == null) {
                return false;
            }

            WorldEntity worldEntity = worldOpt.get();

            // Speichere temporäre ZIP-Datei
            String tempZipPath = serverPath + "/temp_" + worldName + ".zip";
            Files.write(Paths.get(tempZipPath), worldEntity.getWorldData());

            // Entpacke die Welt in das Server-Verzeichnis
            String worldPath = serverPath + "/" + worldName;
            unzipWorld(tempZipPath, worldPath);

            // Lösche temporäre ZIP-Datei
            Files.deleteIfExists(Paths.get(tempZipPath));

            // Lade die Welt
            return loadWorld(worldName) != null;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteWorldFromDatabase(String worldName) {
        return worldEntityManager.deleteByMapName(worldName);
    }

    public boolean updateWorldInDatabase(String worldName) {
        World world = Bukkit.getWorld(worldName);
        if (world == null) {
            return false;
        }

        try {
            // Erstelle temporäres ZIP-Archiv der Welt
            String tempZipPath = serverPath + "/temp_" + worldName + ".zip";
            File worldFolder = new File(serverPath, worldName);
            createWorldZip(worldFolder, tempZipPath);

            // Lese die ZIP-Datei als Byte-Array
            byte[] worldData = Files.readAllBytes(Paths.get(tempZipPath));

            // Aktualisiere in der Datenbank
            boolean success = worldEntityManager.updateWorldData(worldName, worldData);
            
            // Lösche temporäre ZIP-Datei
            Files.deleteIfExists(Paths.get(tempZipPath));
            return success;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private void createWorldZip(File worldFolder, String zipPath) throws IOException {
        try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zipPath))) {
            Path worldPath = worldFolder.toPath();
            Files.walk(worldPath)
                .filter(path -> !Files.isDirectory(path))
                .forEach(path -> {
                    try {
                        ZipEntry zipEntry = new ZipEntry(worldPath.relativize(path).toString());
                        zos.putNextEntry(zipEntry);
                        Files.copy(path, zos);
                        zos.closeEntry();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
        }
    }

    private void unzipWorld(String zipPath, String targetPath) throws IOException {
        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(zipPath))) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                File file = new File(targetPath, entry.getName());
                if (entry.isDirectory()) {
                    file.mkdirs();
                } else {
                    file.getParentFile().mkdirs();
                    try (FileOutputStream fos = new FileOutputStream(file)) {
                        byte[] buffer = new byte[1024];
                        int len;
                        while ((len = zis.read(buffer)) > 0) {
                            fos.write(buffer, 0, len);
                        }
                    }
                }
            }
        }
    }

    public String saveWorldToDatabaseWithValidation(String worldName) {
        if (!isWorldValid(worldName)) {
            return "§cError: World '§e" + worldName + "§c' does not exist or is not valid!";
        }

        try {
            boolean success = saveWorldToDatabase(worldName);
            if (success) {
                return "§aSuccessfully saved world '§e" + worldName + "§a' to the database!";
            } else {
                return "§cError: Failed to save world '§e" + worldName + "§c' to the database!";
            }
        } catch (Exception e) {
            return "§cError saving world to database: " + e.getMessage();
        }
    }
}
