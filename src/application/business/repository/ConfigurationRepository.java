package application.business.repository;

import application.model.Configuration;

public interface ConfigurationRepository extends Repository<Configuration>{

	Configuration findByParameter(String parameter);
	void update(String parameter, String value);
}
