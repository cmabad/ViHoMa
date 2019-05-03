package application.business.repository;

import application.model.Configuration;

public interface ConfigurationRepository extends Repository<Configuration>{

	Configuration findByParameter(String parameter);
	int update(String parameter, String value);
}
