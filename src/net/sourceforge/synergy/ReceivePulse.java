package net.sourceforge.synergy;

import net.sourceforge.synergy.views.DeveloperFile;
import net.sourceforge.synergy.views.DeveloperView;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.IViewPart;

/**
 * Handles the receiving of elvin based pulses and contains related methods.
 */

public class ReceivePulse {

	private static ImageDescriptor offline = Synergy
			.getImageDescriptor("/icons/buddylist_offline.gif");

	private static ImageDescriptor online = Synergy
			.getImageDescriptor("/icons/buddylist_online.gif");

	private static ImageDescriptor offline_server = Synergy
			.getImageDescriptor("/icons/buddylist_offline_server.gif");

	private static ImageDescriptor online_server = Synergy
			.getImageDescriptor("/icons/buddylist_online_server.gif");

	private static String server = Synergy.getDefault().getPreferenceStore()
			.getString(Constants.ELVIN_SERVER);

	/**
	 * Checks if a user has went offline between pulses
	 */
	public void checkForTimeouts() {

		IViewPart NewInstDevView = Synergy.getDefault().getWorkbench()
				.getActiveWorkbenchWindow().getActivePage().findView(
						"net.sourceforge.synergy.views.DeveloperView");
		if (NewInstDevView != null) {

			Tree thetree = (((DeveloperView) (NewInstDevView)).viewer)
					.getTree();

			for (int i3 = 0; i3 < thetree.getItemCount(); i3++) {
				long selecteduserstime = ((Developer) ((((DeveloperView) (NewInstDevView)).viewer)
						.getTree().getItem(i3).getData())).getTimestamp();

				if ((System.currentTimeMillis() - selecteduserstime) > 7000) {
					Developer currentdevel = (Developer) (thetree.getItem(i3)
							.getData());

					thetree.getItem(i3).setImage(offline.createImage());
					currentdevel.setOnline(false);
				}
			}
		}
	}

	/**
	 * Sets and adds an online user to the developer list.
	 * 
	 * @param username the username to add
	 * @param activefile the users currently active file
	 */
	public void setUserOnline(String username, String activefile) {

		/* make sure nobody has timed out */
		checkForTimeouts();

		IViewPart NewInstDevView = Synergy.getDefault().getWorkbench()
				.getActiveWorkbenchWindow().getActivePage().findView(
						"net.sourceforge.synergy.views.DeveloperView");
		if (NewInstDevView != null) {

			Tree thetree = (((DeveloperView) (NewInstDevView)).viewer)
					.getTree();
			for (int i3 = 0; i3 < thetree.getItemCount(); i3++) {
				if (thetree.getItem(i3).getText().equals(username)) {

					Developer currentdevel = ((Developer) ((((DeveloperView) (NewInstDevView)).viewer)
							.getTree().getItem(i3).getData()));

					/* special case: it's the server that has responded */
					if (!username.equals(server)) {
						(((DeveloperView) (NewInstDevView)).viewer).getTree()
								.getItem(i3).setImage(online.createImage());
						/* only people can have opened resources */
						currentdevel.setCurrentlyOpenedResource(activefile);

					} else {
						(((DeveloperView) (NewInstDevView)).viewer).getTree()
								.getItem(i3).setImage(
										online_server.createImage());
					}
					currentdevel.setTimestamp(System.currentTimeMillis());
					currentdevel.setOnline(true);
				}
				DeveloperFile file = (DeveloperFile) (((DeveloperView) (NewInstDevView))
						.getDeveloperFile());
				if (file.find(username) == null) {
					file.add(new Developer(username));
					((DeveloperView) (NewInstDevView)).viewer.refresh(false);
				}
			}
		}

	}

	/**
	 * Switches to offline status, making everyone offline.
	 */
	public static void setAllOffline() {

		IViewPart NewInstDevView = Synergy.getDefault().getWorkbench()
				.getActiveWorkbenchWindow().getActivePage().findView(
						"net.sourceforge.synergy.views.DeveloperView");
		if (NewInstDevView != null) {
			Tree thetree = (((DeveloperView) (NewInstDevView)).viewer)
					.getTree();
			for (int i3 = 0; i3 < thetree.getItemCount(); i3++) {

				Developer currentdevel = (Developer) (thetree.getItem(i3)
						.getData());

				if (!currentdevel.getName().equals(server)) {
					(((DeveloperView) (NewInstDevView)).viewer).getTree()
							.getItem(i3).setImage(offline.createImage());
				} else {
					(((DeveloperView) (NewInstDevView)).viewer).getTree()
							.getItem(i3).setImage(offline_server.createImage());
				}

				currentdevel.setOnline(false);
			}
		}
	}

	/**
	 * Handles an incoming pulse from a user.
	 * 
	 * @param username the responding user
	 * @param activefile the users currently active file
	 */
	public void receivePulse(String username, String activefile) {

		/* always set 'all' and our username online regardless */
		setUserOnline(server, null);
		setUserOnline(System.getProperty("user.name"), activefile);

		if (!System.getProperty("user.name").equals(username)) {
			setUserOnline(username, activefile);
		}
	}
}
