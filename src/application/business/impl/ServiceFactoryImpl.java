package application.business.impl;

import application.business.CategoryService;
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
		// TODO Auto-generated method stub
		return new CustomHostServiceImpl();
	}

	@Override
	public CategoryService forCategory() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ConfigurationService forConfiguration() {
		return new ConfigurationServiceImpl();
	}

}
