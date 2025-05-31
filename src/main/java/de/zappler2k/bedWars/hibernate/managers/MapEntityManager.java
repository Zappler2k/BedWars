package de.zappler2k.bedWars.hibernate.managers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import de.zappler2k.bedWars.hibernate.entities.MapEntity;
import de.zappler2k.bedWars.json.init.LocationTypeAdapter;
import de.zappler2k.bedWars.map.objects.GameMap;
import org.bukkit.Location;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.ArrayList;
import java.util.List;

public class MapEntityManager {

    private SessionFactory sessionFactory;

    public MapEntityManager(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public void createGameMapSave(GameMap gameConfig) {
        Session session = sessionFactory.openSession();
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            MapEntity mapEntity = session.get(MapEntity.class, gameConfig.getName());
            if(mapEntity != null) {
                return;
            }
            mapEntity = new MapEntity(gameConfig.getName(), new GsonBuilder().registerTypeAdapter(Location.class, new LocationTypeAdapter()).create().toJson(gameConfig),
                    gameConfig.getMaxTeams() + "x" + gameConfig.getMaxPlayersPerTeam());
            session.save(mapEntity);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw e;
        } finally {
            session.close();
        }
    }

    public GameMap getGameMapSaveByName(String name) {
        Session session = sessionFactory.openSession();
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            MapEntity mapEntity = session.get(MapEntity.class, name);
            if(mapEntity == null) {
                return null;
            }
            transaction.commit();
            return new Gson().fromJson(mapEntity.getConfig(), GameMap.class);
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw e;
        } finally {
            session.close();
        }
    }

    public List<GameMap> getGameMapSaveByVariant(String variant) {
        Session session = sessionFactory.openSession();
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            Query<MapEntity> query = session.createQuery(
                    "FROM MapEntity WHERE variant = :variant", MapEntity.class);
            query.setParameter("variant", variant);
            List<MapEntity> mapEntities = query.getResultList();
            if(mapEntities == null || mapEntities.isEmpty()) {
                return new ArrayList<>();
            }
            transaction.commit();
            List<GameMap> gameMaps = new ArrayList<>();
            Gson gson = new Gson();
            for(MapEntity entity : mapEntities) {
                gameMaps.add(gson.fromJson(entity.getConfig(), GameMap.class));
            }
            return gameMaps;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw e;
        } finally {
            session.close();
        }
    }

    public void saveGameMap(GameMap gameMap) {
        Session session = sessionFactory.openSession();
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            MapEntity mapEntity = new MapEntity(gameMap.getName(), new Gson().toJson(gameMap),
                    gameMap.getMaxTeams() + "x" + gameMap.getMaxPlayersPerTeam());
            session.merge(mapEntity);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw e;
        } finally {
            session.close();
        }
    }
}

