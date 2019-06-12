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
	void setBlockedAddress(String newBlockingAddress);
	boolean isSharingAllowed();
	void toggleSharing();
	boolean isUpdateAtVihomaStartupEnabled();
	void toggleUpdateAtVihomaStart();
	String getWebSource();
	void setWebSource(String newSource);
	void setStevenBlackCategories(int categories);
	int getStevenBlackCategories();
}
