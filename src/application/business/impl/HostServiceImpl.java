package application.business.impl;

import java.io.IOException;
import java.util.List;

import application.business.HostService;
import application.conf.Factory;
import application.model.Host;
import application.util.Logger;
import application.util.WebUtil;

public class HostServiceImpl implements HostService{

	@Override
	public void addHost(String domain, String category) {
		Factory.repository.forHost().add(new Host(domain,category));
		Logger.log("NEW BLOCKED DOMAIN: " + domain);
	}

	@Override
	public void addHosts(List<Host> hostList) {
		if(hostList.isEmpty())
			return;
		Factory.repository.forHost().addHosts(hostList);
		Factory.service.forConfiguration().setLastUpdateTime();
	}

	@Override
	public Boolean updateHost(String domain) {
		try {
			return WebUtil.uploadHostToWeb(domain);
		} catch (IOException e) {
			return null;
		}
	}

	@Override
	public int getHostsCount() {
		return Factory.repository.forHost().getHostsCount();
	}

	@Override
	public List<Host> downloadNewBlockedHostsFromWeb() {
		try {
			return WebUtil.getHostsFromWeb(
					Factory.service.forConfiguration().getLastUpdateTime());
		} catch (IOException e) {
			return null;
		}
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
		return Factory.repository.forHost().findAllActive();
	}

	@Override
	public List<Host> findByDomain(String filter) {
		return Factory.repository.forHost().findByDomain(filter);
	}

}
