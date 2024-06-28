package net.whydah.uss.util.repository;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;

import javax.sql.DataSource;

import org.eclipse.jetty.server.handler.ContextHandler.Availability;
import org.hibernate.boot.model.naming.CamelCaseToUnderscoresNamingStrategy;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.jpa.HibernatePersistenceProvider;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.SharedCacheMode;
import jakarta.persistence.ValidationMode;
import jakarta.persistence.spi.ClassTransformer;
import jakarta.persistence.spi.PersistenceUnitInfo;
import jakarta.persistence.spi.PersistenceUnitTransactionType;
import net.whydah.uss.MainApplication;
import net.whydah.uss.settings.AppSettings;
import no.cantara.config.ApplicationProperties;



public abstract class Repository<T, K> {

	protected EntityManagerFactory emf = new HibernatePersistenceProvider().createContainerEntityManagerFactory(archiverPersistenceUnitInfo(), config());
	//protected EntityManagerFactory emf = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
	
	private static final String PERSISTENCE_UNIT_NAME = "net.whydah.uss";

	abstract T insert(T obj);
	
	abstract List<T> insertAll(List<T> objs);
	
	abstract T update(T obj);

	abstract List<T> updateAll(List<T> objs);
	
	abstract Optional<T> findById(K key);

	abstract void delete(T obj);

	abstract void deleteById(K key);
	
	abstract void deleteAll();
	
	abstract void deleteAllById(List<K> ids);
	
	abstract void cascadeDeleteById(K key);
	
	abstract List findWithNamedQuery(String queryName);
	
	abstract List findWithNamedQuery(String queryName,int resultLimit);
	
	abstract List findWithNamedQuery(String namedQueryName, Map<String, Object> parameters);
	
	abstract List findWithNamedQuery(String namedQueryName, Map<String, Object> parameters, int resultLimit);

	abstract List findAll();
	
	abstract List findWithQuery(String queryName);
	
	abstract List findWithQuery(String queryName,int resultLimit);
	
	abstract List findWithQuery(String query, Map<String, Object> parameters);
	
	abstract List findWithQuery(String query, Map<String, Object> parameters, int resultLimit);
	
	abstract List findWithNativeQuery(String queryName);
	
	abstract List findWithNativeQuery(String queryName,int resultLimit);
	
	abstract List findWithNativeQuery(String query, Map<String, Object> parameters);
	
	abstract List findWithNativeQuery(String query, Map<String, Object> parameters, int resultLimit);
	
	abstract Long count();
	
	abstract Long countWithQuery(String countJPQLQuery, Map<String, Object> parameters);
	
	abstract Long countWithNativeQuery(String countSQLQuery, Map<String, Object> parameters);
	
	
	public abstract Class<T> getClassType();

	public void close() {
		emf.close();
	}

	private Map<String, Object> config() {
		Map<String, Object> map = new HashMap<>();
	
		String JDBC_URL = AppSettings.JDBC_URL;
		String JDBC_DRIVER =  AppSettings.JDBC_DRIVER;
		String JDBC_USER = AppSettings.JDBC_USER;
		String JDBC_PASSWORD = AppSettings.JDBC_PASSWORD;
		
		JDBC_DRIVER = Objects.requireNonNull(JDBC_DRIVER);
		JDBC_URL = Objects.requireNonNull(JDBC_URL);
		JDBC_USER = Objects.requireNonNull(JDBC_USER);
		JDBC_PASSWORD = Objects.requireNonNull(JDBC_PASSWORD);
		
		map.put(AvailableSettings.JAKARTA_JDBC_DRIVER, JDBC_DRIVER);
		map.put(AvailableSettings.JAKARTA_JDBC_URL, JDBC_URL);
		map.put(AvailableSettings.JAKARTA_JDBC_USER, JDBC_USER);
		map.put(AvailableSettings.JAKARTA_JDBC_PASSWORD, JDBC_PASSWORD);
		
		map.put(AvailableSettings.PHYSICAL_NAMING_STRATEGY, "org.hibernate.boot.model.naming.CamelCaseToUnderscoresNamingStrategy");	
		map.put(AvailableSettings.HBM2DDL_AUTO, "none");	
		map.put(AvailableSettings.SHOW_SQL, "true");
		map.put(AvailableSettings.QUERY_STARTUP_CHECKING, "false");
		map.put(AvailableSettings.GENERATE_STATISTICS, "true");
		
		map.put(AvailableSettings.USE_SECOND_LEVEL_CACHE, "false");
		map.put(AvailableSettings.USE_QUERY_CACHE, "false");
		map.put(AvailableSettings.USE_STRUCTURED_CACHE, "false");
		map.put(AvailableSettings.STATEMENT_BATCH_SIZE, "30");
		map.put(AvailableSettings.AUTOCOMMIT, "false");
		map.put(AvailableSettings.ORDER_INSERTS, "true");
		map.put(AvailableSettings.ORDER_UPDATES, "true");
		
		map.put("hibernate.hikari.minimumIdle", "5");
		map.put("hibernate.hikari.maximumPoolSize", "15");
		map.put("hibernate.hikari.idleTimeout", "30000");

		return map;
	}
	
	private static PersistenceUnitInfo archiverPersistenceUnitInfo() {
		return new PersistenceUnitInfo() {
			@Override
			public String getPersistenceUnitName() {
				return PERSISTENCE_UNIT_NAME;
			}

			@Override
			public String getPersistenceProviderClassName() {
				return "com.zaxxer.hikari.hibernate.HikariConnectionProvider";
			}

			@Override
			public PersistenceUnitTransactionType getTransactionType() {
				return PersistenceUnitTransactionType.RESOURCE_LOCAL;
			}

			@Override
			public DataSource getJtaDataSource() {
				return null;
			}

			@Override
			public DataSource getNonJtaDataSource() {
				return null;
			}

			@Override
			public List<String> getMappingFileNames() {
				return Collections.emptyList();
			}

			@Override
			public List<java.net.URL> getJarFileUrls() {
				try {
					return Collections.list(this.getClass()
							.getClassLoader()
							.getResources(""));
				} catch (IOException e) {
					throw new UncheckedIOException(e);
				}
			}

			@Override
			public URL getPersistenceUnitRootUrl() {
				return null;
			}

			@Override
			public List<String> getManagedClassNames() {
				return Collections.emptyList();
			}

			@Override
			public boolean excludeUnlistedClasses() {
				return false;
			}

			@Override
			public SharedCacheMode getSharedCacheMode() {
				return null;
			}

			@Override
			public ValidationMode getValidationMode() {
				return null;
			}

			@Override
			public Properties getProperties() {
				return new Properties();
			}

			@Override
			public String getPersistenceXMLSchemaVersion() {
				return null;
			}

			@Override
			public ClassLoader getClassLoader() {
				return null;
			}

			@Override
			public void addTransformer(ClassTransformer transformer) {

			}

			@Override
			public ClassLoader getNewTempClassLoader() {
				return null;
			}
		};
	}

	
	
		


	
}