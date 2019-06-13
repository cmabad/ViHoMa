package application.business;

public interface ServiceFactory {

	/**
	 * returns the gateway for the blocked Hosts services. It is a implementation
	 * of the Abstract Factory Pattern.
	 * @return a HostService object with the available operations for 
	 * blocked Hosts
	 */
	HostService forHost();
	/**
	 * returns the gateway for the CustomHosts services. It is a implementation
	 * of the Abstract Factory Pattern.
	 * @return a CustomHostService with the available operations for CustomHosts
	 */
	CustomHostService forCustomHost();
	/**
	 * returns the gateway for the Configuration services. It is a implementation
	 * of the Abstract Factory Pattern.
	 * @return a ConfigurationService with the available operations for 
	 * Configurations 
	 */
	ConfigurationService forConfiguration();
}
