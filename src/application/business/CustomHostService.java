package application.business;

import java.util.List;

import application.model.CustomHost;

public interface CustomHostService {

	/**
	 * persists a CustomHost to the repository. IP address must have the IPv4 
	 * or IPv6 format. Several custom hosts with the same domain name may coexist
	 * in the database, but with different addresses.
	 * @param domain the domain name of the CustomHost
	 * @param address the IP address of the CustomHost
	 * @return the number of hosts added
	 */
	int add(String domain, String address);
	/**
	 * gets the list of CustomHosts persisted in the repository
	 * @return the list of CustomHosts persisted in the repository
	 */
	List<CustomHost> findAll();
	/**
	 * gets the CustomHosts with status Host.STATUS_ACTIVE persisted in the 
	 * repository
	 * @return a List<CustomHost> of the ACTIVE persisted CustomHosts
	 */
	List<CustomHost> findAllActive();
	/**
	 * looks for CustomHosts in the repository whose domain name or IP address
	 * contains the text passed as parameter.
	 * @param filter the search term
	 * @return a list of CustomHosts whose domain names or IP addresses
	 * contain the filter value
	 */
	List<CustomHost> findByDomainOrIp(String filter);
	/**
	 * looks for CustomHosts in the repository whose status is the one passed as
	 * parameter. The statuses available are specified in the Hosts model class.
	 * @param status the search status term
	 * @return a list of the CustomHosts whose status is the one passed as parameter
	 */
	List<CustomHost> findByStatus(int status);
	/**
	 * toggles the status of a CustomHost. The values available are Host.STATUS_ACTIVE
	 * and Host.STATUS_DELETED
	 * @param domain the domain name of the CustomHost whose status is to be toggled
	 * @param address the address of the CustomHost whose status is to be toggled
	 */
	int toggleStatus(String domain, String address);
	/**
	 * counts the number of CustomHosts persisted in the repository.
	 * @return the number of CustomHosts in the repository
	 */
	int getHostsCount();
	/**
	 * deletes all the persisted CustomHosts.
	 */
	void deleteAll();
}
