package org.osehra.eclipse.atfrecorder.actions;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.osehra.eclipse.atfrecorder.RASRPreferences;

//TODO: would be nice to move this to a rich preferences window
public class SetATFDirAction extends Action {

	private IAction prefAction;
	private RASRPreferences preferences = RASRPreferences.getInstance();

	public SetATFDirAction() {
		super();
		

		prefAction = new Action() {
			public void run() {
				Display display = Display.getDefault();
				Shell shell = new Shell(display);
				DirectoryDialog dd = new DirectoryDialog(shell);
				dd.setText("ATF Location");
				dd.setMessage("Select the root directory of the ATF. EG: /home/user/work/OSEHRA-Automated-Testing/");
				
				String directory = dd.open(); //null if the user fails to select a directory.
				
				if (directory == null)
					return;
					
				List<File> checkDirs = new ArrayList<File>(10);
				checkDirs.add(getDirPath(directory, "Packages"));
				checkDirs.add(getDirPath(directory, "Python", "vista"));
				
				boolean isValid = true;
				for (File f : checkDirs) {
					isValid = isValid && f.exists() && f.isDirectory();
				}
				
				if (isValid) {
					preferences.saveValue(RASRPreferences.ATF_LOCATION, directory);
					MessageDialog.openInformation(Display.getDefault().getActiveShell(), 
							"ATF Location set", 
							"ATF location updated succesfully. RASR will create and modify tests at the specified ATF when the Save Test button is clicked.");
				} else {
					MessageDialog.openWarning(shell, "Could not set ATF Location", 
							"The location specified (" +directory+ ") is not a valid Automated Testing Framework directory.");
				}
			}

			private File getDirPath(String directory, String... dirs) {
				String sep = System.getProperty("file.separator");
				String path = sep;
				for (String dir : dirs)
					path += dir + sep;
				return new File(directory+path);
			}
		};
		prefAction.setText("Preferences");

		setText("Set ATF Location");
		setToolTipText("Set ATF Location");
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
