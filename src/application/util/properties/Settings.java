package application.util.properties;

import java.io.IOException;
import java.util.Properties;

/**
 * This class manages the file storing the internal text that will be used
 * in logs, system and web paths, and SQl queries.
 *
 */
public class Settings {

	private static final String CONF_FILE = "configuration.properties";
	
	private static Settings instance;
	private Properties properties;

	private Settings() {
		properties = new Properties();
		try {
			properties.load(Settings.class.getClassLoader().getResourceAsStream(CONF_FILE));
		} catch (IOException e) {
			throw new RuntimeException("Propeties file can not be loaded", e);
		}
	}
	
	public static String get(String key) {
		return getInstance().getProperty( key );
	}

	private String getProperty(String key) {
		String value = properties.getProperty(key);
		if (value == null) {
			throw new RuntimeException("Property not found in config file");
		}
		return value;
	}

	private static Settings getInstance() {
		if (instance == null) {
			instance = new Settings();
		}
		return instance;
	}

}
