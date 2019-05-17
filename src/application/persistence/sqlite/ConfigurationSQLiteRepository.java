package application.persistence.sqlite;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import application.business.repository.ConfigurationRepository;
import application.model.Configuration;
import application.persistence.sqlite.util.SQLiteJDBC;
import application.util.properties.Settings;

public class ConfigurationSQLiteRepository extends BaseSQLiteRepository implements ConfigurationRepository {

	@Override
	public int add(Configuration t) {
		String parameter = t.getParameter();
		String value = t.getValue();
		
		if (null == parameter || null == value)
			throw new IllegalArgumentException(
					Settings.get("parametersCannotBeNull"));
		
		try {
			conn = SQLiteJDBC.connect();
			pstmt = conn.prepareStatement(
					Settings.get("sqlInsertConfiguration"));
			pstmt.setString(1, parameter);
			pstmt.setString(2, value);
			return pstmt.executeUpdate();
			
		} catch (SQLException e) {
			// System.out.println(e.getMessage());
			// ignored
		} finally {
			SQLiteJDBC.close(pstmt, conn);
		}
		return 0;
	}

	@Override
	public void delete(Configuration t) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Configuration findById(Long id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Configuration> findAll() {
		List<Configuration> configs = new ArrayList<Configuration>();
		try {
			conn = SQLiteJDBC.connect();
			stmt = conn.createStatement();
			rs = stmt.executeQuery(Settings.get("sqlSelectConfigurations"));
			// loop through the result set
			while (rs.next())
				configs.add(new Configuration(
						(String) rs.getString("parameter")
						, (String) rs.getString("value")
						));
			
		} catch (SQLException e) {
			// System.out.println(e.getMessage());
			// ignored
		} finally {
			SQLiteJDBC.close(rs, stmt, conn);
		}
		return configs;
	}

	@Override
	public Configuration findByParameter(String parameter) {
		try {
			conn = SQLiteJDBC.connect();
			pstmt = conn.prepareStatement(Settings.get("sqlSelectConfigurationByParameter"));
			pstmt.setString(1, parameter);
			rs = pstmt.executeQuery();
			// loop through the result set
			if (rs.next())
				return new Configuration(parameter,(String) rs.getString("value"));
		} catch (SQLException e) {
			// System.out.println(e.getMessage());
			// ignored
		} finally {
			SQLiteJDBC.close(rs, stmt, conn);
		}
		return null;
	}

	@Override
	public int update(String parameter, String value) {
		try {
			conn = SQLiteJDBC.connect();
			pstmt = conn.prepareStatement(
					Settings.get("sqlUpdateConfiguration"));
			pstmt.setString(2, parameter);
			pstmt.setString(1, value);
			return pstmt.executeUpdate();
		} catch (SQLException e) {
			// System.out.println(e.getMessage());
			// ignored
		} finally {
			SQLiteJDBC.close(pstmt, conn);
		}
		return -1;
	}

}
