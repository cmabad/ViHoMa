package application.business;

import java.util.List;

import application.model.Host;

public interface HostService {

	void addHost(String domain, String category);
	void addHosts(List<Host> hostsList);
	Boolean updateHost(String domain);
	int getHostsCount();
	List<Host> downloadNewBlockedHostsFromWeb();
	List<Host> findAll();
	void persistOnHostsFile();
	void toggleStatus(String domain);
}
