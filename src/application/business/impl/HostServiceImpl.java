package application.business.impl;

import java.util.List;

import application.business.HostService;
import application.conf.Factory;
import application.model.Host;
import application.util.Logger;
import application.util.WebUtil;

public class HostServiceImpl implements HostService{

	@Override
	public int addHost(String domain){
		if (null == domain || "".equals(domain))
			return -1;
		
		return addHost(domain, Host.CATEGORY_VIHOMA);
	}
	
	@Override
	public int addHost(String domain, Integer category) {
		if (null == domain || "".equals(domain) || null == category)
			return -1;
		
		int count =  Factory.repository.forHost().add(new Host(domain,category));
		if (0 == count)
			Logger.err("ERROR BLOCKING DOMAIN " + domain + "  with category " 
					+ category);
		else
			Logger.log("NEW BLOCKED DOMAIN: " + domain);
		return count;
	}

	@Override
	public int addHosts(List<Host> hostList) {
		if(null == hostList || hostList.isEmpty())
			return 0;
		
		return Factory.repository.forHost().addHosts(hostList);
	}

	@Override
	public int getHostsCount() {
		return Factory.repository.forHost().getHostsCount();
	}

	@Override
	public List<Host> findAll() {
		return Factory.repository.forHost().findAll();
	}

	@Override
	public void toggleStatus(String domain) throws IllegalArgumentException{
		if (null == domain)
			throw new IllegalArgumentException("no host provided");
		
		Factory.repository.forHost().toggleHostStatus(domain);
	}

	@Override
	public List<Host> findAllActive() {
		return findByStatus(Host.STATUS_ACTIVE);
	}

	@Override
	public List<Host> findByDomain(String filter) {
		return Factory.repository.forHost().findByDomain(filter);
	}

	@Override
	public void deleteAll() {
		Factory.repository.forHost().deleteAll();
	}

	@Override
	public List<Host> findByCategory(int category) {
		return Factory.repository.forHost().findByCategory(category);
	}

	@Override
	public List<Host> updateDatabaseFromWeb() {
		List<Host> hosts = WebUtil.getHostsFromWebSource(
				Factory.service.forConfiguration().getWebSource());
		if (null != hosts && !hosts.isEmpty()) {
			List<Host> userAdded = findByCategory(Host.CATEGORY_VIHOMA);
			List<Host> deactivatedList = findByStatus(Host.STATUS_DELETED);
			deleteAll();
			addHosts(hosts);
			for (Host user : userAdded)
				addHost(user.getDomain(), user.getCategory());
			for (Host deactivated : deactivatedList) {
				addHost(deactivated.getDomain(), deactivated.getCategory());
				toggleStatus(deactivated.getDomain());
			}
			
			Factory.service.forConfiguration().setLastUpdateTime();
		}
		return hosts;
	}

	@Override
	public List<Host> findByStatus(int status) {
		return Factory.repository.forHost().findByStatus(status);
	}

}
