package net.sourceforge.synergy.views;

import net.sourceforge.synergy.Developer;

import org.eclipse.jface.viewers.*;

/**
 * Translates the viewers input
 */

public class DeveloperContentProvider implements IStructuredContentProvider,
		ITreeContentProvider, DeveloperFile.Listener {
	private DeveloperFile input;

	private TreeViewer viewer;

	/**
	 * @see IStructuredContentProvider#getElements(Object)
	 */
	public Object[] getElements(Object element) {
		if (element == input)
			return input.elements().toArray();
		return new Object[0];
	}

	public Object getParent(Object child) {
		return null;
	}

	public Object[] getChildren(Object parent) {
		return new Object[0];
	}

	public boolean hasChildren(Object parent) {
		return false;
	}

	/**
	 * @see IContentProvider#dispose()
	 */
	public void dispose() {
		if (input != null)
			input.setListener(null);
		input = null;
		viewer = null;
	}

	/**
	 * @see IContentProvider#inputChanged(Viewer, Object, Object)
	 */
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		if (viewer instanceof TreeViewer) {
			this.viewer = (TreeViewer) viewer;
		}
		if (newInput instanceof DeveloperFile) {
			input = (DeveloperFile) newInput;
			input.setListener(this);
		}
	}

	/**
	 * @see Listener#added()
	 */
	public void added(Developer e) {
		if (viewer != null) {
			viewer.add(new Object(), e);
			viewer.refresh(false);
		}
	}

	/**
	 * @see Listener#removed()
	 */
	public void removed(Developer e) {
		if (viewer != null) {
			viewer.setSelection(null);
			viewer.remove(e);
		}
	}
}
