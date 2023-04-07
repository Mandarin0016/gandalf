package com.mandarin.discord.config;

import io.github.cdimascio.dotenv.Dotenv;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class JdbcConnection {

    private static final Properties properties = new Properties();

    private JdbcConnection() {
    }

    public static Connection getConnection() throws SQLException {

        Dotenv config = Dotenv.configure().filename("configuration.env").load();

        String URL = config.get("DATABASE_URL");
        String user = config.get("DATABASE_USER");
        String password = config.get("DATABASE_PASSWORD");

        properties.setProperty("user", user);
        properties.setProperty("password", password);

        return DriverManager
                .getConnection(URL, properties);
    }
}