package de.zappler.bedwars.api.storage.mysql.impl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import de.zappler.bedwars.api.storage.json.ModuleManager;
import de.zappler.bedwars.api.storage.json.impl.IModule;
import lombok.Getter;

import java.io.File;

@Getter
public class MySQLModule implements IModule {

    private String HOST;
    private Integer PORT;
    private String DATABASE;
    private String USER;
    private String PASSWORD;
    private String file;
    private String dictionary;

    public MySQLModule(String dictionary, String file, ModuleManager moduleManager) {
        this.file = file;
        this.dictionary = dictionary;
        moduleManager.addModule(this, true);
    }

    public MySQLModule(String HOST, Integer PORT, String DATABASE, String USER, String PASSWORD) {
        this.HOST = HOST;
        this.PORT = PORT;
        this.DATABASE = DATABASE;
        this.USER = USER;
        this.PASSWORD = PASSWORD;
    }

    @Override
    public File getFile() {
        return new File(dictionary, file);
    }

    @Override
    public String toJson() {
        return new GsonBuilder().setPrettyPrinting().create().toJson(this);
    }

    @Override
    public IModule fromJson(String data) {
        return new Gson().fromJson(data, this.getClass());
    }

    @Override
    public String getDefaultConfig() {
        return new MySQLModule("localhost", 3306, "database", "root", "1234").toJson();
    }
}
