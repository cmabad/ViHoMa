package application.business.repository;

import java.util.List;

import application.model.CustomHost;

public interface CustomHostRepository extends Repository<CustomHost>{

	void toggleStatus(String domain, String address);
	List<CustomHost> findAllActive();
	List<CustomHost> findByDomainOrIp(String filter);
	int getHostsCount();

}
