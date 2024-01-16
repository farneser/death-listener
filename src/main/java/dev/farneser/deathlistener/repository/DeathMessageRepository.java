package dev.farneser.deathlistener.repository;

import dev.farneser.deathlistener.models.DeathMessage;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class DeathMessageRepository {

    private final SessionFactory sessionFactory;

    public DeathMessageRepository(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public void saveOrUpdate(DeathMessage entity) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            session.persist(entity);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            log.info("Error saving death message: " + e.getMessage());
        }
    }

    public List<DeathMessage> getPlayerDeaths(int pageSize, int page, String playerName, boolean isAdmin) {
        List<DeathMessage> messages = new ArrayList<>();

        Transaction transaction = null;

        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();

            String hql = "FROM DeathMessage WHERE playerName = :playerName OR :isAdmin = true ORDER BY id DESC";

            List<DeathMessage> results = session.createQuery(hql, DeathMessage.class)
                    .setParameter("playerName", playerName)
                    .setParameter("isAdmin", isAdmin)
                    .setMaxResults(pageSize)
                    .setFirstResult(pageSize * (page - 1))
                    .list();

            messages.addAll(results);

            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }

            log.info("Error getting player deaths: " + e.getMessage());
        }

        return messages;
    }
}