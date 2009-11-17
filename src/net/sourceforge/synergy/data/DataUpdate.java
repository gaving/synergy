package net.sourceforge.synergy.data;

import net.sourceforge.synergy.Synergy;

import org.eclipse.core.resources.IResource;

/**
 * Handles the management of updating the resources in the shared datbase
 */
public class DataUpdate {
	
	private static SQLInterface iface = Synergy.getDefault().getDatabase();
	
	/**
	 * Inserts a given resource into the database.
	 * 
	 * @param resource the resource to insert
	 * @return <code>true</code> or <code>false</code> depending on success
	 */
	public static boolean insertFile(IResource resource) {

		String projectName = resource.getProject().getName();
		String fileName = resource.getProjectRelativePath().toString();

		if (!iface.execUpdate("INSERT INTO eclipse_ProjectTable VALUES ('"
				+ projectName + "', '" + fileName + "', '"
				+ System.getProperty("user.name") + "', NOW());")) {
			return false;
		}
		return true;
	}

	/**
	 * Removes a given resource into the database.
	 * 
	 * @param resource the resource to remove
	 * @return <code>true</code> or <code>false</code> depending on success
	 */
	public static boolean removeFile(IResource resource) {

		String projectName = resource.getProject().getName();
		String fileName = resource.getProjectRelativePath().toString();

		if (!iface
				.execUpdate("DELETE FROM eclipse_ProjectTable WHERE project='"
						+ projectName + "' AND file='" + fileName
						+ "' AND username='" + System.getProperty("user.name")
						+ "';")) {
			return false;
		}
		return true;
	}

}
