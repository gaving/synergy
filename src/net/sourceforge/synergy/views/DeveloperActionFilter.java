package net.sourceforge.synergy.views;

import net.sourceforge.synergy.Developer;

import org.eclipse.ui.IActionFilter;

/**
 * Filters developer actions
 */

public class DeveloperActionFilter implements IActionFilter {

	public static final String NAME = "name";

	private static DeveloperActionFilter singleton;

	public static DeveloperActionFilter getSingleton() {
		if (singleton == null)
			singleton = new DeveloperActionFilter();
		return singleton;
	}

	/**
	 * @see IActionFilter#testAttribute(Object, String, String)
	 */
	public boolean testAttribute(Object target, String name, String value) {
		if (name.equals(NAME)) {
			Developer le = (Developer)target;
			return value.equals(le.toString());
		}
		return false;
	}
}

