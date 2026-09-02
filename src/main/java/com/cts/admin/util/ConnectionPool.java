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
			
			dataSource.setInitialPoolSize(3);
			dataSource.setMinPoolSize(3);
			dataSource.setAcquireIncrement(3);
			dataSource.setMaxPoolSize(20);
			dataSource.setCheckoutTimeout(15000);  // fail in 15s instead of hanging forever
			dataSource.setAcquireRetryAttempts(3);
			dataSource.setAcquireRetryDelay(1000);
			
		} catch (PropertyVetoException | IOException ex) {
			ex.printStackTrace();
		}
		
	}

	public static DataSource getDataSource() {
		return  dataSource;
	}
}
