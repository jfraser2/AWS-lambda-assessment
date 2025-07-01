package hibenate.utils;

import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;

import entities.NotificationEntity;
import entities.TemplateEntity;

import java.util.Properties;

public class HibernateUtil {
	
	protected static SessionFactory sessionFactory;

	public static SessionFactory getSessionFactory() {
	    if (sessionFactory == null) {
	        try {
	        	
	    	    Properties properties = new Properties();
	    	    
	    	    properties.setProperty("hibernate.hbm2ddl.auto", "none");
//	    	    properties.setProperty("hibernate.hbm2ddl.auto", "create-drop");
//	    	    properties.setProperty("hibernate.hbm2ddl.auto", "update");
	    	    properties.setProperty("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
	    	    properties.setProperty("hibernate.current_session_context_class", "thread");
//	    	    <prop key="hibernate.current_session_context_class">org.hibernate.context.ThreadLocalSessionContext</prop>
	    	    properties.setProperty("hibernate.format_sql", "true");
	    	    properties.setProperty("hibernate.show_sql", "true");
	    	    properties.setProperty("hibernate.jdbc.lob.non_contextual_creation", "true");
	    	    properties.setProperty("hibernate.connection.release_mode", "after_transaction");
	    	    
	    	    properties.setProperty("hibernate.connection.provider_class", "com.zaxxer.hikari.hibernate.HikariConnectionProvider");
	    	    properties.setProperty("hibernate.hikari.dataSource.auto-commit", "false");
	    	    properties.setProperty("hibernate.hikari.dataSource.transaction-isolation", "2"); // TRANSACTION_READ_COMMITED
	    	    
	    	    String url = System.getenv("DB_URL"); // Environment Variable defined in template.yaml
	    	    String userName = System.getenv("DB_USER"); // Environment Variable defined in template.yaml
	    	    String password = System.getenv("DB_PASSWORD"); // Environment Variable defined in template.yaml
	    	    
	    	    properties.setProperty("hibernate.hikari.datasource.driver-class-name", "org.hibernate.dialect.PostgreSQLDialect");
	    	    properties.setProperty("hibernate.hikari.dataSource.url", url);
	    	    properties.setProperty("hibernate.hikari.dataSource.user", userName);
	    	    properties.setProperty("hibernate.hikari.dataSource.password", password);
	    	    
	    	    properties.setProperty("hibernate.hikari.autoconnectionTimeout", "20000");
	    	    properties.setProperty("hibernate.hikari.autoconnectionTimeout", "20000");
	    	    properties.setProperty("hibernate.hikari.connectionTimeout", "20000");
	    	    properties.setProperty("hibernate.hikari.minimumIdle", "1");
	    	    properties.setProperty("hibernate.hikari.maximumPoolSize", "5");
	    	    properties.setProperty("hibernate.hikari.idleTimeout", "30000");	    	    

/*	    	    
	            // Create a Map to hold Hibernate properties
	            Map<String, String> hibernateProperties = new HashMap<>();
	            hibernateProperties.put("hibernate.connection.driver_class", "com.mysql.cj.jdbc.Driver");
	            hibernateProperties.put("hibernate.connection.url", "jdbc:mysql://localhost:3306/mydatabase");
	            hibernateProperties.put("hibernate.connection.username", "root");
	            hibernateProperties.put("hibernate.connection.password", "sapassword");
	            hibernateProperties.put("hibernate.dialect", "org.hibernate.dialect.MySQL8Dialect");
	            hibernateProperties.put("hibernate.hbm2ddl.auto", "update"); // or "create", "create-drop", "validate"
	            hibernateProperties.put("hibernate.show_sql", "true");
	            hibernateProperties.put("hibernate.format_sql", "true");
*/
	    	    
	            // Create a Configuration object and apply properties from the Map
	            Configuration configuration = new Configuration();
	            configuration.setProperties(properties);
	            configuration.addClass(NotificationEntity.class);
	            configuration.addClass(TemplateEntity.class);
	
	            // Add annotated classes or mapping resources
	            // For example, if you have an annotated entity class named 'User':
	            // configuration.addAnnotatedClass(User.class);
	            // Or for HBM XML mapping files:
	            // configuration.addResource("com/example/User.hbm.xml");
	
	            // Build the ServiceRegistry from the Configuration
	            ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
	                    .applySettings(configuration.getProperties())
	                    .build();
	
	            // Build the SessionFactory
	            sessionFactory = configuration.buildSessionFactory(serviceRegistry);
	
	        } catch (Exception e) {
	            System.err.println("Initial SessionFactory creation failed." + e);
	            throw new ExceptionInInitializerError(e);
	        }
	    }
	    
	    return sessionFactory;
	}

	public static void shutdown() {
	    if (sessionFactory != null) {
	    	sessionFactory.close();
	    }
	}
	
}