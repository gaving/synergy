package net.sourceforge.synergy.prefs;

import net.sourceforge.synergy.Constants;

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.StringFieldEditor;

/**
 * For the web search preferences page
 */

public class WebSearchPage extends FieldEditorPreferencePage {
	public WebSearchPage() {
		super(FieldEditorPreferencePage.GRID);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.eclipse.jface.preference.FieldEditorPreferencePage#createFieldEditors()
	 */
	protected void createFieldEditors() {

		StringFieldEditor searchString = new StringFieldEditor(
				Constants.SEARCH_STRING, "Search string: ",
				getFieldEditorParent());
		searchString.getTextControl(getFieldEditorParent());
		addField(searchString);
	}
}
