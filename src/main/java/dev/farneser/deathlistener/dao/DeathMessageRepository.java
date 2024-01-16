package dev.farneser.deathlistener.dao;

import dev.farneser.deathlistener.models.DeathMessage;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.ArrayList;
import java.util.List;

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
            // TODO: handle exception
        }
    }

    public List<String> getPlayerDeaths(int pageSize, int page, String playerName, boolean isAdmin) {
        List<String> messages = new ArrayList<>();

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

            for (DeathMessage entity : results) {
                messages.add(entity.getPlayerName() + " | " + entity.getDeathMessage());
                messages.add(entity.getDeathTime() + " on: " + Math.round(entity.getDeathX()) + " " + Math.round(entity.getDeathY()) + " " + Math.round(entity.getDeathZ()));
            }

            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }

        return messages;
    }
}