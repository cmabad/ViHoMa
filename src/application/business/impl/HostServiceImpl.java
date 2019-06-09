package application.business.impl;

import java.util.List;

import application.business.HostService;
import application.conf.Factory;
import application.model.Host;
import application.util.Logger;
import application.util.WebUtil;

public class HostServiceImpl implements HostService{

	@Override
	public int addHost(String domain, Integer category) {
		int count =  Factory.repository.forHost().add(new Host(domain,category));
		if (0 == count)
			Logger.err("ERROR BLOCKING DOMAIN " + domain + "  with category " 
					+ category);
		else {
			if (Factory.service.forConfiguration().isSharingAllowed())
				WebUtil.uploadHostToWeb(domain);
			Logger.log("NEW BLOCKED DOMAIN: " + domain);
		}
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
	public void toggleStatus(String domain) {
		if (null == domain)
			throw new RuntimeException("no host provided");
		
		Factory.repository.forHost().toggleHostStatus(domain);
	}

	@Override
	public List<Host> findAllActive() {
		return findByStatus(Host.STATUS_OK);
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
	public List<Host> getHostsFromWeb() {
		return WebUtil.getHostsFromWeb(0);
	}

	@Override
	public List<Host> updateDatabaseFromWeb() {
		List<Host> hosts = WebUtil.getHostsFromWeb(0);
		if (null != hosts && !hosts.isEmpty()) {
			List<Host> userAdded = findByCategory(Host.CATEGORY_VIHOMA);
			List<Host> deactivatedList = findByStatus(Host.STATUS_DELETED);
			deleteAll();
			addHosts(hosts);
			for (Host userhost : userAdded)
				addHost(userhost.getDomain(), userhost.getCategory());
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
