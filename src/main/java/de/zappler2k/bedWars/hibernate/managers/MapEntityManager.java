package de.zappler2k.bedWars.hibernate.managers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import de.zappler2k.bedWars.hibernate.entities.MapEntity;
import de.zappler2k.bedWars.json.init.LocationTypeAdapter;
import de.zappler2k.bedWars.objects.map.GameMap;
import org.bukkit.Location;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.ArrayList;
import java.util.List;

public class MapEntityManager {
    private final SessionFactory sessionFactory;
    private final Gson gson;

    public MapEntityManager(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
        this.gson = new GsonBuilder()
                .registerTypeAdapter(Location.class, new LocationTypeAdapter())
                .create();
    }

    public void createGameMapSave(GameMap gameMap) {
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            MapEntity entity = new MapEntity(
                    gameMap.getName(),
                    gson.toJson(gameMap),
                    gameMap.getMaxTeams() + "x" + gameMap.getMaxPlayersPerTeam()
            );
            session.save(entity);
            tx.commit();
        }
    }

    public GameMap getGameMapSaveByName(String name) {
        try (Session session = sessionFactory.openSession()) {
            MapEntity entity = session.get(MapEntity.class, name);
            return entity != null ? gson.fromJson(entity.getConfig(), GameMap.class) : null;
        }
    }

    public List<GameMap> getGameMapSaveByVariant(String variant) {
        try (Session session = sessionFactory.openSession()) {
            Query<MapEntity> query = session.createQuery(
                    "FROM MapEntity WHERE variant = :variant",
                    MapEntity.class
            );
            query.setParameter("variant", variant);

            List<GameMap> maps = new ArrayList<>();
            for (MapEntity entity : query.list()) {
                maps.add(gson.fromJson(entity.getConfig(), GameMap.class));
            }
            return maps;
        }
    }

    public List<String> getAllVariants() {
        try (Session session = sessionFactory.openSession()) {
            Query<String> query = session.createQuery(
                    "SELECT DISTINCT m.variant FROM MapEntity m WHERE m.variant IS NOT NULL",
                    String.class
            );
            return query.list();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public boolean deleteByMapName(String mapName) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = null;
            try {
                transaction = session.beginTransaction();
                
                MapEntity mapEntity = session.get(MapEntity.class, mapName);
                if (mapEntity != null) {
                    session.remove(mapEntity);
                    transaction.commit();
                    return true;
                }
                return false;
            } catch (Exception e) {
                if (transaction != null) {
                    transaction.rollback();
                }
                e.printStackTrace();
                return false;
            }
        }
    }
}

