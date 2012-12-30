package org.osehra.eclipse.atfrecorder.internal;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.swt.widgets.Menu;
import org.osehra.eclipse.atfrecorder.ATFRecorderAWT;

import com.jcraft.eclipse.jcterm.IUIConstants;
import com.jcraft.eclipse.jcterm.JCTermPlugin;

public class RecordingIconAction extends Action {
	
	private IAction recordingIcon;

	public RecordingIconAction(final ATFRecorderAWT term, final StopIconAction stopIcon) {
		
		recordingIcon = new Action() {
			public void run() {
				if (term.isRecordingEnabled())
					return; //maybe display an alert to the user?
				
				stopIcon.enable();				
				term.enableRecording();
			}
		};
				
		//disable();
		setImageDescriptor(JCTermPlugin
				.getImageDescriptor(IUIConstants.IMG_RECORDING));
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
