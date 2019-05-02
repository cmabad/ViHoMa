package application.business.impl;

import java.util.List;

import application.business.ConfigurationService;
import application.conf.Factory;
import application.model.Configuration;
import application.util.Logger;
import application.util.properties.Settings;

public class ConfigurationServiceImpl implements ConfigurationService {

	@Override
	public long getLastUpdateTime() {
		Configuration conf = this.findByParameter("lastUpdateTime");
		return (null==conf)? 0L:Long.parseLong(conf.getValue());
	}

	@Override
	public void setLastUpdateTime() {
		if (0==this.getLastUpdateTime())
			this.add("lastUpdateTime", String.valueOf(System.currentTimeMillis()/1000));
		else
			this.update("lastUpdateTime", String.valueOf(System.currentTimeMillis()/1000));
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

	@Override
	public void update(String parameter, String value) {
		Factory.repository.forConfiguration().update(parameter, value);
		Logger.log("CONFIGURATION UPDATED: " + parameter + " = " + value);
	}

}
