package dev.farneser.deathlistener;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@Slf4j
public class ConnectionFactory {
    private static Connection INSTANCE;

    public static void buildConnection(String dbPath) throws ClassNotFoundException {
        Class.forName("org.sqlite.JDBC");

        try {
            INSTANCE = DriverManager.getConnection("jdbc:sqlite:" + dbPath);

            String sql = getTablesSql();

            log.debug("SQL: {}", sql);

            if (!sql.isEmpty()) {
                log.debug("Creating tables");

                INSTANCE.prepareStatement(sql).execute();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private static String getTablesSql() {
        StringBuilder sql = new StringBuilder();

        try (InputStream in = ConnectionFactory.class.getResourceAsStream("/create_tables.sql")) {
            assert in != null;

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {

                while (reader.ready()) {
                    sql.append(reader.readLine());
                    sql.append("\n");
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return sql.toString();
    }

    public static Connection getInstance() {
        if (INSTANCE == null) {
            log.debug("Building connection");

            try {
                buildConnection("player_deaths.db");
            } catch (ClassNotFoundException e) {
                throw new RuntimeException("Sqlite Driver not found");
            }
        }

        return INSTANCE;
    }
}