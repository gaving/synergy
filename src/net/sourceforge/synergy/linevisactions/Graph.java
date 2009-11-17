package net.sourceforge.synergy.linevisactions;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.FigureCanvas;
import org.eclipse.draw2d.FlowLayout;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.MarginBorder;
import org.eclipse.draw2d.Panel;
import org.eclipse.draw2d.RectangleFigure;
import org.eclipse.draw2d.XYLayout;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;

/**
 * Handles the drawing of the graph and contains related functions
 */

public abstract class Graph {

	private static Panel drawingwindow; // The drawing area

	private static Color primarycolour, secondarycolour, thecolour,
			tooltipcolour;

	private static int viewHeight;

	private static int barLengthScale = 2; // Number of pixels per change

	private static int barHeightScale;
	
	private static int CurrentFileNumofLines;

	/**
	 * Sets the views height
	 * @param theheight the height to set
	 */
	private static void setViewHeight(int theheight) {
		Graph.viewHeight = theheight - 100;
	}

	/**
	 * Removes the entire canvas area
	 */
	public static void clearCanvas() {
		try {
		drawingwindow.removeAll();
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Initializes the graph for a given control
	 * @param viewwindow the control 
	 */
	public static void init(Composite viewwindow) {
		primarycolour = new Color(viewwindow.getDisplay(), 0, 153, 255);
		secondarycolour = new Color(viewwindow.getDisplay(), 61, 177, 255);
		tooltipcolour = new Color(viewwindow.getDisplay(), 140, 210, 255);
		thecolour = primarycolour;
	}

	/**
	 * Sets the number of file lines
	 * @param number the number of lines
	 */
	public static void setNumberofLines(int number) {
		CurrentFileNumofLines = number;
	}

	/**
	 * Returns the number of lines in the current file
	 * @return the number of lines
	 */
	public static int getNumberofLines() {
		return CurrentFileNumofLines;
	}

	/**
	 * Scales the height of the graph
	 */
	public static void calculateHeightScale() {
		if (CurrentFileNumofLines > 0) {		
			barHeightScale = ((viewHeight) / CurrentFileNumofLines);
		}
	}

	/**
	 * Creates the actual drawing area for the graph
	 * @param viewwindow the control to draw on
	 * @param viewdimensions the dimensions of the canvas
	 */
	public static void createCanvas(Composite viewwindow,
			Rectangle viewdimensions) {
		
		try {
			viewwindow.setLayout(new FillLayout());

			FigureCanvas canvas = new FigureCanvas(viewwindow);
			canvas.setVerticalScrollBarVisibility(0);
			
			drawingwindow = new Panel();
			drawingwindow.setLayoutManager(new XYLayout());
			canvas.setBackground(ColorConstants.white);
			canvas.setContents(drawingwindow);
			drawingwindow.setBounds(viewdimensions);
			resizeCanvas(viewdimensions);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Handles the resizing of a canvas
	 * @param viewdimensions the new dimensions
	 */
	public static void resizeCanvas(Rectangle viewdimensions) {
		try {
			drawingwindow.setBounds(viewdimensions);
			setViewHeight(viewdimensions.height);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Handles the tooltip of an individual line
	 * @param code the line
	 * @return a figure containing the information
	 */
	public static IFigure createTooltip(EditorLine code) {
		Panel tooltip;
			tooltip = new Panel();
			try {

			tooltip.setLayoutManager(new FlowLayout());
			tooltip.setBounds(new Rectangle(0, 0, 100, 100));
			tooltip.setBackgroundColor(tooltipcolour);
			tooltip.setBorder(new MarginBorder(1));

			Label linenum = new Label();
			linenum.setText("Line: " + EditorBook.getCurrentPage().findLine(code) + "\nNumber of changes: "
					+ code.numofchanges);
			tooltip.add(linenum);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return (IFigure) tooltip;
	}

	/**
	 * Draws an individual bar for a line of code
	 * @param code the line to draw
	 */
	public static void drawSingleBar(EditorLine code) {
		
		try {
			calculateHeightScale();
			int barLength = (code.numofchanges * barLengthScale);
			RectangleFigure rectFigure = new RectangleFigure();
			rectFigure.setFill(true);
			rectFigure.setToolTip(createTooltip(code));
			
			thecolour = ((EditorBook.getCurrentPage().findLine(code) % 2) == 0) 
			? primarycolour : secondarycolour;
			
			rectFigure.setBackgroundColor(thecolour);
			rectFigure.setOutline(false);
			rectFigure.setLineStyle(SWT.LINE_SOLID);
			rectFigure.setOpaque(true); // no border

			int VertPosition = (EditorBook.getCurrentPage().findLine(code) * barHeightScale) - barHeightScale;
			drawingwindow.add(rectFigure, new Rectangle(0, VertPosition, barLength,
					barHeightScale));
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}