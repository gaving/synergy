package net.sourceforge.synergy.codecalendar;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

public class CodeCalendarLauncher implements IWorkbenchWindowActionDelegate  {

	public CodeCalendarLauncher() {
		
	}
	
	public void run(IAction action) {
		new CodeCalendar().open();
	}
	
	public void selectionChanged(IAction action, ISelection selection) {
	}

	public void dispose() {
	}

	public void init(IWorkbenchWindow window) {
	}

}
