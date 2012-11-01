package org.osehra.eclipse.atfrecorder.internal;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.osehra.eclipse.atfrecorder.ATFRecorderAWT;
import org.osehra.eclipse.atfrecorder.codegen.ATFCodeGenerator;
import org.osehra.python.codegen.LineNotFoundException;

import com.jcraft.eclipse.jcterm.IUIConstants;
import com.jcraft.eclipse.jcterm.JCTermPlugin;

public class SaveTestAction extends Action {

	private IAction saveTest;

	public SaveTestAction(final ATFRecorderAWT atfRecorderAwt) {
		super();
		
		saveTest = new Action() {
			@SuppressWarnings("restriction")
			public void run() {
				
				//TODO: come up with a preferences (singleton) object for RASR
				String atfLocation = JCTermPlugin.getDefault().getValue("PREF/ATF-LOC");

				//TODO: load these parms from user dialog prompt before tests are ran
				try {
					String testSuiteDirectory = new ATFCodeGenerator().addTestToATF(atfRecorderAwt.getRecordableEvents(), "ssh_connect_demo", "rasr_test_"+((int)(Math.random()*1000)), atfLocation, false);
					atfRecorderAwt.resetRecorder();
					
					GenericNotificationPopup popup = new GenericNotificationPopup(Display.getDefault(), 
							"Test Saved", 
							"Test saved to " + testSuiteDirectory);
					popup.create();
					popup.open();
				} catch (FileNotFoundException e) {
					e.printStackTrace();
					MessageDialog.openError(Display.getDefault().getActiveShell(), 
							"File Not Found", 
							"Attempted to save test, however the file was not found. Please check that the ATF located at " +atfLocation+ " is valid.");
				} catch (IOException e) {
					e.printStackTrace();
					MessageDialog.openError(Display.getDefault().getActiveShell(), 
							"IO Exception", 
							e.getLocalizedMessage());
				} catch (LineNotFoundException e) {
					e.printStackTrace();
					MessageDialog.openError(Display.getDefault().getActiveShell(), 
							"Python Code Generation Exception", 
							"Error occured while editing the python script. Please check that the script is still valid for editing.");
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
