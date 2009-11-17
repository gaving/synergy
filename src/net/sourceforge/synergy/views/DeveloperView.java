package net.sourceforge.synergy.views;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;

import net.sourceforge.synergy.Developer;
import net.sourceforge.synergy.Dialogs;
import net.sourceforge.synergy.Constants;
import net.sourceforge.synergy.Synergy;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.part.WorkbenchPart;

/**
 * A DeveloperView shows a bunch of words representing developer names.
 */

public class DeveloperView extends ViewPart {

	private DeveloperFile input;

	private IMemento memento;

	private String server = Synergy.getDefault().getPreferenceStore()
			.getString(Constants.ELVIN_SERVER);

	public TreeViewer viewer;

	public Action addItemAction, deleteItemAction, selectAllAction,
			infoItemAction;

	/**
	 * Constructor
	 */
	public DeveloperView() {
		super();
		input = new DeveloperFile(new File(Synergy.getDefault().getHomeDir()
				+ ".eclipse_developers.txt"));

		/* add the server to the list */
		if (input.find(server) == null) {
			input.add(new Developer(server));
		}
	}

	/**
	 * Returns a developer file associated with the view
	 * @return the developer file
	 */
	public DeveloperFile getDeveloperFile() {
		return input;
	}

	/**
	 * @see IViewPart.init(IViewSite)
	 */
	public void init(IViewSite site) throws PartInitException {
		super.init(site);
	}

	/**
	 * Initializes this view with the given view site. A memento is passed to
	 * the view which contains a snapshot of the views state from a previous
	 * session.
	 */
	public void init(IViewSite site, IMemento memento) throws PartInitException {
		init(site);
		this.memento = memento;
	}

	/**
	 * @see IWorkbenchPart#createPartControl(Composite)
	 */
	public void createPartControl(Composite parent) {

		/* create viewer */
		viewer = new TreeViewer(parent);
		
		/* set providers */
		viewer.setContentProvider(new DeveloperContentProvider());
		viewer.setLabelProvider(new DeveloperLabelProvider());
		viewer.setInput(input);

		getSite().setSelectionProvider(viewer);

		/* create menu and toolbars */
		createActions();
		createMenu();
		createToolbar();
		createContextMenu();
		hookGlobalActions();

		/* restore state from the previous session */
		restoreState();

	}

	public void refreshView() {
		viewer.refresh();
	}

	/**
	 * @see WorkbenchPart#setFocus()
	 */
	public void setFocus() {
		viewer.getControl().setFocus();
	}

	/**
	 * Create the actions.
	 */
	public void createActions() {
		deleteItemAction = new Action("Delete") {
			public void run() {
				deleteItem();
			}
		};
		deleteItemAction.setImageDescriptor(Synergy
				.getImageDescriptor("/icons/buddylist_delete.gif"));

		infoItemAction = new Action("Info..") {
			public void run() {
				viewItem();
			}
		};
		infoItemAction.setImageDescriptor(Synergy
				.getImageDescriptor("/icons/buddylist_info.gif"));

		selectAllAction = new Action("Select All") {
			public void run() {
				selectAll();
			}
		};

		viewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent e) {

				IStructuredSelection selection = (IStructuredSelection) viewer
						.getSelection();

				if (selection != null) {

					if (selection instanceof IStructuredSelection) {
						IStructuredSelection ss = (IStructuredSelection) selection;

						if (!ss.isEmpty()) {

							/* send the selected name to the conversation view. */
							IViewPart InstChatView = getViewSite()
									.getPage()
									.findView(
											"net.sourceforge.synergy.views.ChatView");

							String selectedelement = ss.getFirstElement()
									.toString();

							if (InstChatView == null) {

								/* attempt to open the conversation view */
								try {
									getViewSite()
											.getPage()
											.showView(
													"net.sourceforge.synergy.views.ChatView");

									IViewPart NewInstChatView = getViewSite()
											.getPage()
											.findView(
													"net.sourceforge.synergy.views.ChatView");

									((ChatView) (NewInstChatView))
											.createNewConvTab(selectedelement);
									getViewSite().getPage().activate(
											NewInstChatView);

								} catch (PartInitException ex) {
									System.out
											.println("Unable to initialise the conversation view!");
									ex.printStackTrace();
								}
							} else {

								/* view is already open */
								((ChatView) (InstChatView))
										.createNewConvTab(selectedelement);
								getViewSite().getPage().activate(InstChatView);
							}
						}

					}
				}
				updateActionEnablement();
			}
		});

		/* add selection listener. */
		viewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				updateActionEnablement();
			}
		});

	}

	/**
	 * Create menu.
	 */
	private void createMenu() {
		IMenuManager mgr = getViewSite().getActionBars().getMenuManager();
		mgr.add(selectAllAction);
	}

	/**
	 * Create toolbar.
	 */
	private void createToolbar() {
		IToolBarManager mgr = getViewSite().getActionBars().getToolBarManager();
		mgr.add(deleteItemAction);
		mgr.add(infoItemAction);
	}

	/**
	 * Create context menu.
	 */
	private void createContextMenu() {

		/* create menu manager. */
		MenuManager menuMgr = new MenuManager();
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager mgr) {
				fillContextMenu(mgr);
			}
		});

		/* create menu. */
		Menu menu = menuMgr.createContextMenu(viewer.getControl());
		viewer.getControl().setMenu(menu);

		/* register menu for extension. */
		getSite().registerContextMenu(menuMgr, viewer);
	}

	/**
	 * Hook global actions
	 */
	private void hookGlobalActions() {
		IActionBars bars = getViewSite().getActionBars();
		bars.setGlobalActionHandler(ActionFactory.SELECT_ALL.getId(),
				selectAllAction);
		bars.setGlobalActionHandler(ActionFactory.DELETE.getId(),
				deleteItemAction);
		viewer.getControl().addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent event) {
				if (event.character == SWT.DEL && event.stateMask == 0
						&& deleteItemAction.isEnabled()) {
					deleteItemAction.run();
				}
			}
		});
	}

	/**
	 * Fill context menu
	 * @param mgr the menu to fill
	 */
	private void fillContextMenu(IMenuManager mgr) {
		mgr.add(new GroupMarker(IWorkbenchActionConstants.MB_ADDITIONS));
		mgr.add(deleteItemAction);
		mgr.add(new Separator());
		mgr.add(infoItemAction);
		mgr.add(new Separator());
		mgr.add(selectAllAction);
	}

	/**
	 * Update action enablement
	 */
	private void updateActionEnablement() {
		IStructuredSelection sel = (IStructuredSelection) viewer.getSelection();
		deleteItemAction.setEnabled(sel.size() > 0);
	}

	/**
	 * Remove a developer from the list.
	 */
	private void deleteItem() {
		IStructuredSelection sel = (IStructuredSelection) viewer.getSelection();
		Iterator iter = sel.iterator();
		while (iter.hasNext()) {
			Developer developer = (Developer) iter.next();
			if (!developer.getName().equals(server)) {
				input.remove(developer);
			} else {
				
				/* can't delete the server from the list! */
				Dialogs
						.openError("Error",
								"Cannot delete global elvin server!");
			}
		}
	}

	/**
	 * View a developers info from a list.
	 */
	private void viewItem() {
		IStructuredSelection sel = (IStructuredSelection) viewer.getSelection();
		Iterator iter = sel.iterator();

		while (iter.hasNext()) {
			final Developer developer = (Developer) iter.next();

			/* check if it's the server we've clicked or just a user */
			if (!developer.getName().equals(server)) {
				Dialogs.viewDeveloper(developer);
			}
			/* special server dialog */
			else {
				Dialogs.viewServer(developer);
			}
		}
	}

	/**
	 * Select all items.
	 */
	private void selectAll() {
		viewer.getTree().selectAll();
		updateActionEnablement();
	}

	/**
	 * Saves the object state within a memento.
	 */
	public void saveState(IMemento memento) {
		IStructuredSelection sel = (IStructuredSelection) viewer.getSelection();
		if (sel.isEmpty())
			return;
		memento = memento.createChild("selection");
		Iterator iter = sel.iterator();
		while (iter.hasNext()) {
			Developer developer = (Developer) iter.next();
			memento.createChild("descriptor", developer.toString());
		}
	}

	/**
	 * Restores the viewer state from the memento.
	 */
	private void restoreState() {
		if (memento == null)
			return;
		memento = memento.getChild("selection");
		if (memento != null) {
			IMemento descriptors[] = memento.getChildren("descriptor");
			if (descriptors.length > 0) {
				ArrayList<Developer> objList = new ArrayList<Developer>(
						descriptors.length);
				for (int nX = 0; nX < descriptors.length; nX++) {
					String id = descriptors[nX].getID();
					Developer developer = input.find(id);
					if (developer != null)
						objList.add(developer);
				}
				viewer.setSelection(new StructuredSelection(objList));
			}
		}
		memento = null;
		updateActionEnablement();
	}
}