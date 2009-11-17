package net.sourceforge.synergy.views;

import net.sourceforge.synergy.linevisactions.CodeWatcher;
import net.sourceforge.synergy.linevisactions.Graph;

import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

/**
 * LineVisualiserView details the most modified lines in the actively edited
 * file.
 */

public class LineVisualiserView extends ViewPart {

	private CodeWatcher codeAnalyser;

	public LineVisualiserView() {
		super();
	}

	public void setFocus() {

	}

	public void createPartControl(Composite parent) {
		parent.setLayout(new FillLayout());
		Graph.init(parent);
		Rectangle area = new Rectangle(0, 0, getSite().getShell()
				.getClientArea().width,
				getSite().getShell().getClientArea().height);
		Graph.createCanvas(parent, area);

		codeAnalyser = new CodeWatcher();
		// codeAnalyser.drawFullGraph(codeAnalyser.getActivePage());

		parent.addControlListener(new ControlAdapter() {
			public void controlResized(ControlEvent e) {
				Rectangle area = new Rectangle(0, 0, getSite().getShell()
						.getClientArea().width, getSite().getShell()
						.getClientArea().height);
				Graph.resizeCanvas(area);
				codeAnalyser.drawFullGraph(codeAnalyser.getActivePage());

			}
		});

	}

}