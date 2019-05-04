package application.business;

import java.util.List;

import application.model.CustomHost;

public interface CustomHostService {

	void add(String domain, String address);
	List<CustomHost> findAll();
	void toggleStatus(String domain);
	List<CustomHost> findAllActive();
	List<CustomHost> findByDomainOrIp(String filter);
	int getHostsCount();

}
