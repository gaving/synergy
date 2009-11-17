package net.sourceforge.synergy.views;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.synergy.Constants;
import net.sourceforge.synergy.ElvinProducer;
import net.sourceforge.synergy.Synergy;
import net.sourceforge.synergy.actions.ElvinConsumerAction;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.LineStyleEvent;
import org.eclipse.swt.custom.LineStyleListener;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.custom.StyledTextContent;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.part.ViewPart;

/**
 * Implements the conversation view and contains related methods.
 */

public class ChatView extends ViewPart {

	private int maxtabs = 10, lastcreatedtabindex = -1;

	private StyledText[] chathistory;

	private Text[] chatinput;

	private Composite[] container;

	private String[] recipientname;

	private CTabFolder tabbedchat;

	private String server = Synergy.getDefault().getPreferenceStore()
			.getString(Constants.ELVIN_SERVER);

	private String STARTUPTEXT = "Never give out your password or credit "
			+ "card number in an instant message conversation.";

	public ChatView() {
		super();

	}

	public void setFocus() {
		tabbedchat.setFocus();
	}

	public void createNewConvTab(String tabname) {

		int currenttabindex = -1;
		int tabalreadyexists = -1;

		for (int i1 = maxtabs - 1; i1 >= 0; i1--) {
			if ((recipientname[i1] != null)) {
				if (recipientname[i1].equals(tabname)) {
					tabalreadyexists = i1;
				}
			} else {
				currenttabindex = i1;
			}

		}

		if (tabalreadyexists == -1) {

			final int tabnumber = currenttabindex;

			if (tabnumber != -1) {

				lastcreatedtabindex = tabnumber;

				/* container properties */
				container[tabnumber] = new Composite(tabbedchat, 0);
				container[tabnumber].setLayout(new GridLayout());
				recipientname[tabnumber] = tabname;

				/* chat history */
				chathistory[tabnumber] = new StyledText(container[tabnumber],
						(SWT.MULTI | SWT.V_SCROLL | SWT.BORDER | SWT.WRAP));
				chathistory[tabnumber].setEditable(false);
				chathistory[tabnumber].setLayoutData(new GridData(
						GridData.FILL, GridData.FILL, true, true));
				chathistory[tabnumber].setText(STARTUPTEXT);

				/* input field */
				chatinput[tabnumber] = new Text(container[tabnumber],
						SWT.BORDER);
				chatinput[tabnumber].setEditable(true);
				chatinput[tabnumber].setLayoutData(new GridData(GridData.FILL,
						GridData.CENTER, true, false));

				/* fancy styled listener */
				chathistory[tabnumber]
						.addLineStyleListener(new LineStyleListener() {
							public void lineGetStyle(LineStyleEvent event) {
								List<StyleRange> styles = new ArrayList<StyleRange>();
								Color blue = container[tabnumber].getDisplay()
										.getSystemColor(SWT.COLOR_BLUE);

								if (event.lineOffset >= STARTUPTEXT.length()) {

									/* style-ise everything up to the first ':' point */
									styles.add(new StyleRange(event.lineOffset,
											event.lineText.indexOf(":"), blue,
											null, SWT.BOLD));
									event.styles = (StyleRange[]) styles
											.toArray(new StyleRange[0]);
								}
							}
						});

				/* key listener */
				chatinput[tabnumber].addKeyListener(new KeyAdapter() {
					public void keyPressed(KeyEvent event) {

						int code = event.keyCode;
						if (code == SWT.CR) {

							/* make sure we aren't sending nothing. */
							if (chatinput[tabnumber].getText().length() == 0) {
								return;
							}

							/* send message to server */
							sendMessage(tabnumber, recipientname[tabnumber]);

							/* keep the text 'autoscrolling' */
							StyledTextContent doc = chathistory[tabnumber]
									.getContent();
							int docLength = doc.getCharCount();
							if (docLength > 0) {
								chathistory[tabnumber]
										.setCaretOffset(docLength);
								chathistory[tabnumber].showSelection();
							}

						}
					}
				});

				CTabItem item1 = new CTabItem(tabbedchat, 0);
				item1.setText(tabname);
				item1.addDisposeListener(new DisposeListener() {
					public void widgetDisposed(DisposeEvent e) {
						destroyTab(tabnumber);
					}
				});

				item1.setControl(container[tabnumber]);
				item1.getParent().setSelection(item1);
				chatinput[tabnumber].setFocus();
			} else {
				System.out
						.println("The maximum number of tabs has been reached.  "
								+ "Close a tab in the Conversation Window or "
								+ "increase the maximum number of allowed tabs in the preferences.");
			}
		} else {

			/* set focus to the tab that already exists */
			tabbedchat.setSelection(tabalreadyexists);
			chatinput[tabalreadyexists].setFocus();
		}
	}

	/**
	 * Destroys a specific tab
	 * 
	 * @param tabindex the tab number to destroy.
	 */
	private void destroyTab(int tabindex) {
		if (tabindex != -1) {
			container[tabindex] = null;
			recipientname[tabindex] = null;
			recipientname[tabindex] = null;
			chathistory[tabindex] = null;
			chatinput[tabindex] = null;
		}
	}

	/**
	 * Transmits a message from a specific tab windows input
	 * 
	 * @param tabindex the tab number to destroy.
	 * @param to the recipient of the message
	 */
	private void sendMessage(int tabnumber, String to) {

		/* check if we're connected */
		if (ElvinConsumerAction.connected) {
			ElvinProducer producer = new ElvinProducer();
			producer.send(server, "chat", chatinput[tabnumber].getText(), to);

			chathistory[tabnumber].append("\n"
					+ System.getProperty("user.name") + ": "
					+ chatinput[tabnumber].getText());
		} else {
			chathistory[tabnumber].append("\n"
					+ "You are not connected to the server!");
		}
		chatinput[tabnumber].setText("");
		chatinput[tabnumber].setFocus();
	}

	/**
	 * Recieves a message and places it in the correct tab
	 * 
	 * @param index the tab number to append the message to.
	 * @param from the senders name of the received message
	 * @param text the actual messages text
	 */
	private void receiveMessage(int index, String from, String text) {
		chathistory[index].append("\n" + from + ": " + text);

		/* keep the text bar 'autoscrolling' */
		StyledTextContent doc = chathistory[index].getContent();
		int docLength = doc.getCharCount();
		if (docLength > 0) {
			chathistory[index].setCaretOffset(docLength);
			chathistory[index].showSelection();
		}

	}

	public void createPartControl(Composite parent) {

		parent.setLayout(new GridLayout());

		tabbedchat = new CTabFolder(parent, SWT.PUSH | SWT.CLOSE);
		tabbedchat.setLayoutData(new GridData(GridData.FILL, GridData.FILL,
				true, true));
		tabbedchat.addSelectionListener(new SelectionListener() {

			public void widgetSelected(SelectionEvent event) {
				if (tabbedchat.getItem(tabbedchat.getSelectionIndex())
						.getText().startsWith("*")) {
					tabbedchat.getItem(tabbedchat.getSelectionIndex()).setText(
							tabbedchat.getItem(tabbedchat.getSelectionIndex())
									.getText().replaceFirst("[*]", ""));
				}
			}

			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}

		});

		/* create SWT controls */
		chatinput = new Text[maxtabs];
		chathistory = new StyledText[maxtabs];
		container = new Composite[maxtabs];
		recipientname = new String[maxtabs];
	}

	/**
	 * Delegates a message to the correct tab, creating one if necessary
	 * @param from the senders name of the received message
	 * @param text the actual messages text
	 * @param forAll <code>true</code> or <code>false</code> depending if the
	 * code is for everyone connected.
	 */
	public void sendtoChatWindow(String from, String text, boolean forAll) {

		int recipienttabindex = -1;
		int allindex = -1;

		for (int i1 = 0; i1 < (maxtabs); i1++) {
			if (recipientname[i1] != null) {
				if (from.equals(recipientname[i1]))
					recipienttabindex = i1;
				if (recipientname[i1].equals(server))
					allindex = i1;
			}
		}
		if (forAll) {
			if (allindex != -1) {
				receiveMessage(allindex, from, text);
			} else {

				/* the all tab isn't open... */
				createNewConvTab(server);
				receiveMessage(lastcreatedtabindex, server, text);
				allindex = lastcreatedtabindex;
			}

			if (!tabbedchat.getItem(allindex).getText().startsWith("*")) {
				tabbedchat.getItem(allindex).setText(
						"*" + tabbedchat.getItem(allindex).getText());
			}
		} else {

			if (recipienttabindex != -1) {
				receiveMessage(recipienttabindex, from, text);
			} else {

				/* create the new tab */
				createNewConvTab(from);
				receiveMessage(lastcreatedtabindex, from, text);
				recipienttabindex = lastcreatedtabindex;
			}
			if (!tabbedchat.getItem(recipienttabindex).getText()
					.startsWith("*")) {
				tabbedchat.getItem(recipienttabindex).setText(
						"*" + tabbedchat.getItem(recipienttabindex).getText());
			}

		}
	}

}