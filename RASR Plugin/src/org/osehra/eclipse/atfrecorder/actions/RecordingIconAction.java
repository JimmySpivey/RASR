package org.osehra.eclipse.atfrecorder.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.swt.widgets.Menu;
import org.osehra.eclipse.atfrecorder.ATFRecorderAWT;
import org.osehra.eclipse.atfrecorder.AVCodeStateEnum;

import com.jcraft.eclipse.jcterm.IUIConstants;
import com.jcraft.eclipse.jcterm.JCTermPlugin;

public class RecordingIconAction extends Action {
	
	private IAction recordingIcon;
	private ATFRecorderAWT term;
	
	public RecordingIconAction(final ATFRecorderAWT term) {
		super("Recording Icon", IAction.AS_CHECK_BOX);
		
		this.term = term;
		recordingIcon = new Action() { //TODO: need to synchronize thread, pressing recording button too rapidly fires off competing threads
			public void run() {
				
				//TODO: fix this to prevent user toggling
//				if (term.getAvCodeState() != AVCodeStateEnum.PROMPT_PASSED) {
//					return;
//				}

				if (term.isRecordingEnabled())
					toggleOff();
				else
					toggleOn();
				
				term.requestFocus(); //pressing the button the tool bar sometimes causes the AWT term window to lose its focus. this should resend focus back to the term
			}
		};
		
		setImageDescriptor(JCTermPlugin
				.getImageDescriptor(IUIConstants.IMG_RECORDING));
		toggleOn();
	}
	
	public void toggleOn() {
		setChecked(true);
		setText("Recording enabled");
		setToolTipText("Recording enabled");
		term.setRecordingEnabled(true);
	}
	
	public void toggleOff() {
		setChecked(false);
		setText("Recording disabled");
		setToolTipText("Recording disabled");
		term.setRecordingEnabled(false);
	}
	
	public void run() {
		recordingIcon.run();
	}

	public void dispose() {
	}

	public Menu getMenu(Menu parent) {
		return null;
	}

	public void enable() {
		setText("Recording");
		setToolTipText("Recording");
		setEnabled(true);
	}
	
	public void disable() {
		setText("Not Recording");
		setToolTipText("Not Recording");
		setEnabled(false);
	}
	
}
