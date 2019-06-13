package application.business.repository;

import application.model.Configuration;

public interface ConfigurationRepository extends Repository<Configuration>{

	/**
	 * @param parameter the name of the Configuration parameter to be searched
	 * @return a Configuration object made up with the parameter and value found
	 * in the repository
	 */
	Configuration findByParameter(String parameter);
	/**
	 * @param parameter the name of the Configuration name to be updated
	 * @param value the new value of the Configuration to be updated 
	 * @return the number of Configurations updated in the repository, -1 if an 
	 * error was produced
	 */
	int update(String parameter, String value);
}
