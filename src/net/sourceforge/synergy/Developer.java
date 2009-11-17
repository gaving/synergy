package net.sourceforge.synergy;

import java.io.Serializable;

import net.sourceforge.synergy.views.DeveloperActionFilter;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.ui.IActionFilter;

/**
 * Describes what is considered a developer and the management
 * of their various attributes.
 */

public class Developer implements IAdaptable, Comparable, Serializable {

	private static final long serialVersionUID = 1L;

	private String name, currentlyopenedresource;

	private long timestamp;

	private boolean online;

	/**
	 * The constructor
	 */
	public Developer(String name) {
		super();
		this.name = name;
		this.timestamp = 0;
		this.online = false;
		this.currentlyopenedresource = null;
	}

	public int compareTo(Developer dev) {
		return (this.name == dev.name) ? this.name.compareTo(dev.name) : 0;
	}

	public int compareTo(Object arg0) {
		Developer emp = (Developer) arg0;
		int deptComp = this.name.compareTo(emp.name);
		return ((deptComp == 0) ? this.name.compareTo(emp.name) : deptComp);
	}

	public Object getAdapter(Class adapter) {
		if (adapter == IActionFilter.class) {
			return DeveloperActionFilter.getSingleton();
		}
		return null;
	}
	
	/**
	 * Retrieves a currently opened resource.
	 * 
	 * @return the opened resource.
	 */
	public String getCurrentlyOpenedResource() {
		return this.currentlyopenedresource;
	}
	
	/**
	 * Retrieves a developers name.
	 * 
	 * @return the developers name.
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Retrieves a developers current status.
	 * 
	 * @return the developers status.
	 */
	public boolean getOnlineStatus() {
		return this.online;
	}

	/**
	 * Retrieves a developers current ping time.
	 * 
	 * @return the developers ping time.
	 */
	public long getTimestamp() {
		return this.timestamp;
	}
	
	/**
	 * Sets a developers online status.
	 * 
	 * @param state the developers state.
	 */
	public void setOnline(boolean state) {
		this.online = state;
	}

	/**
	 * Sets a developers currently opened resource.
	 * 
	 * @param name the currently opened resource.
	 */
	public void setCurrentlyOpenedResource(String name) {
		this.currentlyopenedresource = name;
	}

	/**
	 * Sets a developers ping time.
	 * 
	 * @param timestamp the developers last ping time.
	 */
	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	/**
	 * Toggles a developers online status.
	 */
	public void toggleOnline() {
		this.online = (this.online) ? false : true;
	}

	public String toString() {
		return name;
	}
}
