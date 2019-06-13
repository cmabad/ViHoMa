package application.business.repository;

import java.util.List;

import application.model.Host;

public interface HostRepository extends Repository<Host>{

	/**
	 * counts the number of Hosts in the repository
	 * @return the number of Hosts persisted in the repository
	 */
	int getHostsCount();
	/**
	 * persists a list of Hosts to the repository.
	 * @param hostsList the list of hosts to be persisted
	 * @return
	 */
	int addHosts(List<Host> hostsList);
	/**
	 * toggles the status of a Host
	 * @param domain the domain name of the host whose status is to be toggled
	 */
	void toggleHostStatus(String domain);
	/**
	 * looks for Hosts in the repository whose domain name contains 
	 * the text passed as parameter.
	 * @param domain the search term
	 * @return a list of Hosts whose domain names contain the filter value
	 */
	List<Host> findByDomain(String domain);
	/**
	 * empties the Hosts repository
	 */
	void deleteAll();
	/**
	 * looks for Hosts in the repository whose category is the one passed as
	 * parameter. The categories available are specified in the Hosts model class.
	 * @param category the search term
	 * @return a list of Hosts whose category equals the one passed as parameter
	 */
	List<Host> findByCategory(int category);
	/**
	 * looks for Hosts in the repository whose status is the one passed as
	 * parameter.
	 * @param status the search term
	 * @return a list of the Hosts whose status is the one passed as parameter
	 */
	List<Host> findByStatus(int status);
}
