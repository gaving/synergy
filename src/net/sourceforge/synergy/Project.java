package net.sourceforge.synergy;

import java.util.*;

/**
 * Describes what is considered a project and the management of its various
 * resources.
 */

public class Project {

	private String name;

	private List<String> watchedFiles, otherUsersModifyingFiles;

	/**
	 * The contructor
	 * @param name the default project name
	 */
	public Project(String name) {
		this.name = name;
	}
	
	/**
	 * The contructor
	 * @param name the project name
	 * @param watchedFiles files currently 'watched'
	 * @param otherUsersModifyingFiles files being modified by other users
	 */
	public Project(String name, List<String> watchedFiles,
			List<String> otherUsersModifyingFiles) {
		this.name = name;
		this.watchedFiles = watchedFiles;
		this.otherUsersModifyingFiles = otherUsersModifyingFiles;
	}
	
	/**
	 * Retrieves a projects name.
	 * 
	 * @return the projects name.
	 */
	public String getName() {
		return this.name;
	}
	
	/**
	 * Retrieves files modified by many users.
	 * 
	 * @return other users modifying this projects files.
	 */
	public List<String> getOtherUsersModifyingFiles() {
		return this.otherUsersModifyingFiles;
	}
	
	/**
	 * Retrieves files exclusive to the current user.
	 * 
	 * @return files only modified by the current user.
	 */
	public List<String> getWatchedFiles() {
		return this.watchedFiles;
	}
}
