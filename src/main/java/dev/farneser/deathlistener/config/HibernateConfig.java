package dev.farneser.deathlistener.config;

import dev.farneser.deathlistener.models.DeathMessage;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.AvailableSettings;

public class HibernateConfig {
    private static volatile SessionFactory SESSION_FACTORY;

    public static void buildSessionFactory(String databasePath) {
        if (SESSION_FACTORY == null) {
            synchronized (HibernateConfig.class) {
                if (SESSION_FACTORY == null) {
                    try {
                        StandardServiceRegistry standardRegistry = new StandardServiceRegistryBuilder()
                                .applySetting("hibernate.connection.url", "jdbc:sqlite:" + databasePath)
                                .applySetting("hibernate.connection.driver_class", "org.sqlite.JDBC")
                                .applySetting("hibernate.dialect", "org.hibernate.community.dialect.SQLiteDialect")
                                .applySetting(AvailableSettings.HBM2DDL_AUTO, "update")
                                .applySetting(AvailableSettings.SHOW_SQL, "false")
                                .applySetting(AvailableSettings.C3P0_MIN_SIZE, 5)
                                .applySetting(AvailableSettings.C3P0_MAX_SIZE, 20)
                                .applySetting(AvailableSettings.C3P0_TIMEOUT, 300)
                                .applySetting(AvailableSettings.C3P0_MAX_STATEMENTS, 50)
                                .applySetting(AvailableSettings.C3P0_IDLE_TEST_PERIOD, 3000)
                                .build();

                        MetadataSources metadataSources = new MetadataSources(standardRegistry);

                        metadataSources.addAnnotatedClass(DeathMessage.class);

                        SESSION_FACTORY = metadataSources.buildMetadata().buildSessionFactory();
                    } catch (Exception e) {
                        throw new ExceptionInInitializerError(e);
                    }
                }
            }
        }
    }

    public static SessionFactory getSessionFactory() {
        if (SESSION_FACTORY == null) {
            buildSessionFactory("player_deaths.db");
        }

        return SESSION_FACTORY;
    }

    public static void shutdown() {
        SESSION_FACTORY.close();
    }
}
