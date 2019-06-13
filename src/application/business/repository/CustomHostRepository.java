package application.business.repository;

import java.util.List;

import application.model.CustomHost;

public interface CustomHostRepository extends Repository<CustomHost>{

	/**
	 * toggles the status of a CustomHost.
	 * @param domain the domain name of the CustomHost whose status is to be changed
	 * @param address the address of the CustomHost whose status is to be toggled
	 */
	int toggleStatus(String domain, String address);
	/**
	 * looks for CustomHosts in the repository whose domain name or IP address
	 * contains the text passed as parameter.
	 * @param term the search term
	 * @return a list of the CustomHosts whose domain names or IP addresses contain
	 * the term passed as parameter
	 */
	List<CustomHost> findByDomainOrIp(String term);
	/**
	 * looks for CustomHosts in the repository whose status is the one passed as
	 * parameter
	 * @param status the search status term
	 * @return a list of the CustomHosts whose status is the one passed as parameter@return
	 */
	List<CustomHost> findByStatus(int status);
	/**
	 * counts the number of CustomHosts persisted in the repository.
	 * @return the number of CustomHosts in the repository
	 */
	int getHostsCount();
	/**
	 * empties the CustomHost repository
	 */
	void deleteAll();
}
