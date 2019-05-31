package application.persistence.sqlite;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import application.business.repository.CustomHostRepository;
import application.model.CustomHost;
import application.persistence.sqlite.util.SQLiteJDBC;
import application.util.properties.Settings;

public class CustomHostSQLiteRepository extends BaseSQLiteRepository implements CustomHostRepository {

	@Override
	public int add(CustomHost newHost) {
		try {
			conn = SQLiteJDBC.connect();
			pstmt = conn.prepareStatement(
					Settings.get("sqlInsertCustomHost"));
			pstmt.setString(1, newHost.getDomain());
			pstmt.setString(2, newHost.getAddress());
			pstmt.setInt(3, newHost.getStatus());
			return pstmt.executeUpdate();
			
		} catch (SQLException e) {
			// ignored
		} finally {
			SQLiteJDBC.close(pstmt, conn);
		}
		return 0;
	}

	@Override
	public void delete(CustomHost t) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public CustomHost findById(Long id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<CustomHost> findAll() {
		List<CustomHost> hosts = new ArrayList<CustomHost>();
		try {
			conn = SQLiteJDBC.connect();
			stmt = conn.createStatement();
			rs = stmt.executeQuery(Settings.get("sqlSelectCustomHosts"));
			while (rs.next())
				hosts.add(new CustomHost(
						(String) rs.getString("domain")
						, (String) rs.getString("address")
						, (int) rs.getInt("status"))
						);
		} catch (SQLException e) {
			// ignored
		} finally {
			SQLiteJDBC.close(rs, stmt, conn);
		}
		return hosts;
	}

	@Override
	public void toggleStatus(String domain) {
		try {
			conn = SQLiteJDBC.connect();
			pstmt = conn.prepareStatement(
				Settings.get("sqlUpdateCustomHostToggleStatus"));
			pstmt.setString(1, domain);
			pstmt.setString(2, domain);
			pstmt.setString(3, domain);
			pstmt.executeUpdate();
			
		} catch (SQLException e) {
			// ignored
		} finally {
			SQLiteJDBC.close(pstmt, conn);
}		
	}

	@Override
	public List<CustomHost> findAllActive() {
		List<CustomHost> hosts = new ArrayList<CustomHost>();
		try {
			conn = SQLiteJDBC.connect();
			stmt = conn.createStatement();
			rs = stmt.executeQuery(Settings.get("sqlSelectCustomHostsActive"));
			while (rs.next())
				hosts.add(new CustomHost(
						(String) rs.getString("domain")
						, (String) rs.getString("address")
						, (int) rs.getInt("status"))
						);
		} catch (SQLException e) {
			// ignored
		} finally {
			SQLiteJDBC.close(rs, stmt, conn);
		}
		return hosts;
	}

	@Override
	public List<CustomHost> findByDomainOrIp(String filter) {
		List<CustomHost> hosts = new ArrayList<CustomHost>();
		try {
			conn = SQLiteJDBC.connect();
			pstmt = conn.prepareStatement(
					Settings.get("sqlSelectCustomHostsByDomainOrIp"));
			pstmt.setString(1, "%" + filter + "%");
			pstmt.setString(2, "%" + filter + "%");
			rs = pstmt.executeQuery();
			while (rs.next())
				hosts.add(new CustomHost(
						(String) rs.getString("domain")
						, (String) rs.getString("address")
						, rs.getInt("status")
						));
			
		} catch (SQLException e) {
			// ignored
		} finally {
			SQLiteJDBC.close(rs, pstmt, conn);
		}
		return hosts;
	}

	@Override
	public int getHostsCount() {
		try {
			conn = SQLiteJDBC.connect();
			int count = -1;
			stmt = conn.createStatement();

			rs = stmt.executeQuery(Settings.get("sqlSelectCustomHostsCount"));
			if (rs.next())
				count = (Integer) rs.getInt("total");
			
			return count;
		} catch (SQLException e) {
			// ignored
		} finally {
			SQLiteJDBC.close(rs, stmt, conn);
		}
		return -1;
	}

}
