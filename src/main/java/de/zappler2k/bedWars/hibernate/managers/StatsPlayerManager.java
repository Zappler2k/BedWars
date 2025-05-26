package de.zappler2k.bedWars.hibernate.managers;

import de.zappler2k.bedWars.hibernate.entities.StatsPlayer;
import jakarta.persistence.EntityManagerFactory;
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
public class StatsPlayerManager implements Listener {


    private Map<UUID, StatsPlayer> statsPlayers;
    private SessionFactory sessionFactory;

    public StatsPlayerManager(SessionFactory sessionFactory) {
        statsPlayers = new HashMap<>();
        StatsPlayer statsPlayer = new StatsPlayer();
        this.sessionFactory = sessionFactory;
    }


    @EventHandler
    public void playerJoinListener(PlayerJoinEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();

        StatsPlayer statsPlayer = session.get(StatsPlayer.class, uuid);

        if(statsPlayer == null) {
            statsPlayer = new StatsPlayer();
            statsPlayer.setUuid(uuid);
            statsPlayer.setKills(0);
            statsPlayer.setDeaths(0);
            statsPlayer.setWins(0);
            statsPlayer.setLosses(0);
            statsPlayer.setPlayTime(0L);
            statsPlayer.setBedDestroyed(0);
            statsPlayer.setFinalKills(0);
            session.persist(statsPlayer);
        }
        transaction.commit();
        session.close();

        if(!statsPlayers.containsKey(uuid)) {
            statsPlayers.put(uuid, statsPlayer);
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
