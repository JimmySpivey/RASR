package org.osehra.eclipse.atfrecorder.internal;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;

import com.jcraft.eclipse.jcterm.JCTermPlugin;

public class PreferencesAction extends Action {

	private IAction preferences;
	

	public PreferencesAction() {
		super();
		

		preferences = new Action() {
			public void run() {
				Display display = Display.getDefault();
				Shell shell = new Shell(display);
				DirectoryDialog dd = new DirectoryDialog(shell);
				dd.setText("ATF Location");
				dd.setMessage("Select the root directory of the ATF. EG: /home/user/work/OSEHRA-Automated-Testing/");
				
				String directory = dd.open(); //null if the user fails to select a directory.
				System.out.println(directory);
				
				if (directory != null) {
					JCTermPlugin.getDefault().saveValue("PREF/ATF-LOC", directory);
				}
				
			}
		};
		preferences.setText("Preferences");

		setText("Set ATF Location");
		setToolTipText("Set ATF Location");
	}

	public void run() {
		preferences.run();
	}

	public void dispose() {
	}

	public Menu getMenu(Menu parent) {
		return null;
	}
	
}
