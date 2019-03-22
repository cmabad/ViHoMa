package application.business.repository;

public interface RepositoryFactory {

	HostRepository forHost();
	CustomHostRepository forCustomHost();
	ConfigurationRepository forConfiguration();
	CategoryRepository forCategory();
}
