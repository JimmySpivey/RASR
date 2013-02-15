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
					int offset = text.getCharCount() - evContents.length();
					int start = offset+Math.max(1, evContents.length() - 21);
					int end = offset+evContents.length();
					String last20 = text.getText(start, end - 1); //for StyleText, end has to be -1. maybe buggy. Perhaps better to use getTextRange(start,lenght)
					for (int i = last20.length() - 1; i >= 0; i--) {
						if (last20.charAt(i) == '\r' || last20.charAt(i) == '\n' || last20.charAt(i) == ' ')
							end--;
						else
							break;
					}
					for (int i = last20.length() - 2 - (text.getCharCount() - end); i >= 0; i--) {
						if (last20.charAt(i) == '\r' || last20.charAt(i) == '\n'|| last20.charAt(i) == ' ') {
							start += i + 1;
							break;
						}
					}
					
					text.setTopIndex(text.getLineCount() - 1); //causes the rolling text to automatically scroll down
					
			        StyleRange style = new StyleRange();
			        style.borderColor = Display.getDefault().getSystemColor(SWT.COLOR_RED);
			        style.borderStyle = SWT.BORDER_SOLID;
			        style.start = start;
			        style.length = Math.max(start - end, end - start);
			        text.setStyleRange(style);
					
					String selected = evContents.substring(start, end);
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
