package de.zappler2k.bedWarrs;


import org.bukkit.plugin.java.JavaPlugin;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public final class BedWarrs extends JavaPlugin {


    @Override
    public void onEnable() {
        SessionFactory sessionFactory = loadHibernate();

    }

    @Override
    public void onDisable() {

    }

    private SessionFactory loadHibernate() {
        Configuration configuration = new Configuration();
        SessionFactory sessionFactory = configuration.buildSessionFactory();
        sessionFactory.getProperties()
        return sessionFactory;
    }
}
