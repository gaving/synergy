package net.sourceforge.synergy;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.sourceforge.synergy.views.ChatView;
import net.sourceforge.synergy.views.DeveloperView;

import org.eclipse.core.resources.IResource;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.PartInitException;

/**
 * Contains a collection of general static functions.
 */

public class UtilityFunctions {

	public static Shell window = Synergy.getDefault()
			.getActiveWorkbenchWindow().getShell();

	public static String currentEditorTitle = "none";

	/**
	 * Returns information about the current users active workspace.
	 *
	 * @return information about an active workspace
	 */
	public static String getCurrentInformation() {
		String currentEditorTitle = "No editor opened!";
		String currentProject = "No project opened!";
		int currentEditorLine = 0;

		ISelection selection = Synergy.getDefault().getActiveWorkbenchWindow()
				.getSelectionService().getSelection();
		IEditorPart editor = Synergy.getDefault().getActiveWorkbenchWindow()
				.getActivePage().getActiveEditor();

		if (selection instanceof ITextSelection) {

		} else {

			IResource resources[] = Synergy.getDefault().getSelectedResources(
					selection);
			if (resources != null) {
				try {
					currentProject = resources[0].getProject().getName();
				} catch (Exception e) {
				}
			}
		}

		if (editor != null) {

			/* TODO: Get the project name from the editor somehow */

			/* editor isn't null so get some information about it */
			currentEditorTitle = editor.getTitle();

			/* get line number we're at */
			if (selection instanceof ITextSelection) {
				ITextSelection text = (ITextSelection) selection;
				currentEditorLine = text.getStartLine();
			}

		}

		return currentProject + " (" + currentEditorTitle + ":"
				+ currentEditorLine + ")";
	}

	/**
	 * Returns the current active editors filepath.
	 *
	 * @return the current active editors filepath
	 */
	public static String getCurrentFilePath() {

		try {
			final IEditorPart editor = Synergy.getDefault()
					.getActiveWorkbenchWindow().getActivePage()
					.getActiveEditor();

			if (editor != null) {

				try {
				Display.getDefault().syncExec(new Runnable() {
					// not async...
					public void run() {

						/* editor isn't null so get some information about it */
						currentEditorTitle = editor.getTitle();
					}
				});
				} catch (Exception e) {currentEditorTitle = ""; }
			}
			return currentEditorTitle;
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Returns a file extension of a given file.
	 *
	 * @return the file extension
	 * @param file the file
	 * @param keepDot keep the dot when returning the extension
	 */
	public static String getFileExtension(File file, boolean keepDot) {

		/* strip path first. */
		String base = file.getName();

		String extension = "";
		int index = base.lastIndexOf('.');
		if (index != -1) {
			if (keepDot)
				extension = base.substring(index);
			else
				extension = base.substring(index + 1);
		}
		return extension;
	}

	/**
	 * Returns a list of currently online users.
	 *
	 * @return a list of online users
	 */
	public static List getOnlineUsers() {

		List<String> data = new ArrayList<String>();

		IViewPart NewInstDevView = Synergy.getDefault().getWorkbench()
				.getActiveWorkbenchWindow().getActivePage().findView(
						"net.sourceforge.synergy.views.DeveloperView");
		if (NewInstDevView != null) {

			Tree thetree = (((DeveloperView) (NewInstDevView)).viewer)
					.getTree();

			for (int i3 = 0; i3 < thetree.getItemCount(); i3++) {
				Developer currentdevel = ((Developer) ((((DeveloperView) (NewInstDevView)).viewer)
						.getTree().getItem(i3).getData()));
				if (currentdevel.getOnlineStatus()) {
					data.add(currentdevel.getName());
				}
			}

			return data;

		}
		return null;
	}

	/**
	 * Returns a formatted timestamp.
	 *
	 * @return a timestamp
	 */
	public static String getTimestamp() {

		/* create new timestamp */
		Date today = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("h:mm a");
		String datenewformat = formatter.format(today);
		return datenewformat;
	}

	/**
	 * Sets the statusbar text and logs it.
	 *
	 * @param text text to display.
	 */
	public static void setStatus(String text) {

		/* set the actual status bar with timestamp */
		Synergy.getDefault().getStatusBar().setText(
				"(" + getTimestamp() + ") " + text);

		/* add something to the built in eclipse log */
		Synergy.getDefault().logInfo(text);
	}
	
	/**
	 * Sends the given message to the users IM tab.
	 *
	 * @param from the sender
	 * @param message the message
	 * @param forAll indication that the message is for everyone
	 */
	public static void sendMessageToTab(String from, String message,
			boolean forAll) {

		IViewPart InstChatView = Synergy.getDefault().getWorkbench()
				.getActiveWorkbenchWindow().getActivePage().findView(
						"net.sourceforge.synergy.views.ChatView");
		if (InstChatView != null) {
			((ChatView) (InstChatView)).sendtoChatWindow(from, message, forAll);

			/*
			 * TODO: this causes the current selection to lose focus which is
			 * undesirable probably best if we find out how to make a tabs text
			 * bold instead.
			 */
			try {
				InstChatView.getViewSite().getPage().showView(
						"net.sourceforge.synergy.views.ChatView");
			} catch (PartInitException e) {
				Synergy.getDefault().logError(
						"General: Unable to initialise the conversation view.",
						e);
			}
		} else {
			/* attempt to open the conversation view */
			try {

				IViewPart NewInstChatView = InstChatView.getViewSite()
						.getPage().findView(
								"net.sourceforge.synergy.views.ChatView");

				((ChatView) (NewInstChatView)).sendtoChatWindow(from, message,
						forAll);

			} catch (Exception ex) {
				Synergy.getDefault().logError(
						"General: Unable to initialise the conversation view!",
						ex);
			}
		}
	}

}
