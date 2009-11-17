package net.sourceforge.synergy.actions;

import java.net.URL;

import net.sourceforge.synergy.Constants;
import net.sourceforge.synergy.Synergy;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.actions.ActionDelegate;
import org.eclipse.ui.browser.IWorkbenchBrowserSupport;

/**
 * Implements the launch of an internal browser with the current selection.
 */

public class WebSearchAction extends ActionDelegate implements
		IEditorActionDelegate {

	/**
	 * Create a new EditorPopupAction object.
	 */
	public WebSearchAction() {
	}

	/**
	 * @see IEditorActionDelegate#setActiveEditor(IAction, IEditorPart)
	 */
	public void setActiveEditor(IAction action, IEditorPart targetEditor) {
	}

	/**
	 * @see ActionDelegate#run(IAction)
	 */
	public void run(IAction action) {

		IWorkbenchWindow window = Synergy.getDefault()
				.getActiveWorkbenchWindow();

		if (window == null) {
			return;
		}

		/* get the selected element(s) */
		ISelection selection = window.getSelectionService().getSelection();
		if (selection instanceof ITextSelection) {
			ITextSelection ts = (ITextSelection) selection;
			String text = ts.getText();

			/* create the browser support */
			IWorkbenchBrowserSupport browserSupport = window.getWorkbench()
					.getBrowserSupport();

			/*
			 * This can be configured for external launch, too: IWebBrowser
			 * browser = browserSupport.getExternalBrowser();
			 * browser.openURL(myURL);
			 */

			try {

				/* substitute the '%s' for our actual query string */
				URL myURL = new URL(Synergy.getDefault()
						.getPreferenceStore().getString(
								Constants.SEARCH_STRING).replace("%s", text));
				browserSupport.createBrowser(
						IWorkbenchBrowserSupport.AS_EDITOR, "yeah",
						"Search Results", "myId").openURL(myURL);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
