package net.sourceforge.synergy.linevisactions;

import java.util.ConcurrentModificationException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import net.sourceforge.synergy.UtilityFunctions;

import org.eclipse.swt.widgets.Display;

/**
 * Handles the management of updating the various lines regarding the line
 * visualiser.
 */
public class CodeWatcher implements Runnable {

	private Thread t;

	public static EditorBook allPages;

	private static EditorLine theLine;

	private boolean runningThread = true;

	private static int previousCaretPosition = 0;

	private static Set<Integer> changes = new HashSet<Integer>();

	public CodeWatcher() {
		try {
			t = new Thread(this, "Code Watcher");
			allPages = new EditorBook();
			EditorFunctions.setEditorWindow();
			t.start();
		} catch (Exception e) {
		}
	}

	/**
	 * Updates a particular lines caret
	 * 
	 * @param linenum
	 *            the line to be updated
	 */
	public static void updateCaretLine(int linenum) {
		changes.add(linenum);
	}

	/**
	 * Updates a particular line
	 */
	public static void updateLine() {
		EditorLine theLine;
		theLine = allPages.page(UtilityFunctions.getCurrentFilePath()).line(
				previousCaretPosition);
		theLine.numofchanges += 1;
		theLine.code = EditorFunctions.getTextfromLine(previousCaretPosition);
	}

	/**
	 * Compares the line changes with the current page
	 * 
	 * @return <code>true</code> or <code>false</code> depending on success
	 */
	public static boolean checkForChanges() {
		return (EditorFunctions.getTextfromLine(previousCaretPosition)
				.equals(allPages.page(UtilityFunctions.getCurrentFilePath())
						.line(previousCaretPosition).code));
	}

	/**
	 * Returns the active editor page
	 * 
	 * @return the active editor page
	 */
	public EditorPage getActivePage() {
		return allPages.page(UtilityFunctions.getCurrentFilePath());
	}

	/**
	 * Destroys the code watcher
	 */
	public void destroy() {
		runningThread = false;
		t = null;
	}

	/**
	 * Draws the entire graph in relation to the page
	 * 
	 * @param page
	 *            the associated page to draw
	 */
	public void drawFullGraph(EditorPage page) {

		Graph.setNumberofLines(EditorFunctions.getDocumentLineCount());
		Iterator elements = page.lines.iterator();
		while (elements.hasNext()) {
			final Object next = elements.next();
			if (next instanceof EditorLine) {
				try {
				Runnable t1 = new Runnable() {
					public void run() {
						Graph.drawSingleBar((EditorLine) next);
					}
				};
					Display.getDefault().syncExec(t1);
				
				} catch (NullPointerException npe) { System.out.println("Caught one");}
			}
			continue;
		}
	}

	/**
	 * Clears a lines changes
	 */
	private void removeChanges() {
		try {
			Iterator it = changes.iterator();
			while (it.hasNext()) {
				Object element = it.next();
				allPages.page(UtilityFunctions.getCurrentFilePath()).lines
						.remove(((Integer) element) + 1);
				changes.remove(element);
			}
		} catch (ConcurrentModificationException ex) {
		}
	}

	/**
	 * Adds a lines changes
	 */
	private void addChanges() {
		try {
			Iterator it = changes.iterator();
			while (it.hasNext()) {
				Object element = it.next();

				theLine = new EditorLine();
				theLine.numofchanges = 1;
				theLine.code = EditorFunctions
						.getTextfromLine((Integer) element);
				allPages.page(UtilityFunctions.getCurrentFilePath()).lines.add(
						((Integer) element) + 1, theLine);

				Display.getDefault().syncExec(new Runnable() {
					public void run() {
						Graph.drawSingleBar(theLine);
					}
				});
				changes.remove(element);
			}
		} catch (ConcurrentModificationException ex) {
		} catch (NullPointerException npe) { System.out.println("Caught one");}
	}

	/**
	 * Draws a single change
	 */
	private void singleLineChange() {
		try {
			Iterator it = changes.iterator();
			while (it.hasNext()) {
				Object element = it.next();

				if (((Integer) element) != EditorFunctions.getCaretLine()) {

					theLine = allPages.page(
							UtilityFunctions.getCurrentFilePath()).line(
							(Integer) element);
					theLine.numofchanges += 1;
					theLine.code = EditorFunctions
							.getTextfromLine((Integer) element);
					changes.remove(element);
					Display.getDefault().syncExec(new Runnable() {
						public void run() {
							Graph.drawSingleBar(theLine);
						}
					});
				}
			}
		} catch (ConcurrentModificationException ex) {
		} catch (NullPointerException npe) { System.out.println("Caught one");}
	}

	public void run() {

		try {
			int currentNumberofLines = EditorFunctions.getDocumentLineCount();
			int previousNumberofLines = EditorFunctions.getDocumentLineCount();

			while (runningThread) {
				currentNumberofLines = EditorFunctions.getDocumentLineCount();
				previousNumberofLines = Graph.getNumberofLines();

				Display.getDefault().syncExec(new Runnable() {
					public void run() {
						EditorBook
								.setCurrentPage(allPages
										.findPage(UtilityFunctions
												.getCurrentFilePath()));
					}
				});

				if (currentNumberofLines != previousNumberofLines) {
					if (currentNumberofLines > previousNumberofLines) {
						addChanges();
					} else {
						removeChanges();
					}
					Graph.setNumberofLines(currentNumberofLines);
					previousNumberofLines = currentNumberofLines;

					Display.getDefault().syncExec(new Runnable() {
						public void run() {
							Graph.clearCanvas();
						}
					});
					drawFullGraph(allPages.page(UtilityFunctions
							.getCurrentFilePath()));
				}
				if (currentNumberofLines == previousNumberofLines) {
					singleLineChange();
				}

			}
			Thread.sleep(500);

		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (NullPointerException npe) { System.out.println("Caught one");}

	}
}
