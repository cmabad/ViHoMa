package application.business;

import java.util.List;

import application.model.Configuration;

public interface ConfigurationService {

	void add(String parameter, String value);
	List<Configuration> findAll();
	Configuration findByParameter(String parameter);
	int update(String parameter, String value);
	void set(String string, String newAddress);

	long getLastUpdateTime();
	void setLastUpdateTime();
	String getBlockedAddress();
	boolean isSharingAllowed();
	boolean isUpdateAtVihomaStartupEnabled();
}
