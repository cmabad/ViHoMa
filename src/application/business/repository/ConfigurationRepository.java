package application.business.repository;

import java.sql.SQLException;

import application.model.Configuration;

public interface ConfigurationRepository extends Repository<Configuration>{

	Long getLastUpdateTime() throws SQLException;
	void setLastUpdateTime(String string);
	Configuration findByParameter(String parameter);
	int getBlockedIp();
}
