package application.business.impl;

import java.io.IOException;
import java.util.List;

import application.business.HostService;
import application.conf.Factory;
import application.model.Host;
import application.util.HostsFileManager;
import application.util.WebUtil;

public class HostServiceImpl implements HostService{

	@Override
	public void addHost(String domain, String category) {
		Factory.repository.forHost().add(new Host(domain,category));
	}

	@Override
	public void addHosts(List<Host> hostList) {
		//TODO: check if the list is empty?
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
	public void persistOnHostsFile() {
		HostsFileManager.editHostsFile();
	}

}
