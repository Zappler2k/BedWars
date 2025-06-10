package de.zappler2k.bedWars.hibernate.managers;

import de.zappler2k.bedWars.hibernate.entities.WorldEntity;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class WorldEntityManager {

    private final SessionFactory sessionFactory;

    public WorldEntityManager(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    /**
     * Speichert eine neue Welt oder aktualisiert eine bestehende
     * @param worldEntity Die zu speichernde Welt
     * @return true wenn erfolgreich, false bei Fehler
     */
    public boolean saveOrUpdate(WorldEntity worldEntity) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = null;
            try {
                transaction = session.beginTransaction();
                
                if (worldEntity.getCreatedAt() == null) {
                    worldEntity.setCreatedAt(LocalDateTime.now());
                }
                worldEntity.setLastModified(LocalDateTime.now());
                
                session.saveOrUpdate(worldEntity);
                transaction.commit();
                return true;
            } catch (Exception e) {
                if (transaction != null) {
                    transaction.rollback();
                }
                e.printStackTrace();
                return false;
            }
        }
    }

    /**
     * Findet eine Welt anhand des Mapnamens
     * @param mapName Der Name der zu findenden Welt
     * @return Optional mit der gefundenen Welt oder leer wenn nicht gefunden
     */
    public Optional<WorldEntity> findByMapName(String mapName) {
        try (Session session = sessionFactory.openSession()) {
            WorldEntity world = session.get(WorldEntity.class, mapName);
            return Optional.ofNullable(world);
        } catch (Exception e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    /**
     * Löscht eine Welt anhand des Mapnamens
     * @param mapName Der Name der zu löschenden Welt
     * @return true wenn erfolgreich gelöscht, false bei Fehler
     */
    public boolean deleteByMapName(String mapName) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = null;
            try {
                transaction = session.beginTransaction();
                
                Optional<WorldEntity> worldOpt = findByMapName(mapName);
                if (worldOpt.isPresent()) {
                    session.remove(worldOpt.get());
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

    /**
     * Gibt alle gespeicherten Welten zurück
     * @return Liste aller Welten
     */
    public List<WorldEntity> findAll() {
        try (Session session = sessionFactory.openSession()) {
            Query<WorldEntity> query = session.createQuery("FROM WorldEntity", WorldEntity.class);
            return query.list();
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }

    /**
     * Prüft ob eine Welt existiert
     * @param mapName Der Name der zu prüfenden Welt
     * @return true wenn die Welt existiert, false wenn nicht
     */
    public boolean exists(String mapName) {
        return findByMapName(mapName).isPresent();
    }

    /**
     * Aktualisiert nur die Weltdaten einer bestehenden Welt
     * @param mapName Der Name der zu aktualisierenden Welt
     * @param worldData Die neuen Weltdaten
     * @return true wenn erfolgreich aktualisiert, false bei Fehler
     */
    public boolean updateWorldData(String mapName, byte[] worldData) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = null;
            try {
                transaction = session.beginTransaction();
                
                Optional<WorldEntity> worldOpt = findByMapName(mapName);
                if (worldOpt.isPresent()) {
                    WorldEntity world = worldOpt.get();
                    world.setWorldData(worldData);
                    world.setLastModified(LocalDateTime.now());
                    session.update(world);
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
