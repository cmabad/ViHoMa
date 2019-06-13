package application.conf;

import application.business.ServiceFactory;
import application.business.repository.RepositoryFactory;

/**
 * This class is implemented following the Abstract Factory Pattern.
 */
public class Factory {

	public static RepositoryFactory repository;
	public static ServiceFactory service;
}
