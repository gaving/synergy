package net.sourceforge.synergy;

import net.sourceforge.synergy.ReceivePulse;
import net.sourceforge.synergy.actions.*;

import org.eclipse.swt.widgets.Display;

/**
 * Handles the transmission of elvin pulses and contains related methods.
 */

public class SendPulse implements Runnable {

	private static Thread statusthread;

	private ElvinProducer pulsate = new ElvinProducer();

	private String server = Synergy.getDefault().getPreferenceStore()
			.getString(Constants.ELVIN_SERVER);

	private String currentInformation = "Nothing.";

	/**
	 * The constructor
	 */
	public SendPulse() {
		statusthread = new Thread(this, "Pulse thread");
		statusthread.start();
	}
	
	/**
	 * The runnable class
	 */
	public void run() {

		if (ElvinConsumerAction.connected) {
			try {

				while (ElvinConsumerAction.connected) {

					Display.getDefault().asyncExec(new Runnable() {
						public void run() {
							try {

								/* get current user information for trannsmission */
								currentInformation = UtilityFunctions
										.getCurrentInformation();
							} catch (Exception e) {

							}
						}
					});

					/* send the pulse and sleep for 3 seconds */
					pulsate.sendpulse(server, currentInformation);
					Thread.sleep(3000);
				}

			} catch (InterruptedException e) {
				Synergy.getDefault().logError("Pulse was interrupted!", e);
			}

		} else {
			ReceivePulse.setAllOffline();
		}
	}

	/**
	 * Stops an ongoing pulse thread
	 */
	public static void stopPulseThread() {

		/* recommended way of stopping threads */
		statusthread = null;
	}
}
