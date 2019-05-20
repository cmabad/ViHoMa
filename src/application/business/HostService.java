package application.business;

import java.util.List;

import application.model.Host;

public interface HostService {

	int addHost(String domain, Integer category);
	int addHosts(List<Host> hostsList);
	int getHostsCount();
	List<Host> findAll();
	void toggleStatus(String domain);
	List<Host> findAllActive();
	List<Host> findByDomain(String filter);
	void deleteAll();
	List<Host> findByCategory(int categoryVihoma);
	List<Host> getHostsFromWeb();
	List<Host> updateDatabaseFromWeb();
}
