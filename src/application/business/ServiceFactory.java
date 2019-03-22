package application.business;

public interface ServiceFactory {

	HostService forHost();
	CustomHostService forCustomHost();
	CategoryService forCategory();
	ConfigurationService forConfiguration();
}
