package application.persistence.sqlite;

import application.business.repository.CategoryRepository;
import application.business.repository.ConfigurationRepository;
import application.business.repository.CustomHostRepository;
import application.business.repository.HostRepository;
import application.business.repository.RepositoryFactory;

public class SQLiteRepositoryFactory implements RepositoryFactory{

	@Override
	public HostRepository forHost() {
		return new HostSQLiteRepository();
	}

	@Override
	public CustomHostRepository forCustomHost() {
		return new CustomHostSQLiteRepository();
	}

	@Override
	public ConfigurationRepository forConfiguration() {
		return new ConfigurationSQLiteRepository();
	}

	@Override
	public CategoryRepository forCategory() {
		// TODO Auto-generated method stub
		return null;
	}

}
