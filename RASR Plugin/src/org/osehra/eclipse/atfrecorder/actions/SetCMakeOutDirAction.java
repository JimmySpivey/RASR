package org.osehra.eclipse.atfrecorder.actions;

import java.io.File;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.osehra.eclipse.atfrecorder.RASRPreferences;

//this was a class for handling the "set ATF Location" menu item.
public class SetCMakeOutDirAction extends Action {

	private IAction prefAction;
	private RASRPreferences preferences = RASRPreferences.getInstance();

	public SetCMakeOutDirAction() {
		super();
		

		prefAction = new Action() {
			public void run() {
				Display display = Display.getDefault();
				Shell shell = new Shell(display);
				DirectoryDialog dd = new DirectoryDialog(shell);
				dd.setText("CMake Location");
				dd.setMessage("Select the root directory of cmake's build of the ATF.");
				
				String directory = dd.open(); //null if the user fails to select a directory.
				
					preferences.saveValue(RASRPreferences.CMAKE_OUT_LOCATION, directory);

					MessageDialog.openInformation(Display.getDefault().getActiveShell(), 
								"CMake Build Locationed set", 
								"CMake build will be ran each time a test is saved in this directory.");
				
			}
		};
		prefAction.setText("Preferences");

		setText("Set CMake Out Location");
		setToolTipText("Set CMake Out Location");
	}

	public void run() {
		prefAction.run();
	}

	public void dispose() {
	}

	public Menu getMenu(Menu parent) {
		return null;
	}
	
}
