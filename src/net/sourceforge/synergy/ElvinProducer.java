package net.sourceforge.synergy;


import org.elvin.je4.ElvinURL;
import org.elvin.je4.Notification;
import org.elvin.je4.Producer;

/**
 * Handles the transmission of elvin events.
 */

public class ElvinProducer {
	
	/**
	 * Send a message via elvin.
	 * 
	 * @param address server address to send to.
	 * @param type channel to send to.
	 * @param message message to send to
	 * @param to recipent of the message
	 */
	public void send(String address, String type, String message, String to) {

		ElvinURL elvinurl = new ElvinURL("elvin://" + address);

		try {

			Producer prod = new Producer(elvinurl);

			Notification notif = new Notification();

			/* fill the notification with info */
			notif.put(type, message);
			notif.put("from", System.getProperty("user.name"));
			notif.put("to", to);

			prod.notify(notif);

			/* close the connection */
			prod.getConnection().close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Send a keep alive pulse.
	 * 
	 * @param address server address to send to.
	 * @param currentinformation bundled current information about a user.
	 */
	public void sendpulse(String address, String currentinformation) {

		ElvinURL elvinurl = new ElvinURL("elvin://" + address);

		try {

			Producer prod = new Producer(elvinurl);
			Notification notif = new Notification();
			notif.put("pulse", "pulse");
			notif.put("currentinformation", currentinformation);
			notif.put("username", System.getProperty("user.name"));
			prod.notify(notif);

			prod.getConnection().close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Send an object via elvin.
	 * 
	 * @param address server address to send to.
	 * @param objecttosend object to transmit.
	 * @see ReceiveObject
	 */
	public void sendobject(String address, Object objecttosend) {

		ElvinURL elvinurl = new ElvinURL("elvin://" + address);

		try {

			Producer prod = new Producer(elvinurl);
			Notification notif = new Notification();

			String hextosend = new SendObject().ObjecttoHex(objecttosend);
			notif.put("object", "object");
			notif.put("hex", hextosend);
			notif.put("username", System.getProperty("user.name"));

			prod.notify(notif);

			prod.getConnection().close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
