package net.sourceforge.synergy.linevisactions;

import net.sourceforge.synergy.Synergy;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentListener;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.texteditor.ITextEditor;

/**
 * Contains a collection of general functions regarding the line visualiser
 */

public class EditorFunctions {

	private static IWorkbenchWindow window;

	private static IWorkbenchPage pg;

	private static ITextEditor editor;

	private static IDocument doc;

	private static int theLine;

	/**
	 * Sets an active editor window
	 */
	public static void setEditorWindow() {
		try {
			window = Synergy.getDefault().getActiveWorkbenchWindow();
			pg = window.getActivePage();
			editor = (ITextEditor) pg.getActiveEditor();
			doc = editor.getDocumentProvider().getDocument(
					editor.getEditorInput());
			doc.addDocumentListener(new IDocumentListener() {
				public void documentChanged(DocumentEvent e) {
					try {
						CodeWatcher.updateCaretLine(doc
								.getLineOfOffset(e.fOffset) + 1);
					} catch (BadLocationException ex) {
					}
				}

				public void documentAboutToBeChanged(DocumentEvent e) {
				}
			});
		} catch (Exception e) {
		}

	}

	/**
	 * Returns the current text on a line
	 * 
	 * @param ln
	 *            the line number to retrieve text
	 * @return the actual text
	 */
	public static String getTextfromLine(int ln) {
		try {
			IRegion thetext2 = doc.getLineInformation(ln - 1);
			String outputtext = doc.get(thetext2.getOffset(), thetext2
					.getLength());
			return outputtext;
		} catch (BadLocationException ex) {
			return "";
		}
	}

	/**
	 * Returns the line caret
	 * 
	 * @return the line caret
	 */
	public static int getCaretLine() {
		try {

			Display.getDefault().asyncExec(new Runnable() {
				public void run() {
					if (editor != null) {
						ISelection selection = editor.getSelectionProvider()
								.getSelection();
						ITextSelection thetext = (ITextSelection) selection;
						theLine = thetext.getStartLine();
					}
				}
			});

		} catch (NullPointerException npe) { System.out.println("Caught one");}
		
		return (theLine + 1);
	}

	/**
	 * Returns a line count for the current document
	 * 
	 * @return the line count
	 */
	public static int getDocumentLineCount() {
		try {
			setEditorWindow();
			return doc.getNumberOfLines();
		} catch (Exception e) {
		}

		return 0;
	}

}
