package application.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Jdbc {

/*  Configuration for Oracle 
	private static String DRIVER = "oracle.jdbc.driver.OracleDriver";
	private static String URL = "jdbc:oracle:thin:@156.35.94.99:1521:DESA";
	private static String USER = "";
	private static String PASS = "";
*/
/* Configuration for Hsqldb
*/	
	private static String DRIVER = "org.hsqldb.jdbcDriver";
	private static String URL = "jdbc:hsqldb:hsql://localhost";
	private static String USER = "sa";
	private static String PASS = "";
	
	static {
		try {
			Class.forName( DRIVER );
		} catch (ClassNotFoundException e) {
			throw new RuntimeException("Driver not found in classpath", e);
		}
	}
	
	public static Connection getConnection() throws SQLException {
		return DriverManager.getConnection(URL, USER, PASS);
	}

	public static void close(ResultSet rs, Statement st, Connection c) {
		close(rs);
		close(st);
		close(c);
	}

	public static void close(ResultSet rs, Statement st) {
		close(rs);
		close(st);
	}

	protected static void close(ResultSet rs) {
		if (rs != null) try { rs.close(); } catch(SQLException e) {/* ignore */}
	}

	public static void close(Statement st) {
		if (st != null ) try { st.close(); } catch(SQLException e) {/* ignore */}
	}

	public static void close(Connection c) {
		if (c != null) try { c.close(); } catch(SQLException e) {/* ignore */}
	}

	public static Connection createThreadConnection() throws SQLException {
		Connection con = getConnection();
		// con.setAutoCommit( false );
		threadConnection.set(con);
		return con;
	}

	private static ThreadLocal<Connection> threadConnection = new ThreadLocal<Connection>();

	public static Connection getCurrentConnection() {
		return threadConnection.get();
	}

}
