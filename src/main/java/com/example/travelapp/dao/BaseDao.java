package com.example.travelapp.dao;

import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.travelapp.config.DataSourceProvider;

public abstract class BaseDao {
	protected final Logger logger = LoggerFactory.getLogger(getClass());
	private final DataSource dataSource;

	protected BaseDao() {
		this.dataSource = DataSourceProvider.getDataSource();
		logCaller("BaseDao()");
	}

	protected Connection getConnection() throws SQLException {
		logCaller("getConnection()");
		return dataSource.getConnection();
	}

	private void logCaller(String method) {
		StackTraceElement[] stack = Thread.currentThread().getStackTrace();
		if (stack.length > 3) {
			StackTraceElement baseMethod = stack[2];
			StackTraceElement caller = stack[3];

			logger.info("{} called -> {}.{}(line {}) | caller: {}.{}(line {})",
			            method,
			            baseMethod.getClassName(), baseMethod.getMethodName(), baseMethod.getLineNumber(),
			            caller.getClassName(), caller.getMethodName(), caller.getLineNumber());
		}
	}

}
