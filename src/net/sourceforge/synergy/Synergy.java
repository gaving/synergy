package net.sourceforge.synergy;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.sourceforge.synergy.actions.ElvinConsumerAction;
import net.sourceforge.synergy.data.DataUpdate;
import net.sourceforge.synergy.data.SQLInterface;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.action.SubStatusLineManager;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The main plugin class to be used in the workbench.
 */
public class Synergy extends AbstractUIPlugin {

	public interface StatusBar {

		/**
		 * Sets the text to display.
		 * 
		 * @param text text to display.
		 */
		public void setText(String text);
	}

	private static Synergy plugin;

	private StatusBar statusBar;

	private SQLInterface iface;

	/**
	 * The constructor.
	 */
	public Synergy() {
		plugin = this;
		this.statusBar = new EclipseStatusBar();
	}

	/**
	 * This method is called upon plug-in activation
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);

		/* mysql information from prefs */
		String db_hostname = Synergy.getDefault().getPreferenceStore()
				.getString(Constants.MYSQL_SERVER);

		String db_database = Synergy.getDefault().getPreferenceStore()
				.getString(Constants.MYSQL_DATABASE);

		String db_username = Synergy.getDefault().getPreferenceStore()
				.getString(Constants.MYSQL_USERNAME);

		String db_password = Synergy.getDefault().getPreferenceStore()
				.getString(Constants.MYSQL_PASSWORD);

		/* interface with the mysql database */
		iface = new SQLInterface(db_hostname, db_database, db_username,
				db_password);

		/* if we're not connected end this entire process */
		if (iface.getCon() == null)
			return;

		IWorkspace workspace = getWorkspace();

		/* add resource listener to check for any changes */
		IResourceChangeListener rc1 = new IResourceChangeListener() {
			public void resourceChanged(IResourceChangeEvent event) {

				/* only interested in POST_CHANGE events */
				if (event.getType() != IResourceChangeEvent.POST_CHANGE)
					return;

				/* we're interested in any resource over the workspace */
				IResourceDelta rootDelta = event.getDelta();
				if (rootDelta == null)
					return;

				/* visit the file in question and get its information */
				IResourceDeltaVisitor visitor = new IResourceDeltaVisitor() {
					public boolean visit(IResourceDelta delta) {

						IResource resource = delta.getResource();

						/* not interested in files with a class extension */
						if (resource.getType() == IResource.FILE
								&& !"class".equalsIgnoreCase(resource
										.getFileExtension())) {

							String resourceName = resource.getName();

							switch (delta.getKind()) {

							case IResourceDelta.ADDED:
								logInfo("Database: Adding '" + resourceName
										+ "'");
								DataUpdate.insertFile(resource);
								break;

							case IResourceDelta.CHANGED:
								/* TODO: Sort out why this fires twice */
								logInfo("Database: Updating '" + resourceName
										+ "'");
								DataUpdate.insertFile(resource);
								break;

							case IResourceDelta.REMOVED:
								logInfo("Database: Removing '" + resourceName
										+ "'");
								DataUpdate.removeFile(resource);
								break;

							}
						}
						return true;
					}
				};
				try {
					rootDelta.accept(visitor);
				} catch (CoreException e) {

					/* log any errors that occur */
					logError(
							"An error occurred when grabbing a changed resource.",
							e);
				}
			}
		};

		workspace.addResourceChangeListener(rc1);
	}

	/**
	 * This method is called when the plug-in is stopped
	 */
	public void stop(BundleContext context) throws Exception {

		try {
			
			/* set the connected state to false */
			ElvinConsumerAction.connected = false;

			/* stop the elvin related pulse thread */
			SendPulse.stopPulseThread();

			/* close connection to the database */
			iface.destroy();
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			super.stop(context);
		}
		plugin = null;
	}

	/**
	 * Returns the shared instance.
	 */
	public static Synergy getDefault() {
		return plugin;
	}

	/**
	 * Returns the statusbar instance.
	 * 
	 * @return the statusbar instance
	 */
	public StatusBar getStatusBar() {
		return this.statusBar;
	}

	/**
	 * Returns the workspace instance.
	 * 
	 * @return the workspace instance
	 */
	public IWorkspace getWorkspace() {
		return ResourcesPlugin.getWorkspace();
	}

	/**
	 * Returns the shared database.
	 * 
	 * @return the shared database
	 */
	public SQLInterface getDatabase() {
		return this.iface;
	}

	/**
	 * Returns the users home directory.
	 * 
	 * @return the home directory
	 */
	public String getHomeDir() {
		/* TODO: Make this platform independant */
		return System.getProperty("user.home") + "/";
	}

	/**
	 * Sets up default preferences.
	 */
	protected void initializeDefaultPreferences(IPreferenceStore store) {
		store.setDefault(Constants.ELVIN_SERVER, "coldwired.com");
		store.setDefault(Constants.MYSQL_SERVER, "coldwired.com");
		store.setDefault(Constants.MYSQL_USERNAME, "gavin");
		store.setDefault(Constants.MYSQL_PASSWORD, "#");
		store.setDefault(Constants.MYSQL_DATABASE, "plugin");
		store.setDefault(Constants.SEARCH_STRING,
				"http://www.google.co.uk/search?q=");
	}

	/**
	 * Handles the plugin logging events.
	 * 
	 * @param status new status object
	 */
	public void log(IStatus status) {
		plugin.getLog().log(status);
	}

	/**
	 * Logs an error.
	 * 
	 * @param message the error to display
	 * @param exception the associated exception with the message
	 */
	public void logError(String message, Throwable exception) {
		log(new Status(IStatus.ERROR, "net.sourceforge.synergy", 0, message,
				exception));
	}

	/**
	 * Logs an information message.
	 * 
	 * @param message the information to display
	 */
	public void logInfo(String message) {
		log(new Status(IStatus.INFO, "net.sourceforge.synergy", 0, message,
				new Throwable()));
	}

	/**
	 * Logs an error.
	 * 
	 * @param message the message to display
	 * @param exception the associated exception with the message
	 */
	public void logWarning(String message) {
		log(new Status(IStatus.WARNING, "net.sourceforge.synergy", 0,
				message, new Throwable()));
	}

	/**
	 * Provides access to the eclipse status bar.
	 */
	private static class EclipseStatusBar implements StatusBar {
		public void setText(final String message) {
			Display.getDefault().syncExec(new Runnable() {
				public void run() {
					IStatusLineManager statusLine = plugin.getStatusLine();
					if (statusLine != null) {
						statusLine.setMessage(message);
						statusLine.update(true);
					}
				}
			});
		}
	}

	/**
	 * Returns an image descriptor for the image file at the given plug-in
	 * relative path.
	 * 
	 * @param path the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return AbstractUIPlugin.imageDescriptorFromPlugin(
				"net.sourceforge.synergy", path);
	}

	/**
	 * Returns the active workbench instance.
	 * 
	 * @return the active workbench instance
	 */
	public IWorkbenchWindow getActiveWorkbenchWindow() {
		IWorkbenchWindow window = getWorkbench().getActiveWorkbenchWindow();

		if (window == null) {
			final Visibility visibility = new Visibility();
			IWorkbenchWindow[] windows = plugin.getWorkbench()
					.getWorkbenchWindows();

			for (int i = 0; i < windows.length; i++) {
				window = windows[i];

				final Shell shell = window.getShell();

				if (shell != null) {
					Display.getDefault().syncExec(new Runnable() {
						public void run() {
							if (shell.isVisible()) {
								visibility.visible = true;
							}
						}
					});

					if (visibility.visible) {
						return window;
					}
				}
			}
		}

		return window;
	}

	/**
	 * Returns the plugin status line.
	 * 
	 * @return the status line manager
	 */
	protected IStatusLineManager getStatusLine() {
		IWorkbenchWindow window = plugin.getActiveWorkbenchWindow();
		IWorkbenchPage[] pages = window.getPages();

		for (int i = 0; i < pages.length; i++) {
			IViewReference[] references = pages[i].getViewReferences();

			for (int j = 0; j < references.length; j++) {
				IViewPart view = references[j].getView(true);

				if (view == null) {
					continue;
				}

				IActionBars bars = view.getViewSite().getActionBars();
				IStatusLineManager statusLineManager = bars
						.getStatusLineManager();

				if (statusLineManager instanceof SubStatusLineManager) {
					statusLineManager = (IStatusLineManager) ((SubStatusLineManager) statusLineManager)
							.getParent();
				}

				return statusLineManager;
			}
		}

		return null;
	}

	/**
	 * Returns the current opened projects.
	 * 
	 * @return the current opened projects
	 */
	public IProject[] getOpenProjects() throws CoreException {
		List<IProject> projectList = new ArrayList<IProject>();

		IProject[] projects = this.getWorkspace().getRoot().getProjects();
		for (int i = 0; i < projects.length; ++i) {
			projectList.add(projects[i]);
		}

		IProject[] sharedProjects = new IProject[projectList.size()];
		for (int i = 0; i < projectList.size(); ++i) {
			sharedProjects[i] = (IProject) projectList.get(i);
		}

		return sharedProjects;
	}

	/**
	 * Returns the current dirty files.
	 * 
	 * @return the current dirty files
	 * @throws CoreException if any error occurs
	 * 
	 */
	public IProject[] getCurrentDirtyFiles() throws CoreException {
		List<IProject> projectList = new ArrayList<IProject>();

		IProject[] projects = this.getWorkspace().getRoot().getProjects();
		for (int i = 0; i < projects.length; ++i) {
			projectList.add(projects[i]);
		}

		IProject[] sharedProjects = new IProject[projectList.size()];
		for (int i = 0; i < projectList.size(); ++i) {
			sharedProjects[i] = (IProject) projectList.get(i);
		}
		return sharedProjects;
	}

	/**
	 * Returns the current dirty files.
	 * 
	 * @return the current dirty files
	 */
	protected IResource[] getSelectedResources(ISelection selection) {
		ArrayList<Object> resources = null;
		if (!selection.isEmpty()) {
			resources = new ArrayList<Object>();
			Iterator elements = ((IStructuredSelection) selection).iterator();
			while (elements.hasNext()) {
				Object next = elements.next();
				if (next instanceof IResource) {
					resources.add(next);
					continue;
				}
				if (next instanceof IAdaptable) {
					IAdaptable a = (IAdaptable) next;
					Object adapter = a.getAdapter(IResource.class);
					if (adapter instanceof IResource) {
						resources.add(adapter);
						continue;
					}
				}
			}
		}
		if (resources != null && !resources.isEmpty()) {
			IResource[] result = new IResource[resources.size()];
			resources.toArray(result);
			return result;
		}
		return new IResource[0];
	}

	private static class Visibility {
		boolean visible;
	}

}