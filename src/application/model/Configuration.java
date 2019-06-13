package application.model;

/**
 * This class represents a setting entity of the application. It is composed by a String
 * parameter attribute and a String value attribute.
 *
 */
public class Configuration {

	private String parameter, value;
	
	/**
	 * initializes a Vihoma setting
	 * @param parameter the name of the setting
	 * @param value the value of the setting
	 */
	public Configuration(String parameter, String value) {
		this.parameter = parameter;
		this.value = value;
	}

	public String getParameter() {
		return parameter;
	}

	public void setParameter(String parameter) {
		this.parameter = parameter;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
	
}
