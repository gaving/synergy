package net.sourceforge.synergy.perspective;

import org.eclipse.ui.*;

public class SynergyPerspective implements IPerspectiveFactory {

	public SynergyPerspective() {
		super();
	}

	public void createInitialLayout(IPageLayout layout) {
		defineActions(layout);
		defineLayout(layout);
	}

	public void defineActions(IPageLayout layout) {
		layout.addNewWizardShortcut("org.eclipse.ui.wizards.new.folder");
		layout.addNewWizardShortcut("org.eclipse.ui.wizards.new.file");
		layout.addShowViewShortcut(IPageLayout.ID_RES_NAV);
		layout.addShowViewShortcut(IPageLayout.ID_BOOKMARKS);
		layout.addShowViewShortcut(IPageLayout.ID_OUTLINE);
		layout.addShowViewShortcut(IPageLayout.ID_PROP_SHEET);
		layout.addShowViewShortcut(IPageLayout.ID_TASK_LIST);
	}

	public void eventLoopException(Throwable ex) {

	}

	public void defineLayout(IPageLayout layout) {
		String editorArea = layout.getEditorArea();

		/* place navigator and outline to left of editor area. */
		IFolderLayout left = layout.createFolder("left", IPageLayout.LEFT,
				(float) 0.26, editorArea);
		left.addView(IPageLayout.ID_RES_NAV);
		left.addView(IPageLayout.ID_OUTLINE);

		IFolderLayout bottomleft = layout.createFolder("bottomleft",
				IPageLayout.BOTTOM, (float) 0.5, "left");
		bottomleft.addView("net.sourceforge.synergy.views.DeveloperView");
		bottomleft.addView("net.sourceforge.synergy.views.FileHistoryView");
		bottomleft.addView("net.sourceforge.synergy.views.ChatView");

		IFolderLayout topright = layout.createFolder("topright",
				IPageLayout.RIGHT, (float) 0.95, editorArea);

		IFolderLayout bottom = layout.createFolder("bottom",
				IPageLayout.BOTTOM, (float) 0.80, editorArea);

		topright.addView("net.sourceforge.synergy.views.LineVisualiserView");
		bottom.addView("org.eclipse.ui.console.ConsoleView");
		bottom.addView(IPageLayout.ID_PROBLEM_VIEW);
		bottom.addView(IPageLayout.ID_TASK_LIST);
		bottom.addView("org.eclipse.pde.runtime.LogView"); //$NON-NLS-1$
		
	}

}