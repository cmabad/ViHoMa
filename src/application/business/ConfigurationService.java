package application.business;

import java.util.List;

import application.model.Configuration;

public interface ConfigurationService {

	long getLastUpdateTime();
	void setLastUpdateTime();
	void add(String parameter, String value);
	List<Configuration> findAll();
	Configuration findByParameter(String parameter);
	String getBlockedAddress();
}
