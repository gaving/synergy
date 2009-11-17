package net.sourceforge.synergy.codecalendar;
import java.util.GregorianCalendar;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

public class CalendarTable extends Table {

	public static int currentMonth;
	public static int currentYear = 2006;
	private static final String noData = "-";
	
	/**
	 * The constructor
	 * @param The controls parent container.
	 * @param The style of the control.
	 */
	public CalendarTable(Composite c, int s) {
		super(c, s);
		addTableColumns();
	}

	TableItem row1 = new TableItem(this, SWT.CENTER), row2 = new TableItem(
			this, SWT.NONE), row3 = new TableItem(this, SWT.NONE),
			row4 = new TableItem(this, SWT.NONE), row5 = new TableItem(this,
					SWT.NONE), row6 = new TableItem(this, SWT.NONE);

	TableColumn col1 = new TableColumn(this, SWT.CENTER),
			col2 = new TableColumn(this, SWT.CENTER), col3 = new TableColumn(
					this, SWT.CENTER),
			col4 = new TableColumn(this, SWT.CENTER), col5 = new TableColumn(
					this, SWT.CENTER),
			col6 = new TableColumn(this, SWT.CENTER), col7 = new TableColumn(
					this, SWT.CENTER);

	private void addTableColumns() {
		col1.setText("Monday");
		col2.setText("Tuesday");
		col3.setText("Wednesday");
		col4.setText("Thursday");
		col5.setText("Friday");
		col6.setText("Saturday");
		col7.setText("Sunday");
	}

	protected void checkSubclass() {
		// allows the table component to be sub-classed.
	}

	/**
	 * Scales and rescales the calendars columns.
	 * 
	 * @param Current width of the table.
	 */
	public void scaleColumns(int width) {
		col1.setWidth(width / 7 - 8);
		col2.setWidth(width / 7 - 8);
		col3.setWidth(width / 7 - 8);
		col4.setWidth(width / 7 - 8);
		col5.setWidth(width / 7 - 8);
		col6.setWidth(width / 7 - 8);
		col7.setWidth(width / 7 - 8);
	}

	/**
	 * Gives the numerical value of a selected month.
	 * 
	 * @param The month to return the Calendars numerical value.
	 * 
	 * @return returns the Calendar integer representation of the month.
	 */
	public int selectedMonth(int inputMonth) {
		return (inputMonth == 1) ? java.util.Calendar.JANUARY
				: (inputMonth == 1) ? java.util.Calendar.JANUARY
						: (inputMonth == 2) ? java.util.Calendar.FEBRUARY
								: (inputMonth == 3) ? java.util.Calendar.MARCH
										: (inputMonth == 4) ? java.util.Calendar.APRIL
												: (inputMonth == 5) ? java.util.Calendar.MAY
														: (inputMonth == 6) ? java.util.Calendar.JUNE
																: (inputMonth == 7) ? java.util.Calendar.JULY
																		: (inputMonth == 8) ? java.util.Calendar.AUGUST
																				: (inputMonth == 9) ? java.util.Calendar.SEPTEMBER
																						: (inputMonth == 10) ? java.util.Calendar.OCTOBER
																								: (inputMonth == 11) ? java.util.Calendar.NOVEMBER
																										: java.util.Calendar.DECEMBER;
	}

	/**
	 * Adds all the days of the month to the table.
	 * 
	 */
	public void addTableRows() {

		java.util.GregorianCalendar date2 = new GregorianCalendar(currentYear,
				selectedMonth(currentMonth), 1);
		
		// Draw the first row
		int currentDay = 1;
		String[] firstRow = new String[8];
		for (int i = 2; i <= 9; i++) {
			if (i < date2.get(java.util.Calendar.DAY_OF_WEEK))
				firstRow[i - 2] = " ";
			else {
				if(CodeCalendar.monthDays[currentDay].isEmpty())
					firstRow[i - 2] = ""  + noData;
				else
					firstRow[i - 2] = "" + currentDay;
				currentDay++;
			}
		}
		currentDay--;
		row1.setText(firstRow);
		int maxDay = date2.getActualMaximum(java.util.Calendar.DAY_OF_MONTH);
		String[] nextRow = new String[8];
		for (int p = 0; p <= 6; p++)
			if (currentDay <= maxDay)
				if(CodeCalendar.monthDays[currentDay].isEmpty())
					nextRow[p] = ""  + noData;
				else
				nextRow[p] = "" + currentDay++;
		row2.setText(nextRow);

		for (int p = 0; p <= 6; p++)
			if (currentDay <= maxDay)
				if(CodeCalendar.monthDays[currentDay].isEmpty())
					nextRow[p] = ""  + noData;
				else
				nextRow[p] = "" + currentDay++;
		row3.setText(nextRow);

		for (int p = 0; p <= 6; p++)
			if (currentDay <= maxDay)
				if(CodeCalendar.monthDays[currentDay].isEmpty()) {
					nextRow[p] = ""  + noData;
					 currentDay++;
				}else{
				nextRow[p] = "" + currentDay++;
				}
			else
				nextRow[p] = " ";
		row4.setText(nextRow);

		for (int p = 0; p <= 6; p++)
			if (currentDay <= maxDay)
				if(CodeCalendar.monthDays[currentDay].isEmpty()) {
					nextRow[p] = ""  + noData;
					 currentDay++;
				}else{
				nextRow[p] = "" + currentDay++;
				}
			else
				nextRow[p] = " ";
		row5.setText(nextRow);

		for (int p = 0; p <= 6; p++)
			if (currentDay <= maxDay)
				if(CodeCalendar.monthDays[currentDay].isEmpty()) {
					nextRow[p] = ""  + noData;
					 currentDay++;
				}else{
				nextRow[p] = "" + currentDay++;
				}
			else
				nextRow[p] = " ";
		row6.setText(nextRow);
	}

}
