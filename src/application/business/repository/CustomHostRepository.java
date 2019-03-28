package application.business.repository;

import application.model.CustomHost;

public interface CustomHostRepository extends Repository<CustomHost>{

	void toggleStatus(String domain);

}
