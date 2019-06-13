package application.business;

import java.util.List;

import application.model.Configuration;

public interface ConfigurationService {

	/**
	 * persists a Configuration to the repository.
	 * @param parameter the name of the new Configuration
	 * @param value the value of the new Configuration
	 */
	int add(String parameter, String value);
	/**
	 * returns the list of Configurations persisted in the repository.
	 * @return a List<Configuration> list, which may be empty
	 */
	List<Configuration> findAll();
	/**
	 * looks for a Configuration in the repository whose parameter is *exactly*
	 * the one passed as parameter.
	 * @param parameter name of the Configuration parameter to be searched
	 * @return the Configuration found with the given parameter name
	 */
	Configuration findByParameter(String parameter);
	/**
	 * changes the value of a Configuration persisted in the repository.
	 * @param parameter the parameter name of the Configuration
	 * @param value the new value the Configuration will have
	 * @return the number of affected rows
	 */
	int update(String parameter, String value);
	/**
	 * searches for a Configuration in the repository. If it is found, it updates
	 * its value. A new Configuration with the given parameter and value is
	 * added otherwise.
	 * @param parameter the name of the Configuration to be set
	 * @param newValue the value of the Configuration to be added or updated
	 */
	void set(String parameter, String newValue);

	
	/**
	 * syntax sugar for findByParameter("lastUpdateTime")
	 * @return the value of the last update stored in the repository. 0 if it
	 * has never been updated
	 */
	long getLastUpdateTime();
	/**
	 * syntax sugar for set("lastUpdateTime", {unixTime})
	 */
	void setLastUpdateTime();
	/**
	 * syntax sugar for findByParameter("blockedAddress"). If not found, returns
	 * the defaultBlockedAddress stored in the configuration files.
	 * @return
	 */
	String getBlockedAddress();
	/**
	 * syntax sugar for set("blockedAddress", newBlockingAddress)
	 * @param newBlockingAddress the new address to which the blocked hosts will
	 * be redirected
	 */
	void setBlockedAddress(String newBlockingAddress);
	/**
	 * syntax sugar for findByParameter("updateAtVihomaStartup").
	 * @return false if no value is found, or it is "no". True otherwise.
	 */
	boolean isUpdateAtVihomaStartupEnabled();
	/**
	 * syntax sugar for toggling the value of "updateAtVihomaStartup" 
	 * Configuration through the set method. Values can be "yes" or "no".
	 */
	void toggleUpdateAtVihomaStart();
	/**
	 * syntax sugar for findByParameter("webSource").
	 * @return the value found if not null, 'StevenBlack' + the enabled Steven
	 * Black categories otherwise.
	 */
	String getWebSource();
	/**
	 * syntax sugar for set("webSource", newSource)
	 * @param newSource the new source from where the blocked hosts list will be
	 * downloaded. Must start with 'http://' (without quotes)
	 */
	void setWebSource(String newSource);
	/**
	 * syntax sugar for set("StevenBlackCategories", String.valueOf(categories)).
	 * @param categories the sum of the enabled categories values. They are
	 * specified in the Host model class.
	 */
	void setStevenBlackCategories(int categories);
	/**
	 * syntax sugar for findByParameter("StevenBlackCategories").
	 * @return 0 if the value found is null or empty, the sum of the enabled
	 * categories otherwise. The value of each category is specified in the Host 
	 * model class
	 */
	int getStevenBlackCategories();
}
