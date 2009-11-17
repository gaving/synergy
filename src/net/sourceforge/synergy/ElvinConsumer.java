package net.sourceforge.synergy;

import org.eclipse.swt.widgets.Display;
import org.elvin.je4.Consumer;
import org.elvin.je4.ElvinURL;
import org.elvin.je4.Notification;
import org.elvin.je4.NotificationListener;
import org.elvin.je4.Subscription;

/**
 * Handles the receiving and processing of elvin events.
 */

public class ElvinConsumer implements NotificationListener {

	private ReceivePulse checkstatus = new ReceivePulse();

	private Consumer cons;

	private String server = Synergy.getDefault().getPreferenceStore()
			.getString(Constants.ELVIN_SERVER);

	/**
	 * Connects and listens for incoming events.
	 * 
	 * @param address server address to connect to.
	 * @param type list of channels to subscribe to.
	 */
	public boolean listen(final String address, String[] type) {

		String types = "";

		try {

			cons = new Consumer(new ElvinURL("elvin://" + address));

			for (int i = 0; i < type.length; i++) {

				types = types + " " + type[i];

				/* create a subscription to every string in the list type */
				Subscription sub = new Subscription("require(" + type[i] + ")");

				/* register a listener */
				sub.addNotificationListener(this);
				cons.addSubscription(sub);
			}

			/* pretty up types and set status */
			UtilityFunctions.setStatus("Elvin: Connected to "
					+ cons.getConnection().getURL() + " ("
					+ types.substring(1, types.length()).replace(" ", ", ")
					+ ")");

		} catch (Exception e) {
			return false;
		}
		return true;

	}

	/**
	 * Disconnects from the currently connected server.
	 */
	public void disconnect() {

		/* disconnect from the server */
		cons.getConnection().close();
		UtilityFunctions.setStatus("Elvin: Disconnected from "
				+ cons.getConnection().getURL());
	}

	/**
	 * Responds to an incoming notification.
	 * 
	 * @param event the incoming event.
	 */
	public void notificationAction(final Notification event) {

		/* get current display (PDE Environment) */
		Display display = Display.getCurrent();
		if (display == null) {

			Display.getDefault().asyncExec(new Runnable() {
				public void run() {

					String type = "";

					/* we want pulses regardless who we are */
					if (event.containsKey("pulse")) {
						checkstatus.receivePulse(event.getString("username"),
								event.getString("currentinformation"));
					} else {

						/* check that it isn't us the event is for */
						if (event.getString("to").equals(
								System.getProperty("user.name"))
								|| event.getString("to").equals(server)) {

							/* code event received */
							if (event.containsKey("code")) {

								Dialogs.receiveCode(event.getString("from"),
										event.getString("code"));
							}

							/* chat event received */
							else if (event.containsKey("chat")) {

								type = event.printString().substring(0,
										(event.printString().indexOf(':')));

								UtilityFunctions.sendMessageToTab(event
										.getString("from"), event
										.getString(type), event.getString("to")
										.equals(server));
							}
						}

						/*
						 * Indicate that some event we are listening for has
						 * been received
						 */
						UtilityFunctions
								.setStatus("Elvin: Watched event received!");
					}
				}
			});

		}

	}
}