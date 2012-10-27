package org.osehra.eclipse.atfrecorder.internal;

import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;

public class EVSelectedTextListener implements Listener {
	
	private ScreenStateSourceProvider screenStateService;

	private String previousSelectedText;
	private int previousStart;
	private int previousEnd;
	private boolean newTextSelected;

	public EVSelectedTextListener(ScreenStateSourceProvider screenStateService) {
		super();
		System.out.println("creating EVSelcectedTextListener");
		this.screenStateService = screenStateService;
	}
	
	public String getPreviousSelectedText() {
		return previousSelectedText;
	}

	public void setPreviousSelectedText(String previousSelectedText) {
		this.previousSelectedText = previousSelectedText;
	}

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

	public boolean isNewTextSelected() {
		return newTextSelected;
	}

	@Override
	public void handleEvent(Event event) {
		Text text = (Text) event.widget;
		String selected = text.getSelectionText();
		//determine if it is selected from left-right or right-left
		
//		int start = text.getCaretPosition() + text.getCaretPosition(); // TODO: get from text
//		int end = text.getCaretPosition();
//
//		if (selected.equals(previousSelectedText) && start == previousStart
//				&& end == previousEnd) {
//			newTextSelected = false;
//			return;
//		} else {

			// further validations...

			// did user deselect?
			// is selected free of newlines?
			if (selected.length() == 0 || selected.contains("\r")
					|| selected.contains("\n")) {
				//override what the user selected
				newTextSelected = false;
				text.setSelection(previousStart, previousEnd);
				text.showSelection();
				return;
			}
			newTextSelected = true;
			previousSelectedText = selected;
			
			
			//need to determine if user selected from left-right or right-left
			int caretPos = text.getCaretPosition();
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
				previousStart = text.getCaretPosition() - selected.length();
				previousEnd = text.getCaretPosition();
			} else {
				previousStart = text.getCaretPosition();
				previousEnd = text.getCaretPosition() + selected.length();
			}

			
			screenStateService.setSelected(selected);
//		}
	}

}
