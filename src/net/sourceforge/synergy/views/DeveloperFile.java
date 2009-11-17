package net.sourceforge.synergy.views;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import net.sourceforge.synergy.Developer;

/**
 * Stores all developer information for the view
 */

public class DeveloperFile {

	private File file;

	private List<Developer> list = new ArrayList<Developer>();

	private Listener listener;

	public interface Listener {
		public void added(Developer w);

		public void removed(Developer w);
	}

	/**
	 * Constructor for FileList
	 */
	public DeveloperFile(File file) {
		this.file = file;
		if (file.exists()) {
			readFile();
		} else {
			writeFile();
		}
	}

	/**
	 * Sets the listener
	 * 
	 * @param l
	 *            the listener
	 */
	public void setListener(Listener l) {
		listener = l;
	}

	/**
	 * Adds a developer to the list and file
	 * 
	 * @param dev
	 *            the developer to add
	 */
	public void add(Developer dev) {
		list.add(dev);
		sort();
		writeFile();
		if (listener != null)
			listener.added(dev);
	}

	/**
	 * Removes a developer from the list and file
	 * 
	 * @param dev
	 *            the developer to remove
	 */
	public void remove(Developer dev) {
		list.remove(dev);
		sort();
		writeFile();
		if (listener != null)
			listener.removed(dev);
	}

	/**
	 * Finds a given developer in the file
	 * 
	 * @param str
	 *            the developer to find
	 * @return the located developer
	 */
	public Developer find(String str) {
		Iterator iter = list.iterator();
		while (iter.hasNext()) {
			Developer developer = (Developer) iter.next();
			if (str.equals(developer.toString()))
				return developer;
		}
		return null;
	}

	/**
	 * Sorts a developer list by status, then name.
	 */
	private void sort() {
		Collections.sort(elements(), new Comparator<Developer>() {
			public int compare(Developer d1, Developer d2) {

				/* return by status, then alphabetical order */
				return ((d1.getOnlineStatus() && !d2.getOnlineStatus()) ? -1
						: ((d1.getOnlineStatus() && d2.getOnlineStatus()) ? d1
								.getName().compareToIgnoreCase(d2.getName())
								: 1));
			}
		});
	}

	/**
	 * Retrieves a list of current developers
	 * 
	 * @return the developer list
	 */
	public List<Developer> elements() {
		return list;
	}

	/**
	 * Writes out a developer list to file.
	 */
	private void writeFile() {
		try {
			OutputStream os = new FileOutputStream(file);
			DataOutputStream data = new DataOutputStream(os);
			data.writeInt(list.size());
			Iterator iter = list.iterator();
			while (iter.hasNext()) {
				Developer developer = (Developer) iter.next();
				data.writeUTF(developer.toString());
			}
			data.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Reads a developer list from file to collection.
	 */
	private void readFile() {
		try {
			InputStream is = new FileInputStream(file);
			DataInputStream data = new DataInputStream(is);
			int size = data.readInt();
			for (int nX = 0; nX < size; nX++) {
				String str = data.readUTF();
				list.add(new Developer(str));
			}
			data.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}