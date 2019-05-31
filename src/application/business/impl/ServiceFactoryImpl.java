package application.business.impl;

import application.business.ConfigurationService;
import application.business.CustomHostService;
import application.business.HostService;
import application.business.ServiceFactory;

public class ServiceFactoryImpl implements ServiceFactory {

	@Override
	public HostService forHost() {
		return new HostServiceImpl();
	}

	@Override
	public CustomHostService forCustomHost() {
		return new CustomHostServiceImpl();
	}

	@Override
	public ConfigurationService forConfiguration() {
		return new ConfigurationServiceImpl();
	}

}
