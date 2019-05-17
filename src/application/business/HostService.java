package application.business;

import java.util.List;

import application.model.Host;

public interface HostService {

	void addHost(String domain, Integer category);
	void addHosts(List<Host> hostsList);
	Boolean updateHost(String domain);
	int getHostsCount();
	List<Host> findAll();
	void toggleStatus(String domain);
	List<Host> findAllActive();
	List<Host> findByDomain(String filter);
	List<Host> downloadHostsFromWeb();
	List<Host> downloadHostsFromAlternativeWeb();
}
