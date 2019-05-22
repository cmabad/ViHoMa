package application.persistence.sqlite.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import application.util.Logger;
import application.util.SystemUtil;
import application.util.properties.Settings;

public class SQLiteJDBC {

	private static SQLiteJDBC manager;
	
	/**
	 * Connect to the database
	 * 
	 * If the database does not exist, then SQLite automatically creates it.
	 */
	public static Connection connect() {
		
		try {
			// db parameters
			String url = "jdbc:sqlite:" + SystemUtil.getVihomaFolderPath() + "vihoma.sqlite";
			// create a connection to the database
			Connection conn = DriverManager.getConnection(url);
			
			return conn;

		} catch (SQLException e) {
			System.out.println(e.getMessage());
			return null;
		}

	}

	/**
	 * @param args the command line arguments
	 */
	public static void main(String[] args) {
		//connect();
		getManager().setUp();
	}

	public static SQLiteJDBC getManager() {
		if (null == manager) {
			manager = new SQLiteJDBC();
			manager.setUp();
		}
		return manager;
	}

	/**
	 * creates all the tables in the database and adds some minimal data. 
	 * If the db already exists, it's not changed.
	 */
	private void setUp() {
		// drop evrything (or delete file)
		Connection conn = connect();
		try {
			conn.setAutoCommit(false);

			conn.createStatement().execute(Settings.get("sqlCreateCustomHostsTable"));
			conn.createStatement().execute(Settings.get("sqlCreateHostsTable"));
			conn.createStatement().execute(Settings.get("sqlCreateConfigurationTable"));

			conn.commit();

		} catch (SQLException e) {
			Logger.err(e.toString());
		} finally {
			close(conn);
		}
	}

	public static void close(ResultSet rs, Statement stmt, Connection conn) {
		close(rs);
		close(stmt,conn);
	}

	public static void close(ResultSet rs) {
		try { rs.close(); } catch (Exception e) { /* Ignore */}
	}
	
	public static void close(Statement stmt, Connection conn) {
		try { stmt.close();	} catch (Exception e) { /* Ignore */ }
		close(conn);
	}
	
	public static void close(Connection conn) {
		try { conn.close();	} catch (Exception e) { /* Ignore */	}
	}
	
}