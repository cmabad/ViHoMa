package application.business.impl;

import java.util.List;

import application.business.CustomHostService;
import application.conf.Factory;
import application.model.CustomHost;

public class CustomHostServiceImpl implements CustomHostService {

	@Override
	public void add(String domain, String address) {
		Factory.repository.forCustomHost().add(new CustomHost(domain,address));
	}

	@Override
	public List<CustomHost> findAll() {
		return Factory.repository.forCustomHost().findAll();
	}

}
