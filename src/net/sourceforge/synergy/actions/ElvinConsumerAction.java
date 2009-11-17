package net.sourceforge.synergy.actions;

import net.sourceforge.synergy.ElvinConsumer;
import net.sourceforge.synergy.ReceivePulse;
import net.sourceforge.synergy.SendPulse;
import net.sourceforge.synergy.Constants;
import net.sourceforge.synergy.Synergy;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

/**
 * Handles the elvin toolbar button.
 */

public class ElvinConsumerAction implements IWorkbenchWindowActionDelegate {
	public static boolean connected = false;

	private ElvinConsumer elvin = new ElvinConsumer();

	public ElvinConsumerAction() {
	}

	public void run(IAction action) {

		/* check to see if we're already connected */
		if (!connected) {

			/* list of subscription names to be subscribed to */
			String[] types = { "code", "chat", "pulse", "object" };

			/* address of the server from the preferences window */
			String address = Synergy.getDefault().getPreferenceStore()
					.getString(Constants.ELVIN_SERVER);

			/* attempted to connect to server with the above subscriptions */
			if (elvin.listen(address, types)) {

				connected = true;

				/* send out a pulse with the online status. */
				new SendPulse();

				/* change our buttons text and tooltip text */
				action.setText("Disconnect");
				action.setToolTipText("Disconnect from " + address);

				/* change to our connected image */
				action.setImageDescriptor(Synergy
						.getImageDescriptor("/icons/toolbar_disconnect.png"));
			} else {

				/* connect failed, show an error dialog */
				MessageDialog
						.openError(
								Synergy.getDefault().getActiveWorkbenchWindow()
										.getShell(),
								"Connection Failed!",
								"Could not connect to \""
										+ address
										+ "\" \n"
										+ "Please check this is the correct address and try again.");

				Synergy.getDefault().logError(
						"Elvin: Could not connect to " + address + ".",
						new Throwable());
			}
		} else {

			/* disconnect */
			elvin.disconnect();
			connected = false;

			/* set buddylist offline */
			ReceivePulse.setAllOffline();

			/* change our buttons text and tooltip text */
			action.setText("Listen");
			action.setToolTipText("Listen for incoming signals");

			/* change to our disconnected image */
			action.setImageDescriptor(Synergy
					.getImageDescriptor("/icons/toolbar_connect.png"));
		}
	}

	public void selectionChanged(IAction action, ISelection selection) {
	}

	public void dispose() {
	}

	public void init(IWorkbenchWindow window) {
	}

}