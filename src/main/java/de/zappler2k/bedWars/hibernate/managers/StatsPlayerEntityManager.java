package de.zappler2k.bedWars.hibernate.managers;

import de.zappler2k.bedWars.hibernate.entities.StatsPlayerEntity;
import lombok.Getter;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.*;

@Getter
public class StatsPlayerEntityManager implements Listener {


    private Map<UUID, StatsPlayerEntity> statsPlayers;
    private SessionFactory sessionFactory;

    public StatsPlayerEntityManager(SessionFactory sessionFactory) {
        statsPlayers = new HashMap<>();
        StatsPlayerEntity statsPlayerEntity = new StatsPlayerEntity();
        this.sessionFactory = sessionFactory;
    }


    @EventHandler
    public void playerJoinListener(PlayerJoinEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();

        StatsPlayerEntity statsPlayerEntity = session.get(StatsPlayerEntity.class, uuid);

        if(statsPlayerEntity == null) {
            statsPlayerEntity = new StatsPlayerEntity();
            statsPlayerEntity.setUuid(uuid);
            statsPlayerEntity.setKills(0);
            statsPlayerEntity.setDeaths(0);
            statsPlayerEntity.setWins(0);
            statsPlayerEntity.setLosses(0);
            statsPlayerEntity.setPlayTime(0L);
            statsPlayerEntity.setBedDestroyed(0);
            statsPlayerEntity.setFinalKills(0);
            session.persist(statsPlayerEntity);
        }
        transaction.commit();
        session.close();

        if(!statsPlayers.containsKey(uuid)) {
            statsPlayers.put(uuid, statsPlayerEntity);
        } else {
        }
    }

    @EventHandler
    public void playerQuitListener(PlayerQuitEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();
        session.merge(statsPlayers.get(uuid));
        transaction.commit();
        session.close();
        statsPlayers.remove(uuid);
    }

}
