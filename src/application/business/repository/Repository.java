package application.business.repository;

import java.util.List;

public interface Repository<T> {
	void add(T t);
	void delete(T t);
	T findById(Long id);
	List<T> findAll();
}
