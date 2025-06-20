package de.zappler2k.bedWars;


import de.zappler2k.bedWars.command.CommandManager;
import de.zappler2k.bedWars.commands.mapsave.MapManagerCommand;
import de.zappler2k.bedWars.commands.mapsetup.MapSetup;
import de.zappler2k.bedWars.commands.teamsetup.TeamSetup;
import de.zappler2k.bedWars.commands.teamsetup.init.TeamBedLocationListener;
import de.zappler2k.bedWars.hibernate.managers.MapEntityManager;
import de.zappler2k.bedWars.hibernate.managers.StatsPlayerEntityManager;
import de.zappler2k.bedWars.json.JsonManager;
import de.zappler2k.bedWars.managers.MapManager;
import de.zappler2k.bedWars.map.objects.GameMap;
import de.zappler2k.bedWars.setup.map.MapSetupManager;
import de.zappler2k.bedWars.setup.map.TeamSetupManager;
import de.zappler2k.bedWars.spigot.SpigotManager;
import de.zappler2k.bedWars.spigot.SpigotPlayer;
import de.zappler2k.bedWars.yml.YamlManager;
import jakarta.persistence.Entity;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.checkerframework.checker.units.qual.C;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.reflections.Reflections;

import java.awt.*;
import java.io.File;
import java.util.Random;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class BedWars extends JavaPlugin {


    @Override
    public void onEnable() {
        Logger logger = this.getLogger();

        // FILES

        File hibernateConfig = new File(this.getDataFolder() + "/hibernate.yml");
        File config = new File(this.getDataFolder() + "/config.yml");
        // General

        YamlManager yamlManager = new YamlManager();
        yamlManager.addAndCopyFile("hibernate.yml", hibernateConfig);
        yamlManager.addAndCopyFile("config.yml", config);
        // YamlConfigurations

        YamlConfiguration yamConfigurationConfig = yamlManager.getConfig(config);
        // SQL

        SessionFactory sessionFactory = loadHibernate(yamlManager.getConfig(hibernateConfig), logger);

        // Managers

        // Hibernate

        StatsPlayerEntityManager statsPlayerEntityManager = new StatsPlayerEntityManager(sessionFactory);
        MapEntityManager mapEntityManager = new MapEntityManager(sessionFactory);
        // General


        MapManager mapManager = new MapManager(this, mapEntityManager, yamConfigurationConfig);
        mapManager.loadAllConfigs();
        //

        // Registration of Listeners and Commands
        // Listeners

        // Commands
        CommandManager commandManager = new CommandManager(this);


        if (yamConfigurationConfig.getBoolean("setupMode")) {
            // Managers
            MapSetupManager mapSetupManager = new MapSetupManager(this);
            TeamSetupManager teamSetupManager = new TeamSetupManager(this, mapSetupManager);

            // Commands
            TeamSetup teamSetup = new TeamSetup("bedwars.admin.setup.team", teamSetupManager);
            commandManager.registerCommand("mapsetup", new MapSetup("bedwars.admin.setup.map", mapSetupManager));
            commandManager.registerCommand("teamsetup", teamSetup);
            commandManager.registerCommand("mapmanager", new MapManagerCommand("bedwars.admin.manager.map", mapManager, this));

            // Register event listeners for setup mode
            getServer().getPluginManager().registerEvents(new TeamBedLocationListener(teamSetup), this);
            return;
        }

        logger.log(Level.INFO, mapManager.importConfigsByVariantFromSQL());
        SpigotManager spigotManager = new SpigotManager();

        // JSONManager

        JsonManager jsonManager = new JsonManager(logger);

    }

    @Override
    public void onDisable() {

    }

    private SessionFactory loadHibernate(YamlConfiguration config, Logger logger) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Configuration configuration = new Configuration();
            configuration.setProperty("hibernate.connection.driver_class", config.getString("database.driver"));
            configuration.setProperty("hibernate.connection.url", config.getString("database.url"));
            configuration.setProperty("hibernate.connection.username", config.getString("database.username"));
            configuration.setProperty("hibernate.connection.password", config.getString("database.password"));
            configuration.setProperty("hibernate.dialect", "org.hibernate.dialect.MySQL8Dialect");
            configuration.setProperty("hibernate.show_sql",
                    String.valueOf(config.getBoolean("database.show_sql", true)));
            configuration.setProperty("hibernate.hbm2ddl.auto",
                    config.getString("database.hbm2ddl_auto", "update"));
            configuration.setProperty("hibernate.connection.pool_size",
                    config.getString("database.pool_size", "10"));
            Reflections reflections = new Reflections("de.zappler2k.bedWars.hibernate.entities");
            reflections.getTypesAnnotatedWith(Entity.class)
                    .forEach(configuration::addAnnotatedClass);
            StandardServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
                    .applySettings(configuration.getProperties())
                    .build();
            return configuration.buildSessionFactory(serviceRegistry);

        } catch (ClassNotFoundException cnfe) {
            logger.log(Level.SEVERE, "JDBC-Treiber nicht gefunden!", cnfe);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Fehler beim Initialisieren von Hibernate", e);
        }
        return null;

    }
}