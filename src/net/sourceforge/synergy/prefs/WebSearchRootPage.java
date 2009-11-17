package net.sourceforge.synergy.prefs;

import net.sourceforge.synergy.Synergy;

import org.eclipse.jface.preference.IPreferenceStore;

import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;


/**
 * For the web search preferences page
 */

public class WebSearchRootPage extends WebSearchPage implements IWorkbenchPreferencePage {
	public WebSearchRootPage() {
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
