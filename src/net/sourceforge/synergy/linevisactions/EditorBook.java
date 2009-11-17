package net.sourceforge.synergy.linevisactions;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Maintains a collection of editor pages 
 */
public class EditorBook {

	private ArrayList<EditorPage> openpages = new ArrayList<EditorPage>();
	private static EditorPage currentPage;
	
	public EditorBook() {
	}

	/**
	 * Returns the currently active page
	 * @return the currently active page
	 */
	public static EditorPage getCurrentPage()	{
		return currentPage;
	}
	
	/**
	 * Sets the current page
	 * @param page the page to set
	 */
	public static void setCurrentPage(EditorPage page)	{
		if(currentPage != page){
			Graph.clearCanvas();
		}
		currentPage =  page;
	}
	
	/**
	 * Locates and returns and active page, given a name
	 * @param pagename the pages name
	 * @return the actual editorpage object
	 */
	public EditorPage findPage(String pagename)	{
		if (!openpages.isEmpty()) {
			Iterator elements = openpages.iterator();
			while (elements.hasNext()) {
				Object next = elements.next();
				if ((next instanceof EditorPage)
						&& (((EditorPage) next).path == pagename)) {
					EditorBook.currentPage = (EditorPage) next;
					return (EditorPage) next;
				}
				continue;
			}
		}
		return null;
	}
	
	/**
	 * Returns the page for a given name
	 * @param name the pages name
	 * @return the actual editorpage object
	 */
	public EditorPage page(String name) {
		if (!openpages.isEmpty()) {
			Iterator elements = openpages.iterator();
			while (elements.hasNext()) {
				Object next = elements.next();
				if ((next instanceof EditorPage)
						&& (((EditorPage) next).path == name)) {
					EditorBook.currentPage = (EditorPage) next;
					return (EditorPage) next;
				}
				continue;
			}
		}
		EditorPage pagetoAdd = new EditorPage(name);
		openpages.add(pagetoAdd);
		EditorBook.currentPage = pagetoAdd;
		return pagetoAdd;
	}

}