package com.example.travelapp.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Set;

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
			try (InputStream in = DataSourceProvider.class.getClassLoader().getResourceAsStream("application.properties")) {
				if (in != null) {
					props.load(in);
				}
			} catch (IOException e) {
				LOGGER.warn("Could not load application.properties", e);
			}

			Properties hikariProps = new Properties();
			Set<String> names = props.stringPropertyNames();
			for (String name : names) {
				if (name.startsWith("db.")) {
					hikariProps.setProperty(name.substring(3), props.getProperty(name));
				}
			}

			HikariConfig config = new HikariConfig(hikariProps);
			dataSource = new HikariDataSource(config);
			LOGGER.info("Initialized HikariCP data source");
		}
		return dataSource;
	}
}
