package application.business.repository;

import java.util.List;

import application.model.Host;

public interface HostRepository extends Repository<Host>{

	int getHostsCount();
	void addHosts(List<Host> newHostsList);
	void toggleHostStatus(String domain);
	List<Host> findAllActive();
	List<Host> findByDomain(String domain);
}
