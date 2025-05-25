package de.zappler2k.bedWarrs.yml;

import lombok.SneakyThrows;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;

public class YamlManager {

    private Map<String, YamlConfiguration> configurations;

    public YamlManager() {
        this.configurations = new HashMap<>();
    }

    public YamlConfiguration getConfig(File file) {
        return configurations.get(file.getName());
    }

    public void addAndCopyFile(String resourceFile, File file) {
        copyFile(resourceFile, file);
        addFileToConfiguration(file);
    }

    @SneakyThrows
    private void addFileToConfiguration(File file) {
        configurations.put(file.getName(), YamlConfiguration.loadConfiguration(file));
    }

    @SneakyThrows
    private void copyFile(String resourceFile, File finalFile) {
        if(!finalFile.getParentFile().exists()) {
            finalFile.getParentFile().mkdirs();
        }
        if(!finalFile.exists()) {
            Files.copy(this.getClass().getResourceAsStream("/" + resourceFile), finalFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            finalFile.createNewFile();
        }
    }
}
