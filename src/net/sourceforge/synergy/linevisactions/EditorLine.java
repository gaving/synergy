package net.sourceforge.synergy.linevisactions;

import org.eclipse.draw2d.IFigure;

/**
 * Describes what is considered an editor line
 */

public class EditorLine {

	public String code;
	public int numofchanges = 1;
	public IFigure bar;
	
	public EditorLine() {
		
	}
	
	public EditorLine(String inputcode, int changes) {
		this.code=inputcode;
		this.numofchanges = changes;
	}
	
	public String toString()
	{
		return "Line: " + EditorBook.getCurrentPage().findLine(this) + ", Number of changes: " + this.numofchanges + ", "
		+ "Code: '" + this.code + "'.";
	}
	
}
