package application.business.repository;

import java.util.List;

import application.model.Host;

public interface HostRepository extends Repository<Host>{

	int getHostsCount();
	int addHosts(List<Host> newHostsList);
	void toggleHostStatus(String domain);
	List<Host> findByDomain(String domain);
	void deleteAll();
	List<Host> findByCategory(int category);
	List<Host> findByStatus(int status);
}
