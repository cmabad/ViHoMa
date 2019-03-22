package application.util.properties;

import java.io.IOException;
import java.util.Properties;

public class Messages{
	
	private static String CONF_FILE = "messages_enEN.properties";
	
	private static Messages instance;
	private static Properties properties;

	private Messages() {
		properties = new Properties();
		changePropertiesFile();
	}
	
	private static void changePropertiesFile() {
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

	private static Messages getInstance() {
		if (instance == null) {
			instance = new Messages();
		}
		return instance;
	}
	
	public static void setLanguage(String locale) {
		//TODO
		//if esES... CONF_FILE = x
		//if enEN...
		changePropertiesFile();
	}
}
