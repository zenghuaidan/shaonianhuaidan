package com.yoson.sql;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URLDecoder;
import java.util.Properties;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;  
  

public class HibernateUtil  
{  
  
    public static SessionFactory getSessionFactory()  
    {  
    	InputStream fis = null;
        try  
        {    
            String path = URLDecoder.decode(Thread.currentThread().getContextClassLoader().getResource("jdbc.properties").getPath(), "UTF-8");  
            Properties properties = new Properties();  
            fis = new FileInputStream(path);  
            properties.load(fis);                   
  
            Properties extraProperties = new Properties();
            extraProperties.setProperty("hibernate.connection.driver_class", properties.getProperty("jdbc.driverClassName"));
            extraProperties.setProperty("hibernate.connection.url", properties.getProperty("jdbc.url"));  
            extraProperties.setProperty("hibernate.connection.username", properties.getProperty("jdbc.username"));  
            extraProperties.setProperty("hibernate.connection.password", properties.getProperty("jdbc.password"));
            
            extraProperties.setProperty("hibernate.connection.pool_size", "1");
            extraProperties.setProperty("hibernate.current_session_context_class", "thread");
            extraProperties.setProperty("hibernate.show_sql", "false");
            extraProperties.setProperty("hibernate.dialect", "org.hibernate.dialect.MySQLDialect");
  
            Configuration cfg = new Configuration();  
            cfg.addProperties(extraProperties);  
            return cfg.buildSessionFactory();  
        }  
        catch (Exception e)  
        {  
            e.printStackTrace();  
        } finally {
        	try {
        		fis.close();        		
        	} catch (Exception e) {
			}
		}
        
        return null;
    }  
    
} 