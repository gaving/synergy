package net.sourceforge.synergy.linevisactions;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Describes what is considered an editor page
 */

public class EditorPage {

	public String path;

	public ArrayList<EditorLine> lines;

	protected EditorPage(String page) {
		this.path = page;
		lines = new ArrayList<EditorLine>();
		lines.add(new EditorLine("never used.", 0)); // no such thing as line 0
		addLinestoPage(); // Add the lines from the editor window to this editor page
	}

	/**
	 * Locates a line number for a given editorline
	 * @param line the editor line
	 * @return the actual line number
	 */
	public int findLine(EditorLine line) {
		int count = 0;
		if (!lines.isEmpty()) {
			Iterator elements = lines.iterator();
			while (elements.hasNext()) {
				Object next = elements.next();
				if ((next instanceof EditorLine)
						&& (((EditorLine) next) == line)) {
					return count;
				}
				count++;
				continue;
			}
		}
		return -1;
	}

	/**
	 * Returns the equivalent editorline object for a given line number
	 * @param number the line number
	 * @return the actual editorline object
	 */
	public EditorLine line(int number) {
		try {
			return this.lines.get(number);
		} catch (IndexOutOfBoundsException e) {
			// happens when the editor is closed before the thread	
		}
		return null;

	}

	/**
	 * Adds a number of associated lines to a page
	 */
	private void addLinestoPage() {
		for (int range = 1; range <= EditorFunctions.getDocumentLineCount(); range++) {
			this.lines.add(new EditorLine(EditorFunctions
					.getTextfromLine(range), 1));
		}
	}
}