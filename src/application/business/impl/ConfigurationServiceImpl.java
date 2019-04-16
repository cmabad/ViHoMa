package application.business.impl;

import java.sql.SQLException;
import java.util.List;

import application.business.ConfigurationService;
import application.business.util.Logger;
import application.conf.Factory;
import application.model.Configuration;
import application.util.properties.Settings;

public class ConfigurationServiceImpl implements ConfigurationService {

	@Override
	public long getLastUpdateTime() {
		try {
			return Factory.repository.forConfiguration().getLastUpdateTime();
		} catch (SQLException e) {
			//e.printStackTrace();
			add("lastUpdateTime","0");
		}
		return 0;
	}

	@Override
	public void setLastUpdateTime() {
		Factory.repository.forConfiguration()
			.setLastUpdateTime(String.valueOf(System.currentTimeMillis()/1000));
		Logger.log("CONFIGURATION UPDATED: " + "lastUpdateTime" + " = " + String.valueOf(System.currentTimeMillis()/1000));
	}

	@Override
	public void add(String parameter, String value) {
		Factory.repository.forConfiguration()
			.add(new Configuration(parameter,value));
		Logger.log("NEW CONFIGURATION: " + parameter + " = " + value);
	}

	@Override
	public List<Configuration> findAll() {
		return Factory.repository.forConfiguration().findAll();
	}

	@Override
	public String getBlockedAddress() {
		Configuration conf = this.findByParameter("blockedAddress");
		return (null == conf)? 
				Settings.get("defaultTargetAddress"):conf.getValue();
	}

	@Override
	public Configuration findByParameter(String parameter) {
		return Factory.repository.forConfiguration().findByParameter(parameter);
	}

}
