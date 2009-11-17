package net.sourceforge.synergy.codecalendar;

import java.sql.SQLException;
import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.List;

import net.sourceforge.synergy.Synergy;
import net.sourceforge.synergy.UtilityFunctions;
import net.sourceforge.synergy.data.SQLInterface;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.LineStyleEvent;
import org.eclipse.swt.custom.LineStyleListener;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.custom.TableCursor;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableItem;

import com.sun.rowset.CachedRowSetImpl;

public class CodeCalendar {

	private static Shell codewindow = new Shell(UtilityFunctions.window);

	private static CalendarTable table;

	private static SQLInterface iface = Synergy.getDefault().getDatabase();

	public static DayDataSet monthDays[]; // Record of all the months data

	/*
	 * The constructor
	 * 
	 */
	public CodeCalendar() {
		Display.getDefault().asyncExec(new Runnable() {
			// not async...

			public void run() {

				createCodeCalWindow();
				codewindow.open();

				while (!codewindow.isDisposed()) {
					if (!Display.getDefault().readAndDispatch())
						Display.getDefault().sleep();
				}
			}

		});
	}

	
	// It's ridiculous java doesn't support this function
	public String getMonthFromInt(int month) {
		return new DateFormatSymbols().getMonths()[month-1];
	}	

	private void createCodeCalWindow() {
		codewindow.setLayout(new FillLayout());
		codewindow.setText("Code Calendar");
		codewindow.setSize(620, 650);

		FillLayout fill = new FillLayout(SWT.VERTICAL);
		fill.spacing = 15;
		fill.marginWidth = 15;
		fill.marginHeight = 15;
		codewindow.setLayout(fill);

		table = new CalendarTable(codewindow, SWT.MULTI
				| SWT.FULL_SELECTION | SWT.BORDER);

		final StyledText dataDisplay = new StyledText(codewindow,
				SWT.MULTI | SWT.V_SCROLL | SWT.BORDER | SWT.WRAP);
		dataDisplay.addLineStyleListener(new LineStyleListener() {
			public void lineGetStyle(LineStyleEvent event) {
				List<StyleRange> styles = new ArrayList<StyleRange>();
				Color blue = codewindow.getDisplay().getSystemColor(
						SWT.COLOR_BLUE);
				/* List of files changed: */
				styles.add(new StyleRange(0, event.lineText
						.indexOf(">"), blue, null, SWT.BOLD));

				/* style-ise everything up to the first ':' point */
				styles.add(new StyleRange(event.lineOffset,
						event.lineText.indexOf(":") + 1, blue, null,
						SWT.ITALIC));
				event.styles = (StyleRange[]) styles
						.toArray(new StyleRange[0]);
			}

		});
		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		final TableCursor cursor = new TableCursor(table, SWT.NONE);
		cursor.setBackground(new Color(codewindow.getDisplay(), 30,
				145, 200));

		cursor.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent se) {
				// table.deselectAll();
				TableItem row = cursor.getRow();
				try {
					final String selectedDay = row.getText(cursor
							.getColumn());
					dataDisplay.setText(monthDays[Integer
							.parseInt(selectedDay)].getDayData(Integer
							.parseInt(selectedDay)));
				} catch (NumberFormatException e) {
					dataDisplay
							.setText("< No data available for this day in "
									+ getMonthFromInt(CalendarTable.currentMonth)
									+ ". >");
				}
			}

			public void widgetDefaultSelected(SelectionEvent se) {
				// table.deselectAll();
			}
		});

		codewindow.addControlListener(new ControlAdapter() {
			public void controlResized(ControlEvent e) {
				Rectangle area = codewindow.getClientArea();
				Point preferredSize = table.computeSize(SWT.DEFAULT,
						SWT.DEFAULT);
				int width = area.width - 2 * table.getBorderWidth();
				if (preferredSize.y > area.height
						+ table.getHeaderHeight()) {

					Point vBarSize = table.getVerticalBar().getSize();
					width -= vBarSize.x;
				}
				Point oldSize = table.getSize();
				table.scaleColumns(width);
				if (oldSize.x > area.width)
					table.setSize(area.width, area.height);

			}
		});
		table.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent se) {
				table.deselectAll();
			}

			public void widgetDefaultSelected(SelectionEvent se) {
				table.deselectAll();
			}
		});
		table.setLayoutData(new GridData(GridData.BEGINNING));
		table.setLinesVisible(true);
		table.setCapture(false);

		dataDisplay.setEditable(false);
		dataDisplay.setLayoutData(new GridData(
				GridData.VERTICAL_ALIGN_FILL));
		dataDisplay
		.setText("< Please click on a date to view further information. >");
		final Combo months = new Combo(codewindow, SWT.HORIZONTAL
				| SWT.READ_ONLY);

		for (int t = 1; t <= 12; t++)
			// Add the 12 months of the year
			months.add(getMonthFromInt(t));

		months.select(java.util.Calendar.MONTH); // Set default to
													// this month

		months.setLayoutData(new GridData(GridData.BEGINNING,
				GridData.FILL_HORIZONTAL, true, true));

		months.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent se) {
				CalendarTable.currentMonth = months.getSelectionIndex() + 1;
				getData(CalendarTable.currentMonth);
				table.addTableRows();
				cursor.setSelection(1,1);
				dataDisplay
						.setText("< Please click on a date to view further information. >");
			}
			public void widgetDefaultSelected(SelectionEvent se) {
			}
		});

		/* Select a month on load */
		CalendarTable.currentMonth = months.getSelectionIndex() + 1;
		getData(CalendarTable.currentMonth);
		table.addTableRows();
	}
	

	public void open() {
		if(codewindow.isDisposed()) {
			codewindow = new Shell(UtilityFunctions.window);
			createCodeCalWindow();
			codewindow.open();
		}
		
	}
	/**
	 * Views retreives data for a set month from the database.
	 * 
	 * @param month to retreive
	 */
	public void getData(int month) {
		if (!iface.execQuery("SELECT DISTINCT file,project, username, "
				+ "DATE_FORMAT(eclipse_ProjectTable.modified,'%d') AS theDay "
				+ "FROM eclipse_ProjectTable WHERE "
				+ "(DATE_FORMAT(eclipse_ProjectTable.modified,'%m') = " + month
				+ ") GROUP BY modified ORDER BY username DESC;")) {

		}
		try {
			CachedRowSetImpl crs = new CachedRowSetImpl();
			crs = iface.getRowSet();
			monthDays = new DayDataSet[32];
			for (int i = 0; i <= 31; i++)
				monthDays[i] = new DayDataSet();
			while (crs.next()) {
				monthDays[crs.getInt(4)].addDay(new DayData(crs.getString(1),
						crs.getString(2), crs.getString(3)));
			}
		} catch (SQLException se) {
			se.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		codewindow.setText("Code Calendar - Displaying code information for "
				+ getMonthFromInt(month) + ".");
	}
}
