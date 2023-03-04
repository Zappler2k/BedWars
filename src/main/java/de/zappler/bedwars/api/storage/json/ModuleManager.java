package de.zappler.bedwars.api.storage.json;

import de.zappler.bedwars.api.storage.json.impl.IModule;
import lombok.SneakyThrows;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class ModuleManager {

    private List<IModule> iModules;

    public ModuleManager() {
        this.iModules = new ArrayList<>();
    }

    public IModule getIModule(Class<? extends IModule> iModule) {
        return iModules.stream().filter(iModules -> iModules.getClass().getName().equals(iModule.getName())).findAny().orElse(null);
    }

    public boolean isExistsIModule(IModule iModule) {
        return getIModule(iModule.getClass()) != null;
    }

    @SneakyThrows
    public void addModule(IModule iModule, boolean saveDefaultConfig) {
        if (!iModule.getFile().getParentFile().exists()) iModule.getFile().getParentFile().mkdirs();
        if (!iModule.getFile().exists()) {
            iModule.getFile().createNewFile();
            if (saveDefaultConfig) {
                FileWriter writer = new FileWriter(iModule.getFile());
                writer.write(iModule.getDefaultConfig());
                writer.flush();
            }
        }
        this.iModules.add(iModule.fromJson(getContent(iModule)));
    }

    public void removeIModule(IModule iModule) {
        if (isExistsIModule(iModule)) {
            iModules.remove(iModule);
            iModule.getFile().delete();
        }
    }

    @SneakyThrows
    public void insert(IModule iModule, String data) {
        FileWriter fileWriter = new FileWriter(iModule.getFile());
        fileWriter.write(data);
        fileWriter.flush();
        fileWriter.close();
    }

    @SneakyThrows
    public String getContent(IModule iModule) {
        return new String(Files.readAllBytes(iModule.getFile().toPath()));
    }

    @SneakyThrows
    public String getContent(File file) {
        return new String(Files.readAllBytes(file.toPath()));
    }

}
