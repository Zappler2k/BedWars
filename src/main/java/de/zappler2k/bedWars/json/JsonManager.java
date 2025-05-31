package de.zappler2k.bedWars.json;

import lombok.SneakyThrows;

import java.io.FileWriter;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class JsonManager {

    private List<JsonModule> modules;
    private Logger logger;

    public JsonManager(Logger logger) {
        this.modules = new ArrayList<>();
        this.logger = logger;
    }

    @SneakyThrows
    public void generateDefaultConfig(JsonModule module) {
        if(module.getFile().getParentFile().exists()) {
            module.getFile().getParentFile().mkdirs();
        }
        if(!module.getFile().exists()) {
            FileWriter fileWriter = new FileWriter(module.getFile());
            fileWriter.write(module.getDefaultConfig());
            fileWriter.flush();
            fileWriter.close();
            module.getFile().createNewFile();
        }
    }

    public void loadConfigFromConfig(JsonModule module) {
        if(!module.getFile().exists())  {
            logger.log(Level.INFO, "There was a error by importing the file.");
            return;
        }
        modules.add(module.fromJson());
    }

    @SneakyThrows
    public void saveConfigToConfig(JsonModule module) {
        if(!module.getFile().exists())  {
            logger.log(Level.INFO, "There was a error by saveing the file.");
            return;
        }
        FileWriter fileWriter = new FileWriter(module.getFile());
        fileWriter.write(module.getDefaultConfig());
        fileWriter.flush();
        fileWriter.close();
    }
}
