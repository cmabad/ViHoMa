package application.business.impl;

import java.util.List;

import application.business.CustomHostService;
import application.conf.Factory;
import application.model.CustomHost;
import application.util.Logger;

public class CustomHostServiceImpl implements CustomHostService {

	@Override
	public int add(String domain, String address) {
		try {
			new CustomHost(domain, address);
		}  catch (IllegalArgumentException e){
			// the address is not valid; avoid addition
			Logger.err("ERROR ADDING DOMAIN " + domain + "  with address " 
					+ address);
			return -1;
		}
		
		int count = Factory.repository.forCustomHost()
					.add(new CustomHost(domain,address));	
		if (0 == count)
			Logger.err("ERROR ADDING DOMAIN " + domain + "  with address " 
					+ address);
		else
			Logger.log("NEW CUSTOM DOMAIN: " + domain + " at " + address);
		return count;
	}

	@Override
	public List<CustomHost> findAll() {
		return Factory.repository.forCustomHost().findAll();
	}

	@Override
	public void toggleStatus(String domain, String address) {
		if (null == domain)
			throw new RuntimeException("no host provided");
		
		Factory.repository.forCustomHost().toggleStatus(domain, address);
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
