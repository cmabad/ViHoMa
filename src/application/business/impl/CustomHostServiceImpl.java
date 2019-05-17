package application.business.impl;

import java.util.List;

import application.business.CustomHostService;
import application.conf.Factory;
import application.model.CustomHost;

public class CustomHostServiceImpl implements CustomHostService {

	@Override
	public int add(String domain, String address) {
		return Factory.repository.forCustomHost().add(new CustomHost(domain,address));
	}

	@Override
	public List<CustomHost> findAll() {
		return Factory.repository.forCustomHost().findAll();
	}

	@Override
	public void toggleStatus(String domain) {
		if (null == domain)
			throw new RuntimeException("no host provided");
		
		Factory.repository.forCustomHost().toggleStatus(domain);
	}

	@Override
	public List<CustomHost> findAllActive() {
		return Factory.repository.forCustomHost().findAllActive();
	}

	@Override
	public List<CustomHost> findByDomainOrIp(String filter) {
		return Factory.repository.forCustomHost().findByDomainOrIp(filter);
	}

	@Override
	public int getHostsCount() {
		return Factory.repository.forCustomHost().getHostsCount();
	}

}
