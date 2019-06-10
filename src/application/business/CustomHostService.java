package application.business;

import java.util.List;

import application.model.CustomHost;

public interface CustomHostService {

	int add(String domain, String address);
	List<CustomHost> findAll();
	void toggleStatus(String domain, String address);
	List<CustomHost> findAllActive();
	List<CustomHost> findByDomainOrIp(String filter);
	int getHostsCount();
	void deleteAll();
}
