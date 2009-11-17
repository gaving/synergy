package net.sourceforge.synergy.views;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.sourceforge.synergy.Dialogs;
import net.sourceforge.synergy.Project;
import net.sourceforge.synergy.Synergy;
import net.sourceforge.synergy.UtilityFunctions;
import net.sourceforge.synergy.data.DataQueries;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.ViewPart;

/**
 * FileHistory displays the users current workspace and allows information
 * retrieval about each file monitored
 */

public class FileHistoryView extends ViewPart {

	private File mroot = new File(Synergy.getDefault().getWorkspace().getRoot()
			.getLocation().toOSString());

	private ImageDescriptor projectIcon = Synergy
			.getImageDescriptor("/icons/history_project.gif");

	private ImageDescriptor dirIcon = Synergy
			.getImageDescriptor("/icons/history_dir.gif");

	private ImageDescriptor textFileIcon = Synergy
			.getImageDescriptor("/icons/history_file.gif");

	private ImageDescriptor modifiedFileIcon = Synergy
			.getImageDescriptor("/icons/history_modified.gif");

	private ImageDescriptor watchedFileIcon = Synergy
			.getImageDescriptor("/icons/history_watched.gif");

	private Tree tree;

	public FileHistoryView() {
		super();
	}

	/**
	 * @see IWorkbenchPart.init#setFocus
	 */
	public void setFocus() {
	}

	public void createPartControl(Composite parent) {

		/* create layout and over all tree */
		parent.setLayout(new FillLayout());
		tree = new Tree(parent, SWT.BORDER);

		/* create a list of projects */
		final List<Project> listOfProjects = new ArrayList<Project>();
		File[] roots = mroot.listFiles();

		/* add first level to tree */
		for (int i = 0; i < roots.length; i++) {

			if (!roots[i].isHidden()) {

				TreeItem root = new TreeItem(tree, 0);
				final String projectName = roots[i].getName();

				/* do the database stuff without hanging the view */
				Display.getDefault().asyncExec(new Runnable() {
					public void run() {

						List<String> watchedFiles = DataQueries
								.getWatchedFiles(projectName);
						List<String> otherUsersModifyingFiles = DataQueries
								.getUsersModifyingFiles(projectName);
						listOfProjects.add(new Project(projectName,
								watchedFiles, otherUsersModifyingFiles));
					}
				});

				root.setText(roots[i].getName());
				root.setData(roots[i]);
				root.setImage(projectIcon.createImage());

				new TreeItem(root, 0);
			}
		}

		/* handles expanding of tree roots */
		tree.addListener(SWT.Expand, new Listener() {
			public void handleEvent(final Event event) {
				final TreeItem root = (TreeItem) event.item;
				TreeItem[] items = root.getItems();
				for (int i = 0; i < items.length; i++) {
					if (items[i].getData() != null) {
						return;
					}
					items[i].dispose();
				}

				File file = (File) root.getData();
				File[] files = file.listFiles();
				if (files == null) {
					return;
				}

				for (int i = 0; i < files.length; i++) {

					/* ignore *.class files */
					if (UtilityFunctions.getFileExtension(files[i], false)
							.equals("class")) {
						continue;
					}

					String projectName, filePath, properPath = files[i]
							.getParent()
							.substring(mroot.getPath().length() + 1);

					TreeItem item = new TreeItem(root, 0);
					item.setText(files[i].getName());
					item.setImage(textFileIcon.createImage());
					item.setData(files[i]);
					item.setData("properPath", properPath);

					/* sort out the project name from the first '/' */
					if (properPath.indexOf("/") == -1) {
						filePath = files[i].getName();
						projectName = properPath;
					} else {
						filePath = properPath.substring(
								properPath.indexOf("/") + 1, properPath
										.length())
								+ "/" + files[i].getName();
						projectName = properPath.substring(0, properPath
								.indexOf("/"));
					}

					if (!listOfProjects.isEmpty()) {
						Iterator listOfProjectsIterator = listOfProjects
								.iterator();
						while (listOfProjectsIterator.hasNext()) {

							/* current project to search for */
							Project currentProject = (Project) listOfProjectsIterator
									.next();

							if (currentProject.getName().equals(projectName)) {

								/* check for files users are modifying first */
								if ((currentProject
										.getOtherUsersModifyingFiles() != null)
										&& currentProject
												.getOtherUsersModifyingFiles()
												.contains(filePath)) {
									FontData fontData = new FontData();
									fontData.setStyle(SWT.BOLD);
									fontData.setHeight(10);
									Font bold = new Font(Display.getCurrent(),
											fontData);
									item.setFont(bold);
									item.setImage(modifiedFileIcon
											.createImage());
								}

								/*
								 * check for files the current user has modified
								 */
								else if ((currentProject.getWatchedFiles() != null)
										&& currentProject.getWatchedFiles()
												.contains(filePath)) {
									FontData fontData = new FontData();
									fontData.setStyle(SWT.ITALIC);
									fontData.setHeight(10);
									Font italic = new Font(
											Display.getCurrent(), fontData);
									item.setFont(italic);
									item
											.setImage(watchedFileIcon
													.createImage());
								}

							}

						}

						if (files[i].isDirectory()) {
							new TreeItem(item, 0);
							item.setImage(dirIcon.createImage());
						}
					}
				}
			}
		});

		/* double click event */
		tree.addListener(SWT.DefaultSelection, new Listener() {
			public void handleEvent(Event e) {
				TreeItem item = (TreeItem) e.item;
				File file = (File) item.getData();
				TreeItem[] selection = tree.getSelection();
				if (item != null) {
					if (!file.isDirectory()) {
						for (int i = 0; i < selection.length; i++) {

							String projectName, filePath, text = selection[i]
									.getText(), properPath = (String) selection[i]
									.getData("properPath");

							if (properPath.indexOf("/") == -1) {
								projectName = properPath;
								filePath = text;
							} else {
								projectName = properPath.substring(0,
										properPath.indexOf("/"));
								filePath = properPath.substring(properPath
										.indexOf("/") + 1, properPath.length())
										+ "/" + text;
							}

							String lastModifiedTimes = DataQueries
									.getUsersModifiedTimes(projectName,
											filePath);
							
							int numberOfUsers = DataQueries.getFileUserHistory(
									projectName, filePath);
							
							List<String> usersWhichModifiedfile = DataQueries
									.getUsersWhichModifiedFile(projectName,
											filePath);

							/* make sure we have proper data returned */
							if (lastModifiedTimes != null) {
								Dialogs.viewFileHistory(text,
										lastModifiedTimes, projectName,
										filePath, numberOfUsers,
										usersWhichModifiedfile);
							} else {
								
								/* error handling */
								Dialogs
										.openError("Error",
												"The database is currently unavailable or misconfigured.");
								Synergy
										.getDefault()
										.logError(
												"Database:"
														+ " Could not get file information for "
														+ text, new Throwable());
							}
						}
					}
				}
			}

		});

	}

}