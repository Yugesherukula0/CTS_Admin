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
			
			dataSource.setInitialPoolSize(0);  // don't connect at startup — open lazily on first use
			dataSource.setMinPoolSize(0);       // allow pool to shrink to zero when idle
			dataSource.setMaxPoolSize(10);
			dataSource.setAcquireIncrement(1);  // grow one connection at a time
			dataSource.setCheckoutTimeout(20000);   // 20s timeout if no connection available
			dataSource.setAcquireRetryAttempts(2);  // retry twice before giving up
			dataSource.setAcquireRetryDelay(500);   // 500ms between retries
			
		} catch (PropertyVetoException | IOException ex) {
			ex.printStackTrace();
		}
		
	}

	public static DataSource getDataSource() {
		return  dataSource;
	}
}
