package org.osehra.eclipse.atfrecorder.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.osehra.eclipse.atfrecorder.internal.EVSelectedTextListener;
import org.osehra.eclipse.atfrecorder.internal.ScreenStateSourceProvider;
import org.osehra.eclipse.atfrecorder.views.ExpectedValueView;

import com.jcraft.eclipse.jcterm.IUIConstants;
import com.jcraft.eclipse.jcterm.JCTermPlugin;

public class MultiSelectToggleAction extends Action {
	
	private Action mutliSelectIcon;

	public MultiSelectToggleAction(final EVSelectedTextListener evListener,
			final ScreenStateSourceProvider selectedTextProvider,
			final StyledText text) {
		super("Mutli Select Toggle", IAction.AS_CHECK_BOX);

		mutliSelectIcon = new Action() { // maybe need to synchronize thread,
											// pressing recording button too
											// rapidly fires off competing
											// threads?
			public void run() {

				// clear current screen selections
				StyleRange[] ranges = { new StyleRange(0, text.getCharCount(),
						null, null) }; // clear previous
				text.setStyleRanges(ranges);
				// reset ev listener state
				evListener.setPreviousStart(0);
				evListener.setPreviousEnd(0);
				// toggle ev listener mode
				evListener.setMultiSelectMode(!evListener.isMultiSelectMode());
				// reset values
				selectedTextProvider.resetSelected();
				
				//if toggling off multi-select, then select last 20 again
				if (!evListener.isMultiSelectMode()) {
					
					//TODO: Copy and pasted code, needs to be refactored...
					String evContents = text.getText();
					//obtain last valid line's contents.
					String lastLine = text.getLine(text.getLineCount() - 1).trim();
					for (int i = text.getLineCount() - 1; lastLine.length() == 0; i--) {
						if (i == -1)
							return;
						lastLine = text.getLine(i).trim();
					}
					
					//search for this line starting from the end
					int start = evContents.lastIndexOf(lastLine);
					int length = lastLine.length();
					
					text.setTopIndex(text.getLineCount() - 1); //causes the rolling text to automatically scroll down
					
			        StyleRange style = new StyleRange();
			        style.borderColor = Display.getDefault().getSystemColor(SWT.COLOR_RED);
			        style.borderStyle = SWT.BORDER_SOLID;
			        style.start = start;
			        style.length = length; //Math.max(start - end, end - start);
			        text.setStyleRange(style);
					
					String selected = evContents.substring(start, start+length);
					selectedTextProvider.resetSelected();
					selectedTextProvider.addSelected(selected);
					//TODO: Copy and pasted code, needs to be refactored...
				}
			}
		};

		setImageDescriptor(JCTermPlugin
				.getImageDescriptor(IUIConstants.IMG_MULTI_SELECT));
	}

	public void run() {
		mutliSelectIcon.run();
	}

	public void dispose() {
	}

	public Menu getMenu(Menu parent) {
		return null;
	}
}
