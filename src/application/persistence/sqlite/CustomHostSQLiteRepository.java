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
	public void add(CustomHost newHost) {
		try {
			conn = SQLiteJDBC.connect();
			pstmt = conn.prepareStatement(
					Settings.get("sqlInsertCustomHost"));
			pstmt.setString(1, newHost.getDomain());
			pstmt.setString(2, newHost.getAddress());
			pstmt.setInt(3, newHost.getStatus());
			pstmt.executeUpdate();
			
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		} finally {
			SQLiteJDBC.close(pstmt, conn);
		}
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
			// loop through the result set
			while (rs.next())
				hosts.add(new CustomHost(
						(String) rs.getString("domain")
						, (String) rs.getString("address")
						, (int) rs.getInt("status"))
						);
		} catch (SQLException e) {
			System.out.println(e.getMessage());
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
			System.out.println(e.getMessage());
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
			// loop through the result set
			while (rs.next())
				hosts.add(new CustomHost(
						(String) rs.getString("domain")
						, (String) rs.getString("address")
						, (int) rs.getInt("status"))
						);
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		} finally {
			SQLiteJDBC.close(rs, stmt, conn);
		}
		return hosts;
	}

}
