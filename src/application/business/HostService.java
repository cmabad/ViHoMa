package application.business;

import java.util.List;

import application.model.Host;

public interface HostService {

	/**
	 * persists a Host to the repository.
	 * @param domain the domain name of the host
	 * @return the number of hosts persisted
	 */
	int addHost(String domain);
	/**
	 * persists a Host to the repository.
	 * @param domain the domain name of the host
	 * @param category the category of the host
	 * @return the number of hosts persisted
	 */
	int addHost(String domain, Integer category);
	/**
	 * persists a list of Hosts to the repository.
	 * @param hostsList the list of hosts to be persisted
	 * @return the number of hosts persisted
	 */
	int addHosts(List<Host> hostsList);
	/**
	 * counts the number of Hosts persisted in the repository.
	 * @return the number of Hosts persisted in the repository.
	 */
	int getHostsCount();
	/**
	 * gets the list of Hosts persisted in the repository
	 * @return the list of Hosts persisted in the repository
	 */
	List<Host> findAll();
	/**
	 * gets the Hosts with status Host.STATUS_ACTIVE persisted in the repository
	 * @return a List<Host> of the ACTIVE persisted Hosts
	 */
	List<Host> findAllActive();
	/**
	 * toggles the status of a Host. The values available are Host.STATUS_ACTIVE
	 * and Host.STATUS_DELETED
	 * @param domain the domain name of the host whose status is to be toggled
	 */
	void toggleStatus(String domain);
	/**
	 * looks for Hosts in the repository whose domain name contains 
	 * the text passed as parameter.
	 * @param filter the search term
	 * @return a list of Hosts whose domain names contain the filter value
	 */
	List<Host> findByDomain(String filter);
	/**
	 * deletes all the persisted Hosts.
	 */
	void deleteAll();
	/**
	 * looks for Hosts in the repository whose category is the one passed as
	 * parameter. The categories available are specified in the Hosts model class.
	 * @param category the search term
	 * @return a list of Hosts whose category equals the one passed as paremeter
	 */
	List<Host> findByCategory(int category);
	/**
	 * replaces the persisted block hosts list by a new one which will be downloaded
	 * from the program webSource Configuration. The user manually blocked hosts
	 * and user manually disabled hosts are not replaced.
	 * @return a list of the hosts downloaded from the web
	 */
	List<Host> updateDatabaseFromWeb();
	/**
	 * looks for Hosts in the repository whose status is the one passed as
	 * parameter. The statuses available are specified in the Hosts model class.
	 * @param status the search status term
	 * @return a list of the Hosts whose status is the one passed as parameter
	 */
	List<Host> findByStatus(int status);
}
