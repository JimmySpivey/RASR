package org.osehra.eclipse.atfrecorder.internal;

import java.io.File;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.osehra.eclipse.atfrecorder.RASRPreferences;

import com.jcraft.eclipse.jcterm.JCTermPlugin;

//TODO: should be refactored if the need for a preferences page/window happens.
//this is currently a class for handling the "set ATF Location" menu item.
public class PreferencesAction extends Action {

	private IAction prefAction;
	private RASRPreferences preferences = RASRPreferences.getInstance();

	public PreferencesAction() {
		super();
		

		prefAction = new Action() {
			@SuppressWarnings("restriction")
			public void run() {
				Display display = Display.getDefault();
				Shell shell = new Shell(display);
				DirectoryDialog dd = new DirectoryDialog(shell);
				dd.setText("ATF Location");
				dd.setMessage("Select the root directory of the ATF. EG: /home/user/work/OSEHRA-Automated-Testing/");
				
				String directory = dd.open(); //null if the user fails to select a directory.
				
				if (directory != null) {
					
					String sep = System.getProperty("file.separator");
					File f = new File(directory +sep+ "FunctionalTest"+sep+"RAS"+sep+"VistA-FOIA"+sep+"Packages"+sep);
					
					if (f.exists() && f.isDirectory()) {
						preferences.saveValue(RASRPreferences.ATF_LOCATION, directory);
						
//						GenericNotificationPopup popup = new GenericNotificationPopup(display, "ATF Location set", "ATF location updated succesfully. RASR will create and modify tests at the specified ATF when the Save Test button is clicked.");
//						popup.create();
//						popup.open();
						MessageDialog.openInformation(Display.getDefault().getActiveShell(), 
								"ATF Location set", 
								"ATF location updated succesfully. RASR will create and modify tests at the specified ATF when the Save Test button is clicked.");
					} else {
						MessageDialog.openWarning(shell, "Could not set ATF Location", "The location specified (" +directory+ ") is not a valid Automated Testing Framework directory.");
					}
					
					
				}
				
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
