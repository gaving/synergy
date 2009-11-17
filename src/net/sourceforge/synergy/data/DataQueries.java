package net.sourceforge.synergy.data;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import net.sourceforge.synergy.Synergy;

import com.sun.rowset.CachedRowSetImpl;

/**
 * Contains a collection of frequently used, general queries to the database.
 */

public class DataQueries {
	
	private static SQLInterface iface = Synergy.getDefault().getDatabase();

	/**
	 * Returns the number of developers currently tracking a file
	 * @param project the files project
	 * @param file the filename
	 * @return the number of developers that have modified this file
	 */
	public static int getFileUserHistory(String project, String file) {

		int userCount = 0;

		if (!iface
				.execQuery("SELECT COUNT(username) FROM eclipse_ProjectTable WHERE project = '"
						+ project + "' AND file='" + file + "';")) {
			return 0;
		}
		try {
			CachedRowSetImpl crs = new CachedRowSetImpl();
			crs = iface.getRowSet();
			while (crs.next()) {
				userCount = crs.getInt(1);
			}
		} catch (SQLException se) {
			se.printStackTrace();
		}
		return userCount;
	}

	/**
	 * Returns the amount of times a specific developer has modified a file
	 * @param project the files project
	 * @param file the filename
	 * @param name the name of the developer
	 * @return the number of times the file has been modified
	 */
	public static int getUsersFileModTotal(String project, String file,
			String name) {
		int userCount = 0;

		if (!iface
				.execQuery("SELECT COUNT(username) FROM eclipse_ProjectTable WHERE project = '"
						+ project
						+ "' AND file='"
						+ file
						+ "'AND username='"
						+ name + "';")) {
			return 0;
		}
		try {
			CachedRowSetImpl crs = new CachedRowSetImpl();
			crs = iface.getRowSet();
			while (crs.next()) {
				userCount = crs.getInt(1);
			}
		} catch (SQLException se) {
			se.printStackTrace();
		}
		return userCount;
	}

	/**
	 * Returns all modified times of a particular file
	 * @param project the files project
	 * @param file the filename
	 * @return the modified times
	 */
	public static String getUsersModifiedTimes(String project, String file) {
		String modified = "";

		if (!iface
				.execQuery("SELECT username, modified FROM eclipse_ProjectTable WHERE project = '"
						+ project + "' AND file='" + file + "';")) {
			return null;
		}
		try {
			CachedRowSetImpl crs = new CachedRowSetImpl();
			crs = iface.getRowSet();
			while (crs.next()) {
				modified = modified + crs.getString(2) + "\n";
			}
		} catch (SQLException se) {
			se.printStackTrace();
		}
		return modified;
	}

	/**
	 * Returns the names of developers who have modified a particular file
	 * @param project the files project
	 * @param file the filename
	 * @return the developers that have modified this file
	 */
	public static List<String> getUsersWhichModifiedFile(String project,
			String file) {
		List<String> userNames = new ArrayList<String>();

		if (!iface
				.execQuery("SELECT DISTINCT(username) FROM eclipse_ProjectTable WHERE project = '"
						+ project + "' AND file='" + file + "';")) {
			return null;
		}
		try {
			CachedRowSetImpl crs = new CachedRowSetImpl();
			crs = iface.getRowSet();
			while (crs.next()) {
				userNames.add(crs.getString(1));
			}
		} catch (SQLException se) {
			se.printStackTrace();
		}

		return userNames;
	}

	/**
	 * Returns the first time a developer modified a file
	 * @param project the files project
	 * @param file the filename
	 * @return the first time this developer modified the file
	 */
	public static String getUserFirstModForFile(String project, String file,
			String name) {
	
		String times = "";
	
		if (!iface
				.execQuery("SELECT  DATE_FORMAT(MIN(modified),'%b %D, %Y at %T hrs') FROM eclipse_ProjectTable WHERE project = '"
						+ project
						+ "' AND file='"
						+ file
						+ "'AND username='"
						+ name + "';")) {
			return null;
		}
		try {
			CachedRowSetImpl crs = new CachedRowSetImpl();
			crs = iface.getRowSet();
			while (crs.next()) {
	
				times = times + (crs.getString(1));
	
			}
		} catch (SQLException se) {
			se.printStackTrace();
		}
	
		return times;
	}

	/**
	 * Returns the last time a developer modified a file
	 * @param project the files project
	 * @param file the filename
	 * @return the last time this developer modified the file
	 */
	public static String getUserLastModForFile(String project, String file,
			String name) {

		String times = "";

		if (!iface
				.execQuery("SELECT  DATE_FORMAT(MAX(modified),'%b %D, %Y at %T hrs') FROM eclipse_ProjectTable WHERE project = '"
						+ project
						+ "' AND file='"
						+ file
						+ "'AND username='"
						+ name + "';")) {
			return null;
		}
		try {
			CachedRowSetImpl crs = new CachedRowSetImpl();
			crs = iface.getRowSet();
			while (crs.next()) {
				times = times + (crs.getString(1));
			}
		} catch (SQLException se) {
			se.printStackTrace();
		}

		return times;
	}

	/**
	 * Returns a list of files a developer has modified
	 * @param name the developers name
	 * @return the list of files a developer has modified
	 */
	public static List<String> getUsersModifiedFiles(String name) {

		List<String> files = new ArrayList<String>();

		if (!iface
				.execQuery("SELECT DISTINCT(file) FROM eclipse_ProjectTable WHERE username ='"
						+ name + "';")) {
			return null;
		}
		try {

			CachedRowSetImpl crs = new CachedRowSetImpl();
			crs = iface.getRowSet();
			while (crs.next()) {
				files.add(crs.getString(1));
			}
		} catch (SQLException se) {
			se.printStackTrace();
		}
		return files;
	}

	/**
	 * Returns a list of projects a developer has modified
	 * @param name the developers name
	 * @return the list of projects a developer has modified
	 */
	public static List<String> getUsersModifiedProjects(String name) {

		List<String> files = new ArrayList<String>();

		if (!iface
				.execQuery("SELECT DISTINCT(file), project FROM eclipse_ProjectTable WHERE username ='"
						+ name + "';")) {
			return null;
		}
		try {
			CachedRowSetImpl crs = new CachedRowSetImpl();
			crs = iface.getRowSet();
			while (crs.next()) {
				files.add(crs.getString(2));
			}
		} catch (SQLException se) {
			se.printStackTrace();
		}
		return files;
	}

	/**
	 * Returns a list of files only the current user has modified
	 * @param project the project
	 * @return the files only the current developer has modified
	 */
	public static List<String> getWatchedFiles(String project) {

		List<String> watchedFiles = new ArrayList<String>();

		if (!iface
				.execQuery("SELECT project, file FROM eclipse_ProjectTable WHERE project = '"
						+ project + "';")) {
			return null;
		}
		try {
			CachedRowSetImpl crs = new CachedRowSetImpl();
			crs = iface.getRowSet();
			while (crs.next()) {
				watchedFiles.add(crs.getString(1));
			}
		} catch (SQLException se) {
			se.printStackTrace();
		}
		return watchedFiles;
	}
	
	/**
	 * Returns a list of files that have been modified by others
	 * @param project the project
	 * @return the files that have been modified by others
	 */
	public static List<String> getUsersModifyingFiles(String project) {

		List<String> watchedFiles = new ArrayList<String>();

		if (!iface
				.execQuery("SELECT file FROM eclipse_ProjectTable WHERE project = '"
						+ project
						+ "' AND username != '"
						+ System.getProperty("user.name") + "';")) {
			return null;
		}
		try {
			CachedRowSetImpl crs = new CachedRowSetImpl();
			crs = iface.getRowSet();
			while (crs.next()) {
				watchedFiles.add(crs.getString(1));
			}
		} catch (SQLException se) {
			se.printStackTrace();
		}
		return watchedFiles;
	}
}