package application.business;

public interface ServiceFactory {

	HostService forHost();
	CustomHostService forCustomHost();
	ConfigurationService forConfiguration();
}
