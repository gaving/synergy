package net.sourceforge.synergy.actions;

import net.sourceforge.synergy.Dialogs;
import net.sourceforge.synergy.Synergy;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.actions.ActionDelegate;
import org.eclipse.ui.texteditor.ITextEditor;

/**
 * Handles the "send code" context action.
 */

public class SendCodePopupAction extends ActionDelegate implements
		IEditorActionDelegate {

	public SendCodePopupAction() {
	}

	/**
	 * @see IEditorActionDelegate#setActiveEditor(IAction, IEditorPart)
	 */
	public void setActiveEditor(IAction action, IEditorPart targetEditor) {
	}
	
	/**
	 * Inserts text at the current cursor position
	 *
	 * @param String the text
	 */
	public static void insertText(String text) {
		IWorkbenchWindow window = Synergy.getDefault()
				.getActiveWorkbenchWindow();

		IWorkbenchPage pg = window.getActivePage();
		ITextEditor editor = (ITextEditor) pg.getActiveEditor();
		IDocument doc = editor.getDocumentProvider().getDocument(
				editor.getEditorInput());
		ISelection sel = editor.getSelectionProvider().getSelection();

		/* move the cursor to before the end of the new insert */
		int offset = ((ITextSelection) sel).getOffset();
		int length = ((ITextSelection) sel).getLength();

		try {
			doc.replace(offset, length, text);
		} catch (Exception e) {
			e.printStackTrace();
		}

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

		/* find the developer view */
		IViewPart NewInstDevView = Synergy.getDefault().getWorkbench()
				.getActiveWorkbenchWindow().getActivePage().findView(
						"net.sourceforge.synergy.views.DeveloperView");

		/* check to see if we're currently in a connected state */
		if (!ElvinConsumerAction.connected) {
			Dialogs.openError("Not Connected!",
					"You need to first connect to use this feature.");
			
		/* check to see if the dev view is visible */
		} else if (NewInstDevView == null) {
			Dialogs
					.openError("View hidden!",
							"Please open the developer view to use this feature.");
		} else {
			
			/* get the selected text */
			ISelection selection = window.getSelectionService().getSelection();
			if (selection instanceof ITextSelection) {
				ITextSelection ts = (ITextSelection) selection;
				String text = ts.getText();

				/* call the send code dialog */
				Dialogs.sendCode(text);
			}
		}
	}
}
