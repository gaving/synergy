package net.sourceforge.synergy.data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import net.sourceforge.synergy.Synergy;

import com.sun.rowset.CachedRowSetImpl;

/**
 * Backbone connection/disconnection class to the database and provides related operations.
 */

public class SQLInterface {

	private String server;

	private Statement stmt = null;

	private Connection conn = null;

	private CachedRowSetImpl crs;

	/**
	 * The constructor
	 * @param server the address of the database server
	 * @param database the name of the database
	 * @param username the username
	 * @param password the password
	 */
	public SQLInterface(String server, String database, String username,
			String password) {
		String url = "jdbc:mysql://" + server + "/" + database;
		this.server = server;

		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			conn = DriverManager.getConnection(url, username, password);
			Synergy.getDefault().logInfo(
					"Database: Connected to " + server + ".");
		} catch (SQLException se) {

			/* couldn't connect to server */
			if ("08S01".equals(se.getSQLState())) {
				Synergy.getDefault().logError(
						"Database:" + " Could not connect to " + server, se);
			}

			/* access denied */
			else if ("28000".equals(se.getSQLState())) {
				Synergy.getDefault().logError(
						"Database:" + " Access denied to " + server
								+ ". Check login details.", se);
			}
			/* something more serious */
			else {
				se.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Execute a query on the database
	 * @param query raw sql string
	 * @return <code>true</code> or <code>false</code> depending on success
	 */
	public boolean execQuery(String query) {
		ResultSet resultSet = null;
		try {
			stmt = conn.createStatement();
			resultSet = stmt.executeQuery(query);
			crs = new CachedRowSetImpl();
			crs.populate(resultSet);
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	/**
	 * Execute a update on the database
	 * @param query raw sql string
	 * @return <code>true</code> or <code>false</code> depending on success
	 */
	public boolean execUpdate(String update) {
		try {
			stmt = conn.createStatement();
			stmt.executeUpdate(update);
		} catch (SQLException se) {
			if ("23000".equals(se.getSQLState())) {

				/* TODO: Sort this out rather than leaving this unhandled */
				/*
				 * System.out.println("I fired an event change again for absolutely no reason.");
				 * se.printStackTrace();
				 */
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * Returns an active connection
	 * @return the connection
	 */
	public Connection getCon() {
		return conn;
	}

	/**
	 * Destroys the connection to the database
	 */
	public void destroy() {
		try {
			Synergy.getDefault().logInfo(
					"Database: Disconnected from " + this.server + ".");
			getCon().close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Returns a rowset of information
	 * @return the row set
	 */
	public CachedRowSetImpl getRowSet() {
		return crs;
	}
}