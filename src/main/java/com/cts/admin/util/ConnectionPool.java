package com.cts.admin.util;

import java.beans.PropertyVetoException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.sql.DataSource;

import com.mchange.v2.c3p0.ComboPooledDataSource;

public class ConnectionPool {
	
	static ComboPooledDataSource dataSource;
	
	static {
		
		try {
			
			dataSource = new ComboPooledDataSource();
			Properties properties = new Properties();
			InputStream inputStream = ConnectionPool.class.getClassLoader().getResourceAsStream("db.properties");
			properties.load(inputStream);
			
			dataSource.setDriverClass(properties.getProperty("DRIVER_CLASS"));
			dataSource.setJdbcUrl(properties.getProperty("CONNECTION_STRING"));
			dataSource.setUser(properties.getProperty("USER_NAME"));
			dataSource.setPassword(properties.getProperty("PASSWORD"));
			
			dataSource.setInitialPoolSize(10);
			dataSource.setMinPoolSize(10);
			dataSource.setAcquireIncrement(10);
			dataSource.setMaxPoolSize(40);
			
		} catch (IOException | PropertyVetoException ex) {
			ex.printStackTrace();
		}
		
	}

	public static DataSource getDataSource() {
		return  dataSource;
	}
}
