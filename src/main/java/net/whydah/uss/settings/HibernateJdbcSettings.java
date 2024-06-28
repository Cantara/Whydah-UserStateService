package net.whydah.uss.settings;

import net.whydah.uss.MainApplication;

public interface HibernateJdbcSettings {

	String JDBC_URL = MainApplication.instance.config().get("jdbc.url");
	String JDBC_DRIVER = MainApplication.instance.config().get("jdbc.driver");
	String JDBC_USER = MainApplication.instance.config().get("jdbc.user");
	String JDBC_PASSWORD = MainApplication.instance.config().get("jdbc.password");

	
}
