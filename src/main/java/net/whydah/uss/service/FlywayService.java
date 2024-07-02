package net.whydah.uss.service;

import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.whydah.uss.settings.AppSettings;
import net.whydah.uss.util.DatabaseMigrationHelper;
import no.cantara.config.ApplicationProperties;


public class FlywayService {
	
	public static final Logger logger = LoggerFactory.getLogger(FlywayService.class);
	
	private boolean isReady = false;
	private boolean hasError = false;
	
	public FlywayService(ApplicationProperties appConfigs) {
		try {
			
    		String JDBC_URL = AppSettings.JDBC_URL;
    		String JDBC_USER = AppSettings.JDBC_USER;
    		String JDBC_PASSWORD = AppSettings.JDBC_PASSWORD;
    		
    		JDBC_URL = Objects.requireNonNull(JDBC_URL);
    		JDBC_USER = Objects.requireNonNull(JDBC_USER);
    		JDBC_PASSWORD = Objects.requireNonNull(JDBC_PASSWORD);
    		
            DatabaseMigrationHelper databaseMigrationHelper = new DatabaseMigrationHelper(JDBC_URL, JDBC_USER, JDBC_PASSWORD);
            databaseMigrationHelper.upgradeDatabase();
            isReady = true;
            
        } catch (Exception e) {
        	logger.error("Unable to create and migrate database", e);
        	isReady = false;
        	hasError = true;
        }
	}
	
	public boolean isReady() {
		return isReady;
	}
	
	public boolean hasError() {
		return hasError;
	}
	
}
