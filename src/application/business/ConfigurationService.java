package application.business;

import java.util.List;

import application.model.Configuration;

public interface ConfigurationService {

	void add(String parameter, String value);
	List<Configuration> findAll();
	Configuration findByParameter(String parameter);
	void update(String parameter, String value);
	
	long getLastUpdateTime();
	void setLastUpdateTime();
	String getBlockedAddress();
}
