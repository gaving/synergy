package net.sourceforge.synergy.views;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.sourceforge.synergy.RelatedFile;
import net.sourceforge.synergy.Synergy;
import net.sourceforge.synergy.UtilityFunctions;
import net.sourceforge.synergy.data.SQLInterface;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.ViewPart;

import com.sun.rowset.CachedRowSetImpl;

public class FileRecommendationsView extends ViewPart {
	private static SQLInterface iface = Synergy.getDefault().getDatabase();

	public static String activeFile = null;

	public static Shell shell = Synergy.getDefault().getActiveWorkbenchWindow()
			.getShell();

	private Table table = new Table(shell, SWT.BORDER);

	private TableEditor editor0 = new TableEditor(table);

	private TableEditor editor1 = new TableEditor(table);

	private TableEditor editor2 = new TableEditor(table);

	private TableEditor editor3 = new TableEditor(table);

	private TableEditor editor4 = new TableEditor(table);

	private TableItem item0 = new TableItem(table, SWT.NONE);

	private ProgressBar bar0 = new ProgressBar(table, SWT.RIGHT);

	private TableItem item1 = new TableItem(table, SWT.NONE);

	private ProgressBar bar1 = new ProgressBar(table, SWT.RIGHT);

	private TableItem item2 = new TableItem(table, SWT.NONE);

	private ProgressBar bar2 = new ProgressBar(table, SWT.RIGHT);

	private TableItem item3 = new TableItem(table, SWT.NONE);

	private ProgressBar bar3 = new ProgressBar(table, SWT.RIGHT);

	private TableItem item4 = new TableItem(table, SWT.NONE);

	private ProgressBar bar4 = new ProgressBar(table, SWT.RIGHT);

	private List<RelatedFile> relatedFiles = new ArrayList<RelatedFile>();

	private int activeFileHistorySize = 0;

	public FileRecommendationsView() {
		super();

	}

	/**
	 * @see IWorkbenchPart.init#setFocus
	 */
	public void setFocus() {
		updateRelatedFiles();
		updatePartControl(relatedFiles, activeFileHistorySize);
	}

	public void updateRelatedFiles() {

		activeFile = UtilityFunctions.getCurrentFilePath();

		List<String> relatedFileNames = new ArrayList<String>();
		List<Integer> relatedFileRelevance = new ArrayList<Integer>();
		relatedFiles = new ArrayList<RelatedFile>();
		RelatedFile rf;
		List<String> timesEdited = getFileModificationTimes(activeFile);

		// Find files edited around the time of each modification time
		for (int i = 0; i < timesEdited.size(); i++) {
			calulateRelations(activeFile, timesEdited.get(i), relatedFileNames,
					relatedFileRelevance);
		}

		for (int i = 0; i < relatedFileNames.size(); i++) {

			rf = new RelatedFile(relatedFileNames.get(i));
			rf.setRelevance(relatedFileRelevance.get(i));
			relatedFiles.add(i, rf);

		}
		Collections.sort(relatedFiles);
		Collections.reverse(relatedFiles);
		activeFileHistorySize = timesEdited.size();

	}

	public void createPartControl(Composite parent) {
		table = new Table(parent, SWT.BORDER);
		editor0 = new TableEditor(table);
		editor1 = new TableEditor(table);
		editor2 = new TableEditor(table);
		editor3 = new TableEditor(table);
		editor4 = new TableEditor(table);

		parent.setLayout(new FillLayout());
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		new TableColumn(table, SWT.NONE);
		for (int i = 1; i < 3; i++) {
			new TableColumn(table, SWT.RIGHT);
		}
		table.getColumn(0).setText("Filename");
		table.getColumn(1).setText("");
		table.getColumn(2).setText("%");
		table.getColumn(2).setWidth(40);
		table.getColumn(0).pack();
		table.getColumn(1).setWidth(120);

		item0 = new TableItem(table, SWT.NONE);
		item0.setText("Initialising");
		bar0 = new ProgressBar(table, SWT.RIGHT);
		bar0.setSelection(0);
		editor0.grabVertical = true;
		editor0.grabHorizontal = true;
		editor0.setEditor(bar0, item0, 1);

		item1 = new TableItem(table, SWT.NONE);
		bar1 = new ProgressBar(table, SWT.RIGHT);
		bar1.setSelection(0);
		editor1.grabVertical = true;
		editor1.grabHorizontal = true;
		editor1.setEditor(bar1, item1, 1);

		item2 = new TableItem(table, SWT.NONE);
		bar2 = new ProgressBar(table, SWT.RIGHT);
		bar2.setSelection(0);
		editor2.grabVertical = true;
		editor2.grabHorizontal = true;
		editor2.setEditor(bar2, item2, 1);

		item3 = new TableItem(table, SWT.NONE);
		bar3 = new ProgressBar(table, SWT.RIGHT);
		bar3.setSelection(0);
		editor3.grabVertical = true;
		editor3.grabHorizontal = true;
		editor3.setEditor(bar3, item3, 1);

		item4 = new TableItem(table, SWT.NONE);
		bar4 = new ProgressBar(table, SWT.RIGHT);
		bar4.setSelection(40);
		editor4.grabVertical = true;
		editor4.grabHorizontal = true;
		editor4.setEditor(bar4, item4, 1);
		
		bar4.dispose();
		editor4.getItem().dispose();
		item4.dispose();
		bar3.dispose();
		item3.dispose();
		bar2.dispose();
		item2.dispose();
		bar1.dispose();
		item1.dispose();
		table.redraw();
		
		
		table.update();
		table.redraw();

	}

	public void updatePartControl(List<RelatedFile> relatedFiles,
			int timesEdited) {
		// DRAWING!!
		if (relatedFiles.size() != 0) {

			int barSelection = 0;
			item0.setText(relatedFiles.get(0).getName());
			barSelection = (int) (((float) relatedFiles.get(0).getRelevance()
					/ (float) (timesEdited) * 100.0));
			bar0.setSelection(barSelection);
			editor0.setEditor(bar0, item0, 1);
			item0.setText(2,((Integer)barSelection).toString());

			if (relatedFiles.size() > 1) {

				if (bar1.isDisposed()) {
					item1 = new TableItem(table, SWT.NONE);
					bar1 = new ProgressBar(table, SWT.RIGHT);
				}
				item1.setText(relatedFiles.get(1).getName());
				barSelection = (int) (((float) relatedFiles.get(1)
						.getRelevance()
						/ (float) (timesEdited) * 100.0));

				bar1.setSelection(barSelection);
				editor1.setEditor(bar1, item1, 1);
				item1.setText(2,((Integer)barSelection).toString());
				if (relatedFiles.size() > 2) {

					if (bar2.isDisposed()) {
						item2 = new TableItem(table, SWT.NONE);

						bar2 = new ProgressBar(table, SWT.RIGHT);
					}
					item2.setText(relatedFiles.get(2).getName());
					barSelection = (int) (((float) relatedFiles.get(2)
							.getRelevance()
							/ (float) (timesEdited) * 100.0));

					bar2.setSelection(barSelection);
					editor2.setEditor(bar2, item2, 1);
					item2.setText(2,((Integer)barSelection).toString());
					if (relatedFiles.size() > 3) {

						if (bar3.isDisposed()) {
							item3 = new TableItem(table, SWT.NONE);
							bar3 = new ProgressBar(table, SWT.RIGHT);
						}
						item3.setText(relatedFiles.get(3).getName());
						barSelection = (int) (((float) relatedFiles.get(3)
								.getRelevance()
								/ (float) (timesEdited) * 100.0));

						bar3.setSelection(barSelection);
						editor3.setEditor(bar3, item3, 1);
						item3.setText(2,((Integer)barSelection).toString());
						if (relatedFiles.size() > 4) {

							if (bar4.isDisposed()) {
								item4 = new TableItem(table, SWT.NONE);
								bar4 = new ProgressBar(table, SWT.RIGHT);
							}
							item4.setText(relatedFiles.get(4).getName());
							barSelection = (int) (((float) relatedFiles.get(4)
									.getRelevance()
									/ (float) (timesEdited) * 100.0));

							if (!bar4.getVisible()) {
								bar4.setVisible(true);
							}
							bar4.setSelection(barSelection);
							editor4.setEditor(bar4, item4, 1);
							item4.setText(2,((Integer)barSelection).toString());
						} else {

							bar4.dispose();
							editor4.getItem().dispose();
							item4.dispose();
							table.redraw();
						}
					} else {
						bar4.dispose();
						editor4.getItem().dispose();
						item4.dispose();
						bar3.dispose();
						item3.dispose();
						table.redraw();
					}
				} else {
					bar4.dispose();
					editor4.getItem().dispose();
					item4.dispose();
					bar3.dispose();
					item3.dispose();
					bar2.dispose();
					item2.dispose();
					table.redraw();
				}

			} else {
				bar4.dispose();
				editor4.getItem().dispose();
				item4.dispose();
				bar3.dispose();
				item3.dispose();
				bar2.dispose();
				item2.dispose();
				bar1.dispose();
				item1.dispose();
				table.redraw();
			}
			table.getColumn(2).setWidth(40);
			table.getColumn(1).setWidth(120);
			table.redraw();

		}
	}

	/**
	 * Adds related Files to a list called realatedFileNames and the adds the
	 * relevance for that file to the relatedFileRelevance at the same index.
	 * 
	 * @param file
	 *            the file
	 * @param time
	 *            the time at which to search for relatedfiles
	 * @param relatedFileNames
	 *            the names of any relatedfiles found so far
	 * @param relatedFileRelevance
	 *            the relevance of any relatedfiles found so far
	 */
	public static void calulateRelations(String file, String time,
			List<String> relatedFileNames, List<Integer> relatedFileRelevance) {

		int index;
		if (!iface
				.execQuery("SELECT file, count(file) FROM eclipse_ProjectTable WHERE modified - 500 < '"
						+ time.replace("-", "").replace(" ", "").replace(":",
								"").replace(".0", "")
						+ "' AND modified + 500 > '"
						+ time.replace("-", "").replace(" ", "").replace(":",
								"").replace(".0", "")
						+ "' AND file !='"
						+ file
						+ "' Group By file ;")) {
			return;
		}

		try {
			CachedRowSetImpl crs = new CachedRowSetImpl();
			crs = iface.getRowSet();
			while (crs.next()) {

				index = relatedFileNames.indexOf(crs.getString(1));

				if (index == -1) {
					relatedFileNames.add(crs.getString(1));
					relatedFileRelevance.add(crs.getInt(2));
				} else {
					relatedFileRelevance.set(index, (relatedFileRelevance
							.get(index) + crs.getInt(2)));
				}

			}
		} catch (SQLException se) {
			se.printStackTrace();
		}
	}

	/**
	 * Returns a list of times at which file was modified
	 * 
	 * @return a list of times at which file was modified
	 * @param file
	 *            the file
	 */
	public static List<String> getFileModificationTimes(String file) {

		List<String> modifiedtimes = new ArrayList<String>();

		if (!iface
				.execQuery("SELECT modified FROM eclipse_ProjectTable WHERE file = '"
						+ file + "';")) {
			return null;
		}
		try {
			CachedRowSetImpl crs = new CachedRowSetImpl();
			crs = iface.getRowSet();
			while (crs.next()) {
				modifiedtimes.add(crs.getString(1));
			}
		} catch (SQLException se) {
			se.printStackTrace();
		}
		return modifiedtimes;

	}
}