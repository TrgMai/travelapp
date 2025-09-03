package com.example.travelapp.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class DataSourceProvider {
    private static final Logger LOGGER = LoggerFactory.getLogger(DataSourceProvider.class);
    private static HikariDataSource dataSource;

    private DataSourceProvider() {
    }

    public static synchronized DataSource getDataSource() {
        if (dataSource == null) {
            Properties props = new Properties();
            try (InputStream in = DataSourceProvider.class.getClassLoader()
                    .getResourceAsStream("application.properties")) {
                if (in != null) {
                    props.load(in);
                }
            } catch (IOException e) {
                LOGGER.warn("Could not load application.properties", e);
            }

            String url = props.getProperty("db.url", "jdbc:postgresql://localhost:5432/postgres");
            String user = props.getProperty("db.user", "postgres");
            String password = props.getProperty("db.password", "admin");
            int poolSize = Integer.parseInt(props.getProperty("db.pool.size", "10"));

            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(url);
            config.setUsername(user);
            config.setPassword(password);
            config.setMaximumPoolSize(poolSize);
            config.setMinimumIdle(1);
            config.setPoolName("TravelAppHikariPool");
            dataSource = new HikariDataSource(config);
            LOGGER.info("Initialized HikariCP data source");
        }
        return dataSource;
    }
}