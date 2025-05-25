package de.zappler2k.bedWarrs;


import de.zappler2k.bedWarrs.yml.YamlManager;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;

import java.io.File;

public final class BedWars extends JavaPlugin {


    @Override
    public void onEnable() {

        // FILES
        File hibernateConfig = new File(this.getDataFolder() + "/hibernate.yml");

        YamlManager yamlManager = new YamlManager();
        yamlManager.addAndCopyFile("hibernate.yml", hibernateConfig);
        SessionFactory sessionFactory = loadHibernate(yamlManager.getConfig(hibernateConfig));
    }

    @Override
    public void onDisable() {

    }

    private SessionFactory loadHibernate(YamlConfiguration config) {
        try {
            Configuration configuration = new Configuration();
            configuration.setProperty("hibernate.connection.driver_class", config.getString("database.driver"));
            configuration.setProperty("hibernate.connection.url", config.getString("database.url"));
            configuration.setProperty("hibernate.connection.username", config.getString("database.username"));
            configuration.setProperty("hibernate.connection.password", config.getString("database.password"));
            configuration.setProperty("hibernate.dialect", config.getString("database.dialect"));
            configuration.setProperty("hibernate.show_sql", String.valueOf(config.getBoolean("database.show_sql", true)));
            configuration.setProperty("hibernate.hbm2ddl.auto", config.getString("database.hbm2ddl_auto", "update"));
            configuration.setProperty("hibernate.connection.pool_size", config.getString("database.pool_size", "10"));
            StandardServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
                    .applySettings(configuration.getProperties())
                    .build();
            return configuration.buildSessionFactory(serviceRegistry);
        } catch (Exception e) {
            throw new RuntimeException("Fehler beim Initialisieren von Hibernate", e);
        }
    }

}
