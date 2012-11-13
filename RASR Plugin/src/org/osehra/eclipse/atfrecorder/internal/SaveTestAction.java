package org.osehra.eclipse.atfrecorder.internal;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.osehra.eclipse.atfrecorder.ATFRecorderAWT;
import org.osehra.eclipse.atfrecorder.RASRPreferences;
import org.osehra.eclipse.atfrecorder.RecordableEvent;
import org.osehra.eclipse.atfrecorder.codegen.ATFCodeGenerator;
import org.osehra.python.codegen.LineNotFoundException;

import com.jcraft.eclipse.jcterm.IUIConstants;
import com.jcraft.eclipse.jcterm.JCTermPlugin;

public class SaveTestAction extends Action {

	private RASRPreferences preferences = RASRPreferences.getInstance();
	private IAction saveTest;

	public SaveTestAction(final ATFRecorderAWT atfRecorderAwt) {
		super();
		
		saveTest = new Action() {
			@SuppressWarnings("restriction")
			public void run() {
				
				String atfLocation = preferences.getValue(RASRPreferences.ATF_LOCATION);

				try {
					List<RecordableEvent> recordableEvents = atfRecorderAwt.getRecordableEvents();
					if (recordableEvents == null || recordableEvents.isEmpty()) {
						MessageDialog.openWarning(Display.getDefault().getActiveShell(), 
								"Unable to Save", 
								"Nothing has been recorded");
						return;
					}
					
					SaveTestDialog dialog = new SaveTestDialog(null);
					dialog.open();
					
					String packageName = dialog.getPackageName();
					String testSuite = dialog.getTestSuite();
					String testName = dialog.getTestName();
					
					if (packageName == null || testName == null || testSuite == null) {
						return;
					}
					
					preferences.saveValue(RASRPreferences.PACKAGE_NAME, dialog.getPackageName());
					preferences.saveValue(RASRPreferences.TEST_SUITE_NAME, dialog.getTestSuite());
					
					//turns off saving files, delete later
//					if (1 == 1)
//					return;
					
					String testSuiteDirectory = new ATFCodeGenerator().addTestToATF(
							recordableEvents,
							dialog.getPackageName(),
							dialog.getTestSuite(),
							dialog.getTestName(),
							atfLocation,
							dialog.isNewPackage(),
							dialog.isNewSuite()); 
					atfRecorderAwt.resetRecorder();
					
					GenericNotificationPopup popup = new GenericNotificationPopup(Display.getDefault(), 
							"Test Saved", 
							"Test saved to " + testSuiteDirectory);
					popup.create();
					popup.open();
				} catch (FileNotFoundException e) {
					MessageDialog.openError(Display.getDefault().getActiveShell(), 
							"File Not Found", 
							"Attempted to save test, however the file was not found. Please check that the ATF located at " +atfLocation+ " is valid.");
				} catch (IOException e) {
					MessageDialog.openError(Display.getDefault().getActiveShell(), 
							"IO Exception", 
							e.getLocalizedMessage());
				} catch (LineNotFoundException e) {
					MessageDialog.openError(Display.getDefault().getActiveShell(), 
							"Python Code Generation Exception", 
							"Error occured while editing the python script. Please check that the script is still valid for editing.");
				} catch (IllegalStateException e) {
					MessageDialog.openError(Display.getDefault().getActiveShell(), 
							"IllegalStateException??", 
							e.getLocalizedMessage());
				} catch (URISyntaxException e) {
					MessageDialog.openError(Display.getDefault().getActiveShell(), 
							"Failed to load template", 
							"The templates required for creating new test files could not be found.");
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
