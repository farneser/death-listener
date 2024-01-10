package dev.farneser.deathlistener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionFactory {
    private static Connection INSTANCE;

    public static void buildConnection() throws ClassNotFoundException {
        Class.forName("org.sqlite.JDBC");

        try {
            INSTANCE = DriverManager.getConnection("jdbc:sqlite:player_deaths.db");

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

            if (sql.toString().isEmpty()) {
                INSTANCE.prepareStatement(sql.toString()).execute();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static Connection getInstance() {
        if (INSTANCE == null) {
            try {
                buildConnection();
            } catch (ClassNotFoundException e) {
                throw new RuntimeException("Sqlite Driver not found");
            }
        }

        return INSTANCE;
    }
}