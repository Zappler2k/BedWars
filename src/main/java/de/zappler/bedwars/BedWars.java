package de.zappler.bedwars;

import de.zappler.bedwars.api.storage.json.ModuleManager;
import de.zappler.bedwars.api.storage.mysql.MySQLConnnector;
import de.zappler.bedwars.module.config.ConfigModule;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public final class BedWars extends JavaPlugin {


    @Override
    public void onEnable() {
        ModuleManager moduleManager = new ModuleManager();
        MySQLConnnector mySQLConnnector = new MySQLConnnector(moduleManager, this.getDataFolder().toString(), "mysql.json");
        moduleRegestration(moduleManager);
    }

    @Override
    public void onDisable() {

    }

    private void moduleRegestration(ModuleManager moduleManager) {
        new ConfigModule(this, moduleManager);
    }
}
