package net.sourceforge.synergy.views;

import net.sourceforge.synergy.Developer;
import net.sourceforge.synergy.Constants;
import net.sourceforge.synergy.Synergy;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

/**
 * Translates developer into a displayable object
 */

public class DeveloperLabelProvider extends LabelProvider {

	private String server = Synergy.getDefault().getPreferenceStore()
			.getString(Constants.ELVIN_SERVER);

	private ImageDescriptor offline_server = Synergy
			.getImageDescriptor("/icons/buddylist_offline_server.gif");

	private ImageDescriptor offline_buddy = Synergy
			.getImageDescriptor("/icons/buddylist_offline.gif");
	
	/**
	 * @see LabelProvider#getText
	 */
	public String getText(Object element) {
		return super.getText(element);
	}

	/**
	 * @see LabelProvider#getImage
	 */
	public Image getImage(Object element) {

		try {
			
			/* return either and offline or online image depending on status */
			return ((Developer) element).getName().equals(server) ? offline_server
					.createImage()
					: offline_buddy.createImage();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}