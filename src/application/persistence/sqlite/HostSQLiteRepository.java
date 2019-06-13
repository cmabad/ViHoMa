package application.persistence.sqlite;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import application.business.repository.HostRepository;
import application.model.Host;
import application.persistence.sqlite.util.SQLiteJDBC;
import application.util.properties.Settings;

public class HostSQLiteRepository extends BaseSQLiteRepository implements HostRepository{
	
	public int addHosts(List<Host> newHostsList) {
		try {
			conn = SQLiteJDBC.connect();
			conn.setAutoCommit(false);
			pstmt = conn.prepareStatement(
					Settings.get("sqlInsertHost"));
			for (Host newHost : newHostsList) {
				pstmt.setString(1, newHost.getDomain());
				pstmt.setInt(2, newHost.getCategory());
				pstmt.setInt(3, newHost.getStatus());
				pstmt.setString(4, newHost.getComment());
				pstmt.setLong(5, newHost.getUpdatedAt());
				pstmt.executeUpdate();}
			
			conn.commit();
			return newHostsList.size();
		} catch (SQLException e) {
			//ignored
		} finally {
			SQLiteJDBC.close(pstmt, conn);
		}
		return 0;
				
	}

	public int getHostsCount() {
		try {
			conn = SQLiteJDBC.connect();
			int count = -1;
			stmt = conn.createStatement();

			rs = stmt.executeQuery(Settings.get("sqlSelectHostsCount"));
			if (rs.next())
				count = (Integer) rs.getInt("total");
			
			return count;
		} catch (SQLException e) {
			//ignored
		} finally {
			SQLiteJDBC.close(rs, stmt, conn);
		}
		return -1;
	}

	@Override
	public List<Host> findAll() {
		List<Host> hosts = new ArrayList<Host>();
		try {
			conn = SQLiteJDBC.connect();
			stmt = conn.createStatement();
			rs = stmt.executeQuery(Settings.get("sqlSelectHosts"));
			while (rs.next())
				hosts.add(new Host(
						(String) rs.getString("domain")
						, rs.getInt("category")
						, rs.getInt("status")
						, (String) rs.getString("comment")
						, rs.getInt("updated_at")
						));
			
		} catch (SQLException e) {
			//ignored
		} finally {
			SQLiteJDBC.close(rs, stmt, conn);
		}
		return hosts;
	}

	@Override
	public int add(Host newHost) {
		try {conn = SQLiteJDBC.connect();
				pstmt = conn.prepareStatement(
						Settings.get("sqlInsertHost"));
			pstmt.setString(1, newHost.getDomain());
			pstmt.setInt(2, newHost.getCategory());
			pstmt.setInt(3, newHost.getStatus());
			pstmt.setString(4, newHost.getComment());
			pstmt.setLong(5, newHost.getUpdatedAt());
			return pstmt.executeUpdate();
			
		} catch (SQLException e) {
			//ignored
		} finally {
			SQLiteJDBC.close(pstmt, conn);
		}
		return 0;
		
	}

	@Override
	public void delete(Host t) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Host findById(Long id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void toggleHostStatus(String domain) {
		try {
			conn = SQLiteJDBC.connect();
			pstmt = conn.prepareStatement(
				Settings.get("sqlUpdateHostToggleStatus"));
			pstmt.setString(1, domain);
			pstmt.setString(2, domain);
			pstmt.setString(3, domain);
			pstmt.executeUpdate();
			
		} catch (SQLException e) {
			//ignored
		} finally {
			SQLiteJDBC.close(pstmt, conn);
}
	}

	@Override
	public List<Host> findByDomain(String domain) {
		List<Host> hosts = new ArrayList<Host>();
		try {
			conn = SQLiteJDBC.connect();
			pstmt = conn.prepareStatement(
					Settings.get("sqlSelectHostsByDomain"));
			pstmt.setString(1, "%" + domain + "%");
			rs = pstmt.executeQuery();
			while (rs.next())
				hosts.add(new Host(
						(String) rs.getString("domain")
						, rs.getInt("category")
						, rs.getInt("status")
						, (String) rs.getString("comment")
						, rs.getInt("updated_at")
						));
			
		} catch (SQLException e) {
			//ignored		
		} finally {
			SQLiteJDBC.close(rs, pstmt, conn);
		}
		return hosts;
	}

	@Override
	public void deleteAll() {
		try {
			conn = SQLiteJDBC.connect();
			stmt = conn.createStatement();
			rs = stmt.executeQuery(Settings.get("sqlDeleteAllHosts"));
		} catch (SQLException e) {
			//ignored
		} finally {
			SQLiteJDBC.close(rs, stmt, conn);
		}
	}

	@Override
	public List<Host> findByCategory(int category) {
		List<Host> hosts = new ArrayList<Host>();
		try {
			conn = SQLiteJDBC.connect();
			pstmt = conn.prepareStatement(Settings.get("sqlSelectHostsByCategory"));
			pstmt.setInt(1, category);
			rs = pstmt.executeQuery();
			while (rs.next())
				hosts.add(new Host(
						(String) rs.getString("domain")
						, rs.getInt("category")
						, rs.getInt("status")
						, (String) rs.getString("comment")
						, rs.getInt("updated_at")
						));
			
		} catch (SQLException e) {
			//ignored		
		} finally {
			SQLiteJDBC.close(rs, pstmt, conn);
		}
		return hosts;
	}

	@Override
	public List<Host> findByStatus(int status) {
		List<Host> hosts = new ArrayList<Host>();
		try {
			conn = SQLiteJDBC.connect();
			pstmt = conn.prepareStatement(Settings.get("sqlSelectHostsByStatus"));
			pstmt.setInt(1, status);
			pstmt.setInt(2, status);
			rs = pstmt.executeQuery();
			while (rs.next())
				hosts.add(new Host(
						(String) rs.getString("domain")
						, rs.getInt("category")
						, rs.getInt("status")
						, (String) rs.getString("comment")
						, rs.getInt("updated_at")
						));
			
		} catch (SQLException e) {
			//ignored
		} finally {
			SQLiteJDBC.close(rs, stmt, conn);
		}
		return hosts;
	}
}
