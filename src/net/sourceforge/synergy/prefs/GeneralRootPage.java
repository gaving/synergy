package net.sourceforge.synergy.prefs;

import net.sourceforge.synergy.Synergy;

import org.eclipse.jface.preference.IPreferenceStore;

import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;


/**
 * For the general preferences page
 */

public class GeneralRootPage extends GeneralPage implements IWorkbenchPreferencePage {
	public GeneralRootPage() {
		IPreferenceStore store = Synergy.getDefault().getPreferenceStore();
		setPreferenceStore(store);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	public void init(IWorkbench wb) {
	}
}
