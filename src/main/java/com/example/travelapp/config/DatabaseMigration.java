package com.example.travelapp.config;

import javax.sql.DataSource;

import org.flywaydb.core.Flyway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DatabaseMigration {
    private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseMigration.class);

    public static void migrate() {
        DataSource dataSource = DataSourceProvider.getDataSource();
        Flyway flyway = Flyway.configure()
                .dataSource(dataSource)
                .locations("classpath:db/migration")
                .baselineOnMigrate(true)
                .baselineVersion("0")
                .load();
        LOGGER.info("Applying database migrations...");
        flyway.migrate();
        LOGGER.info("Database migration completed.");
    }
}