package net.sourceforge.synergy;

import java.util.List;

import net.sourceforge.synergy.actions.SendCodePopupAction;
import net.sourceforge.synergy.data.DataQueries;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

/**
 * Contains a collection of common and used dialogs.
 */

public class Dialogs {
	public static Shell window = Synergy.getDefault()
			.getActiveWorkbenchWindow().getShell();

	/**
	 * Views an active developers information.
	 * 
	 * @param developer developer to view.
	 */
	public static void viewDeveloper(final Developer developer) {

		TitleAreaDialog dialog = new TitleAreaDialog(UtilityFunctions.window) {
			protected Control createContents(Composite parent) {
				Control result = super.createContents(parent);
				setTitle("View Info");
				setMessage(developer.getName() + "'s information", IMessageProvider.INFORMATION);
				return result;
			}

			protected Control createDialogArea(Composite parent) {
				Composite composite = (Composite) super
						.createDialogArea(parent);
				composite.setLayout(new GridLayout());
				
				GridData griddata = new GridData(GridData.FILL_BOTH);
				griddata.heightHint = 280;
				composite.setLayoutData(griddata);

				Group group1 = new Group(composite, SWT.BORDER);
				group1.setText("Current status");
				group1.setLayout(new GridLayout(2, true));
				group1.setLayoutData(new GridData(GridData.FILL_BOTH));

				new Label(group1, SWT.NONE).setText("Developer is currently:");

				new Label(group1, SWT.READ_ONLY).setText(((developer
						.getOnlineStatus()) ? "Online" : "Offline"));

				new Label(group1, SWT.NONE).setText("Last ping:");

				new Label(group1, SWT.READ_ONLY)
						.setText(((System.currentTimeMillis() - developer
								.getTimestamp()) / 1000f)
								+ " seconds ago.");

				if (developer.getOnlineStatus()) {

					new Label(group1, SWT.NONE).setText("Looking at:");
					new Label(group1, SWT.READ_ONLY).setText((developer
							.getCurrentlyOpenedResource()));

					Group group2 = new Group(composite, SWT.BORDER);
					group2.setText("Activity status");
					group2.setLayout(new FillLayout());
					group2.setLayout(new GridLayout(1, false));
					group2.setLayoutData(new GridData(GridData.FILL_BOTH));

					Table table1 = new Table(group2, SWT.BORDER);
					table1.setHeaderVisible(true);
					table1.setLinesVisible(true);
					table1.setLayout(new GridLayout());

					GridData griddata2 = new GridData(GridData.FILL_BOTH);
					griddata2.heightHint = 100;
					table1.setLayoutData(griddata2);
					TableColumn first = new TableColumn(table1, SWT.RESIZE
							| SWT.LEFT);
					first.setResizable(true);
					first.setText("Modified Projects");

					TableColumn second = new TableColumn(table1, SWT.LEFT);
					second.setWidth(10);
					second.setResizable(true);
					second.setText("Modified Files");

					TableColumn third = new TableColumn(table1, SWT.RESIZE
							| SWT.LEFT);
					third.setResizable(true);
					third.setText("Last Modified");

					/* get the data we need for the table */
					List<String> usersModifiedFiles = DataQueries
							.getUsersModifiedFiles(developer.getName());

					List<String> usersModifiedProjects = DataQueries
							.getUsersModifiedProjects(developer.getName());

					for (int i = 0; i < usersModifiedFiles.size(); i++) {
						int c = 0;
						TableItem item = new TableItem(table1, SWT.LEFT);

						item.setText(c++, usersModifiedProjects.get(i));
						item.setText(c++, usersModifiedFiles.get(i));

						item
								.setText(c++, DataQueries
										.getUserLastModForFile(
												usersModifiedProjects.get(i),
												usersModifiedFiles.get(i),
												developer.getName()));
					}

					first.pack();
					second.pack();
					third.pack();
					table1.pack();
					composite.pack();

					table1.setRedraw(true);
					table1.addListener(SWT.Selection, new Listener() {
						public void handleEvent(Event event) {
							/* TODO Update the dialog */
						}
					});
				}
				return composite;
			}

			protected void createButtonsForButtonBar(Composite parent) {
				Button okButton = createButton(parent, SWT.OK, "OK", true);
				okButton.addSelectionListener(new SelectionAdapter() {
					public void widgetSelected(SelectionEvent e) {
						close();
					}
				});
			}

		};
		dialog.open();
	}

	/**
	 * Views a files history
	 * 
	 * @param fileName the files name
	 * @param lastModifiedTimes the files modification times
	 * @param projectName the projects name
	 * @param filePath the files path
	 * @param numberOfUsers number of users working on the file
	 * @param usersWhichModifiedfile usernames that modified the file
	 */
	public static void viewFileHistory(final String fileName,
			final String lastModifiedTimes, final String projectName,
			final String filePath, final int numberOfUsers,
			final List<String> usersWhichModifiedfile) {
		TitleAreaDialog dialog = new TitleAreaDialog(UtilityFunctions.window) {
			protected Control createContents(Composite parent) {
				Control result = super.createContents(parent);
				setTitle("View Info");
				setMessage(fileName + "'s information", IMessageProvider.INFORMATION);
				return result;
			}

			protected Control createDialogArea(Composite parent) {
				Composite composite = (Composite) super
						.createDialogArea(parent);
				composite.setLayout(new GridLayout());
				composite.setLayoutData(new GridData(GridData.FILL_BOTH));

				if (!lastModifiedTimes.equals("")) {

					Group group1 = new Group(composite, SWT.BORDER);
					group1.setText("People who've modified this file");
					group1.setLayout(new GridLayout());
					group1.setLayoutData(new GridData(GridData.FILL_BOTH));

					Table table1 = new Table(group1, SWT.FULL_SELECTION
							| SWT.RESIZE);

					table1.setHeaderVisible(true);
					table1.setLinesVisible(true);
					table1.setBounds(10, 180, 24, 80);
					table1.setLayoutData(new GridData(GridData.FILL_BOTH));

					table1.pack();

					TableColumn first = new TableColumn(table1, SWT.RESIZE
							| SWT.CENTER);

					first.setResizable(true);
					first.setText("Developer");
					first.pack();

					TableColumn second = new TableColumn(table1, SWT.RESIZE
							| SWT.CENTER);

					second.setResizable(true);
					second.setText("Last Modified");
					second.pack();

					TableColumn third = new TableColumn(table1, SWT.RESIZE
							| SWT.CENTER);

					third.setResizable(true);
					third.setText("Changes");
					third.pack();

					TableColumn fourth = new TableColumn(table1, SWT.RESIZE
							| SWT.CENTER);

					fourth.setResizable(true);
					fourth.setText("Ratio");
					fourth.pack();

					// Populate the table with the data
					for (int i = 0; i < (usersWhichModifiedfile.size()); i++) {
						int c = 0;
						TableItem item = new TableItem(table1, SWT.LEFT, i);

						item.setText(c++, usersWhichModifiedfile.get(i)
								.toString());

						item.setText(c++, DataQueries.getUserLastModForFile(
								projectName, filePath, usersWhichModifiedfile
										.get(i)));

						item.setText(c++, ""
								+ DataQueries.getUsersFileModTotal(projectName,
										filePath, usersWhichModifiedfile.get(i)
												.toString()));

						ProgressBar bar = new ProgressBar(table1, SWT.NONE);
						bar.setMaximum(numberOfUsers);

						bar.setSelection(DataQueries.getUsersFileModTotal(
								projectName, filePath, usersWhichModifiedfile
										.get(i)));
						TableEditor editor = new TableEditor(table1);
						editor.grabHorizontal = editor.grabVertical = true;
						editor.setEditor(bar, item, 3);
					}

					first.pack();
					second.pack();
					third.pack();

					table1.addListener(SWT.Selection, new Listener() {
						public void handleEvent(Event event) {
							/* TODO: Update dialog */

						}
					});

					Group group2 = new Group(composite, SWT.BORDER);
					group2.setText("Count of modifications");
					group2.setLayout(new GridLayout(1, false));
					group2.setLayoutData(new GridData(GridData.FILL_BOTH));

					new Label(group2, SWT.NONE).setText(usersWhichModifiedfile
							.get(0)
							+ " made "
							+ DataQueries.getUsersFileModTotal(projectName,
									filePath, usersWhichModifiedfile.get(0))
							+ " of the " + numberOfUsers + " changes");

				} else {
					Group group1 = new Group(composite, SWT.BORDER);
					group1.setText("No history of this file in the database!");

				}

				composite.pack();
				return composite;
			}

			protected void createButtonsForButtonBar(Composite parent) {
				Button okButton = createButton(parent, SWT.OK, "OK", true);
				okButton.addSelectionListener(new SelectionAdapter() {
					public void widgetSelected(SelectionEvent e) {
						close();
					}
				});
			}
		};

		dialog.open();

	}

	/**
	 * Views the servers information.
	 * 
	 * @param server server to view.
	 */
	public static void viewServer(final Developer server) {

		TitleAreaDialog dialog = new TitleAreaDialog(UtilityFunctions.window) {
			protected Control createContents(Composite parent) {

				Control result = super.createContents(parent);
				setTitle("View Info");
				setMessage((server) + "'s information", IMessageProvider.INFORMATION);
				return result;

			}

			protected Control createDialogArea(Composite parent) {

				Composite composite = (Composite) super
						.createDialogArea(parent);
				composite.setLayout(new GridLayout());
				composite.setLayoutData(new GridData(GridData.FILL_BOTH));

				Group group1 = new Group(composite, SWT.BORDER);
				group1.setText("Current status");
				group1.setLayout(new GridLayout(2, true));
				group1.setLayoutData(new GridData(GridData.FILL_BOTH));

				new Label(group1, SWT.NONE).setText("Server Status:");
				new Label(group1, SWT.READ_ONLY).setText(((server
						.getOnlineStatus()) ? "Online" : "Offline"));

				new Label(group1, SWT.NONE).setText("Last ping:");
				new Label(group1, SWT.READ_ONLY).setText(((System
						.currentTimeMillis() - server.getTimestamp()) / 1000f)
						+ " seconds ago.");
				return composite;
			}

			protected void createButtonsForButtonBar(Composite parent) {
				Button okButton = createButton(parent, SWT.OK, "OK", true);
				okButton.addSelectionListener(new SelectionAdapter() {
					public void widgetSelected(SelectionEvent e) {
						close();
					}
				});
			}

		};

		dialog.open();
	}

	/**
	 * Views recieved code.
	 * 
	 * @param username senders username.
	 * @param text code recieved.
	 */
	public static void receiveCode(final String username, final String text) {

		TitleAreaDialog dialog = new TitleAreaDialog(window) {

			Clipboard clipboard;

			private Button b1;

			private Button b2;

			private Button b3;

			protected Control createContents(Composite parent) {
				Control result = super.createContents(parent);

				setTitle("New code received!");
				setMessage(username + " has sent you a code snippet.", IMessageProvider.INFORMATION);
				return result;
			}

			protected Control createDialogArea(Composite parent) {
				Composite composite = (Composite) super
						.createDialogArea(parent);

				composite.setLayout(new GridLayout());
				composite.setLayoutData(new GridData(GridData.FILL_BOTH));

				Label l = new Label(composite, SWT.NONE);
				l.setText("What would you like to do?");
				l.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

				b1 = new Button(composite, SWT.RADIO);
				b1.setText("View the received code");
				b1.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

				b2 = new Button(composite, SWT.RADIO);
				b2.setText("Paste to current cursor position");
				b2.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

				b3 = new Button(composite, SWT.RADIO);
				b3.setText("Copy to clipboard");
				b3.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

				return composite;
			}

			protected void buttonPressed(int buttonId) {

				if (buttonId == IDialogConstants.OK_ID) {

					if (b1.getSelection()) {

						/* start new thread */
						Display.getDefault().asyncExec(new Runnable() {
							public void run() {

								Shell codewindow = new Shell(Display
										.getDefault());
								codewindow.setLayout(new GridLayout(1, false));
								codewindow.setText("Received code!");
								codewindow.setBounds(600, 200, 400, 600);

								Text t = new Text(codewindow, SWT.MULTI
										| SWT.BORDER | SWT.WRAP | SWT.V_SCROLL);

								t
										.setLayoutData(new GridData(
												GridData.FILL_BOTH));
								t.setText(text);

								codewindow.open();

								while (!codewindow.isDisposed()) {
									if (!Display.getDefault().readAndDispatch())
										Display.getDefault().sleep();
								}

							}
						});

					} else if (b2.getSelection()) {

						/* insert the received text and current position */
						SendCodePopupAction.insertText(text);

					} else if (b3.getSelection()) {

						/* create new clipboard if none */
						if (clipboard == null) {
							clipboard = new Clipboard(Display.getCurrent());
						}

						/* copy the received to clipboard */
						clipboard.setContents(new Object[] { text },
								new Transfer[] { TextTransfer.getInstance() });
					}
				}
				close();
			}
		};

		dialog.open();
	}

	/**
	 * Opens an message dialog with a farmiliar shell.
	 * 
	 * @param title title of the dialog.
	 * @param text information message.
	 */
	public static void openMessage(String title, String text) {
		MessageDialog.openInformation(window, title, text);
	}

	/**
	 * Opens an error dialog with a farmiliar shell.
	 * 
	 * @param title title of the dialog.
	 * @param text error message.
	 */
	public static void openError(String title, String text) {
		MessageDialog.openError(window, title, text);
	}

	/**
	 * Sends selected code.
	 * 
	 * @param code code to send.
	 */
	public static void sendCode(final String code) {

		TitleAreaDialog dialog = new TitleAreaDialog(window) {

			private Combo username_combo;

			protected Control createContents(Composite parent) {
				Control result = super.createContents(parent);
				setTitle("Send code");
				setMessage("Send a code snippet.", IMessageProvider.INFORMATION);
				return result;
			}

			protected Control createDialogArea(Composite parent) {
				Composite composite = (Composite) super
						.createDialogArea(parent);

				composite.setLayout(new GridLayout());
				composite.setLayoutData(new GridData(GridData.FILL_BOTH));

				Label l = new Label(composite, SWT.NONE);
				l.setText("Select username:");
				l.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

				username_combo = new Combo(composite, SWT.READ_ONLY);
				username_combo.setLayoutData(new GridData(
						GridData.FILL_HORIZONTAL));

				/* get a list of the connected users */
				List online_usernames = UtilityFunctions.getOnlineUsers();

				/* add each to the combo box */
				for (int i = 0; i < online_usernames.size(); i++) {
					username_combo.add((String) online_usernames.get(i));
				}

				return composite;
			}

			protected void buttonPressed(int buttonId) {

				if (buttonId == IDialogConstants.OK_ID
						&& (!username_combo.getText().equals(""))) {

					/* create a new elvin producer */
					ElvinProducer producer = new ElvinProducer();

					/*
					 * send the actual message to username on "code"
					 * subscription
					 */
					producer.send(Synergy.getDefault().getPreferenceStore()
							.getString(Constants.ELVIN_SERVER), "code", code,
							username_combo.getText());

					/* set the status bar */
					UtilityFunctions.setStatus("Elvin: Sent a code snippet.");

				}
				close();
			}
		};

		dialog.open();
	}

	/**
	 * Send raw elvin data via some channel.
	 */
	public static void sendChannelData() {

		TitleAreaDialog dialog = new TitleAreaDialog(window) {

			Combo usernameCombo;

			Text channelName;

			Text channelText;

			protected Control createContents(Composite parent) {
				Control result = super.createContents(parent);

				setTitle("Send signal");
				setMessage("Use this dialog for sending test data on a channel.", IMessageProvider.INFORMATION);
				return result;
			}

			protected Control createDialogArea(Composite parent) {
				Composite composite = (Composite) super
						.createDialogArea(parent);

				composite.setLayout(new GridLayout());
				composite.setLayoutData(new GridData(GridData.FILL_BOTH));

				new Label(composite, SWT.NULL).setText("Channel:");
				channelName = new Text(composite, SWT.SINGLE | SWT.BORDER);
				channelName.setLayoutData(new GridData(
						GridData.HORIZONTAL_ALIGN_FILL));

				new Label(composite, SWT.NULL).setText("Username:");
				usernameCombo = new Combo(composite, SWT.NULL);

				List online_usernames = UtilityFunctions.getOnlineUsers();

				for (int i = 0; i < online_usernames.size(); i++) {
					usernameCombo.add((String) online_usernames.get(i));
				}
				usernameCombo.setLayoutData(new GridData(
						GridData.HORIZONTAL_ALIGN_FILL));

				new Label(composite, SWT.NULL).setText("Text:");
				channelText = new Text(composite, SWT.SINGLE | SWT.BORDER);
				channelText.setLayoutData(new GridData(
						GridData.HORIZONTAL_ALIGN_FILL));

				return composite;
			}

			protected void buttonPressed(int buttonId) {

				if (buttonId == IDialogConstants.OK_ID) {

					ElvinProducer producer = new ElvinProducer();
					producer.send(Synergy.getDefault().getPreferenceStore()
							.getString(Constants.ELVIN_SERVER), channelName
							.getText(), channelText.getText(), usernameCombo
							.getText());
				}
				close();
			}
		};

		dialog.open();
	}

}
