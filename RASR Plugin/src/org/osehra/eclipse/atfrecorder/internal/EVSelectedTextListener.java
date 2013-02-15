package org.osehra.eclipse.atfrecorder.internal;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

public class EVSelectedTextListener implements Listener {
	
	public int getPreviousStart() {
		return previousStart;
	}

	public void setPreviousStart(int previousStart) {
		this.previousStart = previousStart;
	}

	public int getPreviousEnd() {
		return previousEnd;
	}

	public void setPreviousEnd(int previousEnd) {
		this.previousEnd = previousEnd;
	}

	public boolean isMultiSelectMode() {
		return multiSelectMode;
	}

	public void setMultiSelectMode(boolean multiSelectMode) {
		this.multiSelectMode = multiSelectMode;
	}

	private ScreenStateSourceProvider screenStateService;
	private boolean multiSelectMode = false;
	private int previousStart;
	private int previousEnd;


	public EVSelectedTextListener(ScreenStateSourceProvider screenStateService) {
		super();
		System.out.println("creating EVSelcectedTextListener");
		this.screenStateService = screenStateService;
	}

	@Override
	public void handleEvent(Event event) {
		StyledText text = (StyledText) event.widget;
		String selected = text.getSelectionText();

		//validations...
		// did user deselect?
		// is selected free of newlines?
		if (selected.length() == 0 || selected.contains("\r")
				|| selected.contains("\n")) {
			text.setSelection(text.getCaretOffset(), text.getCaretOffset());
			return;
		}
		
		int start, end;
		//need to determine if user selected from left-right or right-left
		int caretPos = text.getCaretOffset();
		int selLength = selected.length();
		boolean leftToRight;
		//check for out of bounds
		if (caretPos - selLength < 0)
			leftToRight = false;
		else if (caretPos + selLength >= text.getCharCount())
			leftToRight = true;
		else {
			//check for left to right select
			String leftRightText = text.getText(caretPos - selLength, caretPos - 1);
			if (leftRightText.equals(selected))
				leftToRight = true;
			else
				leftToRight = false;
		}
		
		if (leftToRight) {
			start = text.getCaretOffset() - selected.length();
			end = text.getCaretOffset();
		} else {
			start = text.getCaretOffset();
			end = text.getCaretOffset() + selected.length();
		}
		
		//validate the user isn't selecting into the current one 
		//and that the current selection is after the previous
		// + 1, do not want the next value to touch the previous one //TODO: note, would be easy to concatenate to the existing string value
		if (multiSelectMode && Math.max(previousStart, previousEnd) + 1 > Math.min(start,end)) {
			text.setSelection(text.getCaretOffset(), text.getCaretOffset());
			return;
		}
		
		if (!multiSelectMode) {
			StyleRange[] ranges = {new StyleRange(0, text.getCharCount(), null, null) }; //clear previous			
			text.setStyleRanges(ranges);
		}
		
        StyleRange style = new StyleRange();
        style.borderColor = Display.getDefault().getSystemColor(SWT.COLOR_RED);
        style.borderStyle = SWT.BORDER_SOLID;
        style.start = start;
        style.length = Math.max(start - end, end - start);
        text.setStyleRange(style);
        
        previousStart = start;
        previousEnd = end;
        
        //TODO: return to single value select mode after pressing enter? or disable selecting last 20 if in multi select mode

        text.setSelection(end, end);
        
        if (!multiSelectMode)
        	screenStateService.resetSelected();
		screenStateService.addSelected(selected);
	}

}
