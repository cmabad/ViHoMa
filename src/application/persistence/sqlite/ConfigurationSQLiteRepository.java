package application.persistence.sqlite;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import application.business.repository.ConfigurationRepository;
import application.model.Configuration;
import application.persistence.sqlite.util.SQLiteJDBC;
import application.util.properties.Settings;

public class ConfigurationSQLiteRepository extends BaseSQLiteRepository implements ConfigurationRepository {

	public Long getLastUpdateTime() throws SQLException {
		long lastUpdate = 0;
		try {
			conn = SQLiteJDBC.connect();
			stmt = conn.createStatement();
			rs = stmt.executeQuery(
					Settings.get("sqlSelectLastUpdateTime"));
			if (rs.next())
				lastUpdate = Long.parseLong(rs.getString("value"));
			
			return lastUpdate;
		} catch (SQLException e) {
			//e.printStackTrace();
			throw e;
			//lastUpdate = 0;
			//add(new Configuration("lastUpdateTime", String.valueOf(lastUpdate)));
		} finally {
			SQLiteJDBC.close(rs, stmt, conn);
		}
	}

	@Override
	public void add(Configuration t) {
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
			pstmt.execute();
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			SQLiteJDBC.close(pstmt, conn);
		}
		
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
			System.out.println(e.getMessage());
		} finally {
			SQLiteJDBC.close(rs, stmt, conn);
		}
		return configs;
	}
	
	@Override
	public void setLastUpdateTime(String utime) {
		try {
			conn = SQLiteJDBC.connect();
			pstmt = conn.prepareStatement(
					Settings.get("sqlUpdateLastUpdateTime"));
			pstmt.setString(1, utime);
			pstmt.execute();
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			SQLiteJDBC.close(pstmt, conn);
		}
		
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
			e.printStackTrace();
		} finally {
			SQLiteJDBC.close(rs, stmt, conn);
		}
		return null;
	}

	@Override
	public int getBlockedIp() {
		// TODO Auto-generated method stub
		return 0;
	}

}
