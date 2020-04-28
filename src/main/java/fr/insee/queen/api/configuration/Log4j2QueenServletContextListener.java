package fr.insee.queen.api.configuration;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.logging.log4j.web.Log4jServletContextListener;
import org.apache.logging.log4j.web.Log4jWebSupport;

public class Log4j2QueenServletContextListener implements ServletContextListener {
	private String log4j2ConfigFile;

	private Log4jServletContextListener listener;

	public Log4j2QueenServletContextListener() {
		this.listener = new Log4jServletContextListener();
		try {
			this.getEnvironmentProperties();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void contextInitialized(ServletContextEvent servletContextEvent) {
		servletContextEvent.getServletContext().setInitParameter(Log4jWebSupport.LOG4J_CONFIG_LOCATION,
				log4j2ConfigFile);
		listener.contextInitialized(servletContextEvent);
	}

	@Override
	public void contextDestroyed(ServletContextEvent servletContextEvent) {
		listener.contextDestroyed(servletContextEvent);
	}

	private void getEnvironmentProperties() throws IOException {
		String log4JExternalFile = System.getProperty("fr.insee.queen.api.logging.path");
		this.log4j2ConfigFile = "log4j2.xml";
		File f = new File(String.format("%s/webapps/%s", System.getProperty("catalina.base"), "log4j2.xml"));
		if (f.exists() && !f.isDirectory()) {
			this.log4j2ConfigFile = String.format("%s/webapps/%s", System.getProperty("catalina.base"), "log4j2.xml");
		}
		File f2 = new File(log4JExternalFile);
		if (f2.exists() && !f2.isDirectory()) {
			this.log4j2ConfigFile = String.format(log4JExternalFile);
		}
	}
}
