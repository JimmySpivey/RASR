package org.osehra.eclipse.atfrecorder.internal;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.swt.widgets.Menu;
import org.osehra.eclipse.atfrecorder.ATFRecorderAWT;
import org.osehra.eclipse.atfrecorder.codegen.ATFCodeGenerator;
import org.osehra.python.codegen.LineNotFoundException;

import com.jcraft.eclipse.jcterm.IUIConstants;
import com.jcraft.eclipse.jcterm.JCTermPlugin;
import com.jcraft.eclipse.jcterm.JCTermView;

public class SaveTestAction extends Action {

	private IAction saveTest;

	public SaveTestAction(final ATFRecorderAWT atfRecorderAwt) {
		super();
		
		saveTest = new Action() {
			public void run() {
				
				//TODO: come up with a preferences (singleton) object for RASR
				String atfLocation = JCTermPlugin.getDefault().getValue("PREF/ATF-LOC");
				
				//TODO: load these parms from user dialog prompt before tests are ran
				try {
					new ATFCodeGenerator().addTestToATF(atfRecorderAwt.getRecordableEvents(), "ssh_connect_demo", "rasr_test_"+((int)(Math.random()*1000)), atfLocation, false);
					atfRecorderAwt.resetRecorder();
				} catch (FileNotFoundException e) {
					// TODO Should inform user if File isn't found.
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Log error, display general error has occured message to user
					e.printStackTrace();
				} catch (LineNotFoundException e) {
					// TODO should inform user about malformed python file
					e.printStackTrace();
				}
			}
		};
		saveTest.setText("Save Test");

		setText("Save Test");
		setToolTipText("Save Test");
		setImageDescriptor(JCTermPlugin
				.getImageDescriptor(IUIConstants.IMG_SAVEADD16));
	}

	public void run() {
		saveTest.run();
	}

	public void dispose() {
	}

	public Menu getMenu(Menu parent) {
		return null;
	}
}
