package net.sourceforge.synergy.codecalendar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;


class DayData implements Comparable<DayData> {
	
	public String file;
	public String project;
	public String username;
	
	/**
	 * The Constructor
	 * 
	 * @param The name of the file.
	 * @param The name of the project.
	 * @param The username who made the change.
	 */
	public DayData(String theFile, String theProject, String theUsername) {
		this.file = theFile;
		this.project = theProject;
		this.username = theUsername;
	}
	
	public int compareTo(DayData aDay) {
		return this.username.compareToIgnoreCase(aDay.username);
	}
}

public class DayDataSet {
	
	private List<DayData> dayItems = new ArrayList<DayData>();
	
	/**
	 * Add a day item to the List.
	 * 
	 * @param The DayData to be added.
	 */
	public void addDay(DayData day) {
		dayItems.add(day);	
	}
	
	/**
	 * Checks if a day has any values in it.
	 * 
	 * @return Returns if the List is empty or not.
	 */
	public boolean isEmpty() {
		return (dayItems.size() == 0) ? true : false;
	}

	/**
	 * Gives a display of the current days data.
	 * 
	 * @param The day to display information on.
	 * 
	 * @return Returns a string representing the days details.
	 */
	public String getDayData(int day) {
		String output = "< List of files changed on the " + day + 
						"/" + CalendarTable.currentMonth + "/" + 
						CalendarTable.currentYear + " >\n";
		
		Collections.sort(dayItems);
		Iterator i = dayItems.iterator();
		while(i.hasNext())
		{
			DayData queriedDay = (DayData)i.next();
			output +=  "Username: " + queriedDay.username + "\nProject: " 
						+ queriedDay.project + "\nFile: " + queriedDay.file + "\n\n";
		}
		return output;
	}
	
}
