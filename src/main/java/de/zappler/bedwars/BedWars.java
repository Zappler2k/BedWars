package de.zappler.bedwars;

import de.zappler.bedwars.api.storage.json.ModuleManager;
import de.zappler.bedwars.module.config.ConfigModule;
import de.zappler.bedwars.module.map.MapManager;
import de.zappler.bedwars.module.map.commands.MapSetupCommand;
import de.zappler.bedwars.module.map.commands.TeamSetupCommand;
import de.zappler.bedwars.module.map.setup.SetupManager;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public final class BedWars extends JavaPlugin {


    @Override
    public void onEnable() {
        ModuleManager moduleManager = new ModuleManager();
        // Configs
        ConfigModule configModule = new ConfigModule(this, moduleManager);

        // MapManager
        MapManager mapManager = new MapManager(this, moduleManager, configModule);
        mapManager.addAllMaps();
        mapManager.removeMapsWithInvalidPlayerAmount();

        // MapSetup
        SetupManager setupManager = new SetupManager(mapManager);

        // MySQL
        //  MySQLConnnector mySQLConnnector = new MySQLConnnector(moduleManager, this.getDataFolder().toString(), "mysql.json");

        // Commands
        getCommand("mapsetup").setExecutor(new MapSetupCommand(setupManager, configModule));
        getCommand("teamsetup").setExecutor(new TeamSetupCommand(setupManager, configModule, this));
    }

    @Override
    public void onDisable() {

    }
}
