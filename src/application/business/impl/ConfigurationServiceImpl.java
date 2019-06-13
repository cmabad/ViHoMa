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
	public int add(String parameter, String value) {
		int count = Factory.repository.forConfiguration()
			.add(new Configuration(parameter,value));
		if (0 == count)
			Logger.err("ERROR ADDING CONFIGURATION: " + parameter + " = " + value);
		else
			Logger.log("NEW CONFIGURATION: " + parameter + " = " + value);
		
		return count;
	}

	@Override
	public List<Configuration> findAll() {
		return Factory.repository.forConfiguration().findAll();
	}

	@Override
	public String getBlockedAddress() {
		Configuration conf = this.findByParameter("blockedAddress");
		return (null == conf || "".equals(conf.getValue()))? 
			Settings.get("defaultBlockedAddress"):conf.getValue();
	}

	@Override
	public void setBlockedAddress(String newBlockingAddress) {
		set("blockedAddress", newBlockingAddress);
	}
	
	@Override
	public Configuration findByParameter(String parameter) throws IllegalArgumentException{
		if (null == parameter)
			throw new IllegalArgumentException("no parameter provided");
		
		return Factory.repository.forConfiguration().findByParameter(parameter);
	}

	@Override
	public int update(String parameter, String value) throws IllegalArgumentException{
		if (null == parameter || null == value || "".equals(parameter))
			throw new IllegalArgumentException("at least a non-empty parameter is needed");
		
		int count = Factory.repository.forConfiguration().update(parameter, value); 
		
		if (0 < count)
			Logger.log("CONFIGURATION UPDATED: " + parameter + " = " + value);
		
		return count;
	}

	@Override
	public void set(String parameter, String newValue) throws IllegalArgumentException{
		if (null == parameter || null == newValue || "".equals(parameter))
			throw new IllegalArgumentException("at least a non-empty parameter is needed");
		
		Configuration conf = this.findByParameter(parameter);
		if (null == conf)
			add(parameter,newValue);
		else
			update(parameter,newValue);
	}

	@Override
	public boolean isUpdateAtVihomaStartupEnabled() {
		Configuration conf = this.findByParameter("updateAtVihomaStartup");
		
		if (null == conf || "no".equals(conf.getValue()))
			return false;
		return true;
	}

	@Override
	public void toggleUpdateAtVihomaStart() {
		if (isUpdateAtVihomaStartupEnabled()) 
			Factory.service.forConfiguration().set("updateAtVihomaStartup", "no");
		else
			Factory.service.forConfiguration().set("updateAtVihomaStartup", "yes");
	}

	@Override
	public String getWebSource() {
		Configuration conf = this.findByParameter("webSource");
		
		return (null == conf || "".equals(conf.getValue()) 
				|| conf.getValue().startsWith(Settings.get("defaultWebSourceDomain")))?
			Settings.get("StevenBlack"+getStevenBlackCategories())
			:conf.getValue();
	}

	@Override
	public void setWebSource(String newSource) {
		set("webSource", newSource);
	}

	@Override
	public void setStevenBlackCategories(int categories) {
		set("StevenBlackCategories", String.valueOf(categories));
	}

	@Override
	public int getStevenBlackCategories() {
		Configuration categories = this.findByParameter("StevenBlackCategories");
		return (null == categories || "".equals(categories.getValue()))?
			0:Integer.parseInt(categories.getValue());
	}
	
}
